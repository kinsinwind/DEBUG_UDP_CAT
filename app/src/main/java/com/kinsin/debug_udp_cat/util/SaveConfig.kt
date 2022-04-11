package com.kinsin.debug_udp_cat.util

import android.content.Context
import com.kinsin.debug_udp_cat.entity.CATApplication
import com.kinsin.debug_udp_cat.entity.UdpInfo

class SaveConfig {

    var context: Context? = null
    var cat: CATApplication? = null
    var shareTool: ShareTool? = null

    private constructor(context: Context) {
        this.context = context.applicationContext
        this.cat = context.applicationContext as CATApplication?
        this.shareTool = ShareTool(context.applicationContext)
    }

    companion object {
        var saveConfig: SaveConfig? = null

        fun getInstance(context: Context): SaveConfig {
            if (saveConfig == null) {
                saveConfig = SaveConfig(context)
            }
            return saveConfig!!
        }
    }

    fun saveUdpInfo() {
        shareTool!!.setShareString(CATDefine.SK_UDP_INFO_IP, UdpInfo.ip)
        shareTool!!.setShareInt(CATDefine.SK_UDP_INFO_SEND_PORT, UdpInfo.sendPort)
        shareTool!!.setShareInt(CATDefine.SK_UDP_INFO_RECEIVER_PORT, UdpInfo.receivePort)
    }

}