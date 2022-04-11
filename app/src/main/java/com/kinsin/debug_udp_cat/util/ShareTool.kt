package com.kinsin.debug_udp_cat.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class ShareTool {
    companion object {
        const val SHARE_KEY: String = "DEBUG_UDP_CAT_SHARED"
    }

    private var shared: SharedPreferences? = null

    constructor(context: Context) {
        shared = if (Build.VERSION.SDK_INT >= 24) {
            context.getSharedPreferences(SHARE_KEY, Context.MODE_PRIVATE)
        } else {
            context.getSharedPreferences(
                SHARE_KEY,
                Context.MODE_MULTI_PROCESS or Context.MODE_WORLD_WRITEABLE
                        or Context.MODE_WORLD_READABLE
            )
        }
    }

    fun setShareInt(key: String, value: Int) {
        val editor: SharedPreferences.Editor = shared!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setShareBoolean(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor = shared!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun setShareString(key: String, value: String) {
        val editor: SharedPreferences.Editor = shared!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setShareLong(key: String, value: Long) {
        val editor: SharedPreferences.Editor = shared!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getShareInt(key: String, value: Int): Int {
        return shared!!.getInt(key, value)
    }

    fun getShareBoolean(key: String, value: Boolean): Boolean {
        return shared!!.getBoolean(key, value)
    }

    fun getShareString(key: String, value: String): String {
        return shared!!.getString(key, value)!!
    }

    fun getShareLong(key: String, value: Long): Long {
        return shared!!.getLong(key, value)
    }

    fun contains(key: String): Boolean {
        return shared!!.contains(key)
    }

}