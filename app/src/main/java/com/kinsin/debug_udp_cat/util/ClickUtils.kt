package com.kinsin.debug_udp_cat.util

class ClickUtils {

    companion object {
        private const val REPEAT_TIME = 800
        private var clickTimeFilter: Long = 0// 通过拦截// 被拦截

        /**
         * 是否重复点击了
         * 普通模式，如果一直点点点，则会隔 REPEAT_TIME 时间响应
         * @return 是/否
         */
        fun isRepeatClick(): Boolean {
            return if (System.currentTimeMillis() - clickTimeFilter < 800) {
                // 被拦截
                true
            } else {
                // 通过拦截
                clickTimeFilter = System.currentTimeMillis()
                false
            }// 通过拦截// 被拦截
        }

        /**
         * 是否重复点击了
         * 严格模式，如果一直点点点，则一直不响应
         * @return 是/否
         */
        fun isRepeatClickStrict(): Boolean {
            return if (System.currentTimeMillis() - clickTimeFilter < 800) {
                // 被拦截
                clickTimeFilter = System.currentTimeMillis()
                true
            } else {
                // 通过拦截
                clickTimeFilter = System.currentTimeMillis()
                false
            }
        }
    }


}