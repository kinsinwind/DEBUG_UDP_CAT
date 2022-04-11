package com.kinsin.debug_udp_cat.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtil {

    companion object {
        /**
         * 获取实例
         */
        fun getInstance(): PermissionUtil {
            return SingletonHolder.instance
        }

        /**
         * 单例
         */
        private class SingletonHolder {
            companion object {
                var instance: PermissionUtil = PermissionUtil()
            }
        }

    }

    fun isNet(context: Context) : Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
    }

}