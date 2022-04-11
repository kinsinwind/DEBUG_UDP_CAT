package com.kinsin.debug_udp_cat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kinsin.debug_udp_cat.entity.CATApplication
import com.kinsin.debug_udp_cat.util.CATDefine

class Splash : AppCompatActivity() {

    var cat: CATApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        cat = applicationContext as CATApplication

        loadConfig()

        var intent = Intent()
        intent.setClass(this, MainActivity().javaClass)
        startActivity(intent)
        finish()
    }

    private fun loadConfig() {
        // 加载字符集
        CATApplication.CHARSET_NOW = CATApplication.CHARSETS_ARRAY[cat!!.shareTool!!.getShareInt(CATDefine.SK_UDP_INFO_SEND_CHARSET, 0)]
    }
}