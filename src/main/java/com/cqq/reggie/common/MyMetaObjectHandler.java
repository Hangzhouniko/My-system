package com.cqq.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info(metaObject.toString());
        log.info("公共字段填充！insert");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.threadLocal.get());
        metaObject.setValue("updateUser", BaseContext.threadLocal.get());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info(metaObject.toString());
        log.info("公共字段填充！update");
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.threadLocal.get());
    }
}
