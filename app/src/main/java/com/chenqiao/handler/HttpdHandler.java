package com.chenqiao.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>应用于 {@link AbstractHandler}的子类，用于标记Handler可处理的请求</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HttpdHandler {
    String name() default "";
}
