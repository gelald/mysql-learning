package com.github.gelald.batch.strategy;

import com.github.gelald.batch.entity.Maintain;
import com.github.gelald.batch.enums.ImportStrategyEnum;
import com.github.gelald.batch.factory.ImportStrategyFactory;
import com.github.gelald.batch.service.MaintainService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class MyBatisPlusBatchStrategy extends AbstractImportStrategy {
    private MaintainService maintainService;

    @Override
    void doRegistry() {
        ImportStrategyFactory instance = ImportStrategyFactory.getInstance();
        instance.registry(ImportStrategyEnum.MYBATIS_PLUS_BATCH_STRATEGY.getStrategyName(), this);
    }

    /**
     * 开启rewriteBatchedStatements前
     * 9.3s、9.8s、10s
     * 开启rewriteBatchedStatements后
     * 7.4s、7.2s、7.2s
     */
    @Override
    public void doImport(List<Maintain> maintains) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            // 使用MyBatis-Plus封装的批处理方法
            this.maintainService.saveBatch(maintains, BATCH_SIZE);
            log.info("本次提交{}条数据", maintains.size());
            log.info("事务提交");
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            stopWatch.stop();
            log.info("MyBatis-Plus批处理插入事务方式花费时间 ==> {}毫秒", stopWatch.getLastTaskTimeMillis());
        }
    }

    @Autowired
    public void setMaintainService(MaintainService maintainService) {
        this.maintainService = maintainService;
    }
}
