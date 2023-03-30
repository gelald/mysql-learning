package com.github.gelald.batch.factory;

import com.github.gelald.batch.strategy.AbstractImportStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WuYingBin
 * date: 2022/12/26
 */
public class ImportStrategyFactory {
    /**
     * 单例对象
     */
    private static final ImportStrategyFactory INSTANCE = new ImportStrategyFactory();
    /**
     * 存储策略的容器
     */
    private final Map<String, AbstractImportStrategy> map = new ConcurrentHashMap<>();

    private ImportStrategyFactory() {
    }

    /**
     * 把策略按名称注册到缓存中去
     *
     * @param strategyName           策略名
     * @param abstractImportStrategy 策略实例
     */
    public void registry(String strategyName, AbstractImportStrategy abstractImportStrategy) {
        map.put(strategyName, abstractImportStrategy);
    }

    /**
     * 根据策略名获取具体策略
     *
     * @param strategyName 策略名
     * @return 策略实例
     */
    public AbstractImportStrategy getStrategy(String strategyName) {
        return map.get(strategyName);
    }

    /**
     * 获取策略工厂实例
     *
     * @return 工厂实例
     */
    public static ImportStrategyFactory getInstance() {
        return INSTANCE;
    }
}
