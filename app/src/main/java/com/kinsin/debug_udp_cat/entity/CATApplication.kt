package com.kinsin.debug_udp_cat.entity

import android.app.Application
import com.kinsin.debug_udp_cat.util.LoadConfig
import com.kinsin.debug_udp_cat.util.ShareTool
import java.util.*

class CATApplication : Application() {

    companion object {
        val CHARSETS_ARRAY = listOf("UTF-8", "GBK", "ASCII", "Unicode")
        var CHARSET_NOW = "UTF-8"
    }

    var shareTool: ShareTool? = null
    var loadConfig: LoadConfig? = null

    override fun onCreate() {
        super.onCreate()
        shareTool = ShareTool(applicationContext)
        loadConfig = LoadConfig(applicationContext)
        init()
    }

    private fun init() {
        loadConfig!!.doLoad()
    }

}