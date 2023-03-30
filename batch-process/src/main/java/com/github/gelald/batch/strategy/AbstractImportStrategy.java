package com.github.gelald.batch.strategy;

import com.github.gelald.batch.entity.Maintain;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
public abstract class AbstractImportStrategy implements InitializingBean {
    /**
     * 一次批量提交的数量
     */
    protected final static int BATCH_SIZE = 2000;

    /**
     * 注册此导入策略
     */
    abstract void doRegistry();

    /**
     * 实际导入逻辑
     */
    public abstract void doImport(List<Maintain> maintains);

    /**
     * Bean初始化时自动完成策略导入工作
     */
    @Override
    public void afterPropertiesSet() {
        this.doRegistry();
    }
}
