package com.nenu.aspect.lang.annotation;

import com.nenu.aspect.lang.enums.BusinessType;

import java.lang.annotation.*;

/**
 * 自定义操作记录注解
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 类功能描述
     * @return
     */
    String classFunctionDescribe() default "";

    /**
     * 方法功能描述
     * @return
     */
    String methodFunctionDescribe() default "";


    /**
     * 功能
     */
    BusinessType businessType() default BusinessType.OTHER;

}
