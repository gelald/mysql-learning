package com.github.gelald.mpcommon.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.gelald.mysql.base.context.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充数据
 *
 * @author WuYingBin
 * date: 2023/3/16
 */
@Slf4j
@Component
public class CommonFieldHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入数据时自动填充公共字段");
        // 两种写法都可以
        // this.strictInsertFill(metaObject, "createdDate", LocalDate.class, LocalDate.now());
        // this.strictInsertFill(metaObject, "updatedDate", LocalDate.class, LocalDate.now());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", CurrentUserContext.getId());
        metaObject.setValue("updateUser", CurrentUserContext.getId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新数据时自动填充公共字段");
        // 两种写法都可以
        // this.strictUpdateFill(metaObject, "updatedDate", LocalDate.class, LocalDate.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", CurrentUserContext.getId());
    }
}
