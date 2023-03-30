package com.github.gelald.batch.strategy;

import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.enums.ImportStrategyEnum;
import com.github.gelald.batch.factory.ImportStrategyFactory;
import com.github.gelald.batch.service.MaintainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;

/**
 * for循环+MyBatis-Plus的save方法+事务+批量提交
 *
 * @author WuYingBin
 * date: 2022/12/26
 */
@Component
public class MyBatisPlusBatchStrategy extends AbstractImportStrategy {
    private MaintainService maintainService;

    @Override
    void doRegistry() {
        ImportStrategyFactory instance = ImportStrategyFactory.getInstance();
        instance.registry(ImportStrategyEnum.MYBATIS_PLUS_BATCH_STRATEGY.getStrategyName(), this);
    }

    /*
    5w:6703，6408，6445
    10w:11355，11657，11493
    20w:22735，22482，24238
    40w:51405，51292，50074
     */
    @Override
    public void doImport(List<Maintain> maintains) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            // 使用MyBatis-Plus封装的批处理方法
            this.maintainService.saveBatch(maintains, BATCH_SIZE);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            stopWatch.stop();
            System.out.println("MyBatis-Plus批处理插入事务方式花费时间 ==> " + stopWatch.getLastTaskTimeMillis());
        }
    }

    @Autowired
    public void setMaintainService(MaintainService maintainService) {
        this.maintainService = maintainService;
    }
}
