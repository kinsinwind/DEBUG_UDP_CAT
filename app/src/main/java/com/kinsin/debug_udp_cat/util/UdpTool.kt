package com.kinsin.debug_udp_cat.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.kinsin.debug_udp_cat.entity.CATApplication
import com.kinsin.debug_udp_cat.entity.UdpInfo
import org.greenrobot.eventbus.EventBus
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UdpTool {
    var isRunReceiver: Boolean = false
    private var udp: DatagramSocket? = null // 发送
    private var serverAddress: InetAddress? = null // 服务器地址
    private var executorService: ExecutorService = Executors.newCachedThreadPool() // 线程池
    private var receiverSocket: DatagramSocket? = null // 接收

    companion object {

        /**
         * 获取实例
         */
        fun getInstance(): UdpTool {
            return SingletonHolder.instance
        }

        /**
         * 单例
         */
        private class SingletonHolder {
            companion object {
                var instance: UdpTool = UdpTool()
            }
        }

    }

    /**
     * 发送消息
     * @param content 字符串消息
     */
    @Synchronized
    fun sendData(content: String) {
        handler.removeMessages(0)
        handler.sendEmptyMessageDelayed(0, 10 * 1000)
        if (udp == null) {
            udp = DatagramSocket(UdpInfo.sendPort)
            serverAddress = InetAddress.getByName(UdpInfo.ip)
        }
        executorService.execute(Runnable {
            kotlin.run {
                val data: ByteArray = content.toByteArray(charset(CATApplication.CHARSET_NOW))
                val packet: DatagramPacket =
                    DatagramPacket(data, data.size, serverAddress, UdpInfo.sendPort)
                if (udp != null) {
                    udp!!.send(packet)
                }
            }
        })
    }

    /**
     * 开启接收器
     */
    @Synchronized
    fun startReceiver() {
        isRunReceiver = true
        if(receiverSocket == null){
            receiverSocket = DatagramSocket(null)
            receiverSocket!!.reuseAddress = true
            receiverSocket!!.bind(InetSocketAddress(UdpInfo.receivePort))
        }
        executorService.execute(Runnable {
            kotlin.run {
                while (isRunReceiver) {
                    Thread.sleep(50)
                    val data = ByteArray(1440)
                    var packet = DatagramPacket(data, data.size)
                    receiverSocket!!.receive(packet)
                    var result = String(packet.data, packet.offset, packet.length)
                    EventBus.getDefault().post(EventBusMessage(EventBusMessage.EVENTBUS_MSG_RECEIVER_DATA, result))
                }
                receiverSocket!!.close()
                receiverSocket = null
            }
        })
    }

    private var handler = Handler(Looper.getMainLooper() , object : Handler.Callback {
        override fun handleMessage(p0: Message): Boolean {
            udp!!.close()
            udp = null
            return false
        }
    })

    /**
     * 停止接收器
     */
    fun stopReceiver() {
        isRunReceiver = false;
    }

}