package com.kinsin.debug_udp_cat.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 不检查重复点击 注解
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(
    RetentionPolicy.RUNTIME
)
annotation class UnCheckClick 