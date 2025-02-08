package com.lizhe.bhrpcannotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RpcService
 * {@code @description} Rpc服务提供者 标注到实现类上
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/8 上午9:45
 * @version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * 接口类型
     * @return 接口类型
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口名称
     * @return 接口名称
     */
    String interfaceClassName() default "";

    /**
     * 服务版本号
     * @return 服务版本号
     */
    String version() default "1.0.0";

    /**
     * 服务分组
     * @return 服务分组
     */
    String group() default "";
}
