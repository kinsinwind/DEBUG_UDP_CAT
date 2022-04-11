package com.kinsin.debug_udp_cat.aop

import android.util.Log
import android.view.View
import com.kinsin.debug_udp_cat.util.ClickUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature

/**
 * 点击Aop 插桩
 * 方法1：统一进行重复点击过滤
 */
@Aspect
class ClickAop {

    companion object {
        const val TAG = "ClickAop"
    }

    /**
     * 是否开启拦截， 通过注解 [com.kinsin.debug_udp_cat.annotation.UnCheckClick]
     * 默认开启拦截
     */
    var isCheckRepeat: Boolean = true

    /**
     * 上次点击事件View
     */
    var lastView: View? = null

    fun doClick(joinPoint: ProceedingJoinPoint) {
        lastView = joinPoint.args[0] as View
        joinPoint.proceed()
    }

    /**
     * 防止重复点击
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Around("execution(@com.kinsin.debug_udp_cat.annotation.AopCheck * *(..))")
    fun clickFilterTime(joinPoint: ProceedingJoinPoint) {
        if (ClickUtils.Companion.isRepeatClick() && isCheckRepeat && lastView == (joinPoint).getArgs()[0]) {
            // 拦截
            return
        }
        // 默认需要拦截
        isCheckRepeat = true;
        var className = joinPoint.target.javaClass.simpleName
        var methodName = joinPoint.signature as MethodSignature
        Log.d(TAG, "$className :  <$methodName> Click AOP already intercept");
        doClick(joinPoint);
    }

    /**
     * 有些点击事件不需要防重复点击
     *
     * @param joinPoint
     */
    @Before("execution(@com.kinsin.debug_udp_cat.annotation.UnCheckClick * *(..))")
    fun beforeUnCheckClick(joinPoint: JoinPoint) {
        Log.d(TAG, "beforeUnCheckClick")
        val className = joinPoint.target.javaClass.simpleName
        val methodName = joinPoint.signature as MethodSignature
        Log.d(
            TAG,
            "$className :  <$methodName> do not need repeat click intercept"
        )
        isCheckRepeat = false
    }

}