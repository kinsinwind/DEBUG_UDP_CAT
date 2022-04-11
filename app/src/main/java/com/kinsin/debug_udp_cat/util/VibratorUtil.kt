package com.kinsin.debug_udp_cat.util

import android.content.Context
import android.os.Vibrator

class VibratorUtil {

    companion object {
        var vibrator: Vibrator? = null

        // 短震动
        private val shortPattern = longArrayOf(2, 25, 2, 25)
        // 中震动
        private val middlePattern = longArrayOf(2, 100, 2, 100)
        // 长震动
        private val longPattern = longArrayOf(2, 500, 2, 500)
        // 强烈震动
        private val strongPattern = longArrayOf(2, 1000, 2, 1000)

        /**
         * 短震动
         */
        fun shortVibrator(context: Context) {
            if (vibrator == null) {
                vibrator = context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator!!.vibrate(shortPattern, -1)
        }

        /**
         * 中震动
         */
        fun middleVibrator(context: Context) {
            if (vibrator == null) {
                vibrator = context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator!!.vibrate(middlePattern, -1)
        }

        /**
         * 长震动
         */
        fun longVibrator(context: Context) {
            if (vibrator == null) {
                vibrator = context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator!!.vibrate(longPattern, -1)
        }

        /**
         * 强烈震动
         */
        fun strongVibrator(context: Context) {
            if (vibrator == null) {
                vibrator = context.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator!!.vibrate(strongPattern, -1)
        }
    }

}