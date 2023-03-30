package com.github.gelald.batch.strategy;

import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.enums.ImportStrategyEnum;
import com.github.gelald.batch.factory.ImportStrategyFactory;
import com.github.gelald.batch.mapper.MaintainMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * for循环+MyBatis的save方法+事务+批量提交
 *
 * @author WuYingBin
 * date: 2022/12/26
 */
@Component
public class MyBatisBatchStrategy extends AbstractImportStrategy {
    private SqlSessionFactory sqlSessionFactory;

    @Override
    void doRegistry() {
        ImportStrategyFactory instance = ImportStrategyFactory.getInstance();
        instance.registry(ImportStrategyEnum.MYBATIS_BATCH_STRATEGY.getStrategyName(), this);
    }

    /*
    5w:7698，7629，7493
    10w:15694，15887，15339
    20w:24564，22924，26506
    40w:55419，57856，57416
     */
    @Override
    public void doImport(List<Maintain> maintains) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 按每一批大小切割原集合
        List<List<Maintain>> lists = this.splitList(maintains, BATCH_SIZE);
        // 开启批量处理模式、关闭自动提交事务
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            // 用这个创建出来的sqlSession获取Mapper，否则配置不生效
            MaintainMapper maintainMapper = sqlSession.getMapper(MaintainMapper.class);
            for (List<Maintain> list : lists) {
                // 每次插入一批数据
                for (Maintain maintain : list) {
                    maintainMapper.insertMaintain(maintain);
                }
                // 清除statementList
                sqlSession.flushStatements();
            }
            // 提交事务
            sqlSession.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            stopWatch.stop();
            System.out.println("MyBatis批处理事务插入方式花费时间 ==> " + stopWatch.getLastTaskTimeMillis());
        }
    }

    @Autowired
    public void setStudentService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    private List<List<Maintain>> splitList(List<Maintain> list, int splitSize) {
        //判断集合是否为空
        if (CollectionUtils.isEmpty(list))
            return Collections.emptyList();
        // 计算分割的份数
        // (总数+每一份的数量-1) / 每一份的数量
        int maxSize = (list.size() + splitSize - 1) / splitSize;
        //开始分割
        return
                // 生成一个从0开始，长度为切割份数的序列
                Stream.iterate(0, integer -> integer + 1).limit(maxSize)
                        .parallel()
                        // 从数字转换成集合
                        // 每一份集合都跳过前(n*每一份数量)，然后取每一份数量
                        // 比如，第一份集合是跳过(0*1000=0)个元素，往后取1000个元素，就是第1个-第1000个元素组成了第一份集合
                        // 第二份集合是跳过(1*1000)个元素，往后取1000个元素，就是第1001个-第2000个元素组成了第二份集合
                        .map(integer -> list.parallelStream().skip((long) integer * splitSize).limit(splitSize).collect(Collectors.toList()))
                        .filter(subList -> !subList.isEmpty())
                        .collect(Collectors.toList());
    }
}
