package com.kinsin.debug_udp_cat.util

import android.content.Context
import com.kinsin.debug_udp_cat.entity.CATApplication
import com.kinsin.debug_udp_cat.entity.UdpInfo

class LoadConfig {

    var context: Context? = null
    var cat: CATApplication? = null
    var shareTool: ShareTool? = null

    constructor(context: Context) {
        this.context = context.applicationContext
        this.cat = context.applicationContext as CATApplication?
        this.shareTool = cat!!.shareTool
    }

    fun doLoad() {
        loadUdpInfo()
    }

    private fun loadUdpInfo() {
        UdpInfo.ip = shareTool!!.getShareString(CATDefine.SK_UDP_INFO_IP, "")
        UdpInfo.sendPort = shareTool!!.getShareInt(CATDefine.SK_UDP_INFO_SEND_PORT, 0)
        UdpInfo.receivePort = shareTool!!.getShareInt(CATDefine.SK_UDP_INFO_RECEIVER_PORT, 0)
    }

}