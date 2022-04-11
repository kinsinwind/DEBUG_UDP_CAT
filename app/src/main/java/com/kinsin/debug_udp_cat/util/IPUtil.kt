package com.kinsin.debug_udp_cat.util

import android.content.Context
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class IPUtil {

    companion object {

        /**
         * 获取本机IPv4地址
         *
         * @param context
         * @return 本机IPv4地址；null：无网络连接
         */
        fun getIpAddress(context: Context): String {
            // 获取wifi服务
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 判断wifi是否开启
            return if (wifiManager.isWifiEnabled) {
                // 已经开启wifi
                val wifiInfo = wifiManager!!.connectionInfo
                val ipAddress = wifiInfo.ipAddress
                intToIp(ipAddress)
            } else {
                // 未开启wifi
                getIpAddress()
            }

        }

        private fun intToIp(ipAddress: Int): String {
            return ipAddress.and(0xFF).toString() + "." +
                    ipAddress.shr(8).and(0xFF).toString() + "." +
                    ipAddress.shr(16).and(0xFF).toString() + "." +
                    ipAddress.shr(24).and(0xFF).toString()

        }

        /**
         * 获取本机IPv4地址
         *
         * @return 本机IPv4地址；null：无网络连接
         */
        private fun getIpAddress(): String {
            var networkInterface: NetworkInterface? = null
            var inetAddress: InetAddress? = null
            val networks: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (networks.hasMoreElements()) {
                networkInterface = networks.nextElement()
                var ipAddresses = networkInterface.inetAddresses
                while (ipAddresses.hasMoreElements()) {
                    inetAddress = ipAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && !inetAddress.isLinkLocalAddress) {
                        return inetAddress.hostAddress
                    }
                }
            }
            return null.toString()
        }

        /*
        * /**
     * 获取本机IPv4地址
     *
     * @return 本机IPv4地址；null：无网络连接
     */
    private static String getIpAddress() {
        try {
            NetworkInterface networkInterface;
            InetAddress inetAddress;
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
            return null;
        } catch (SocketException ex) {
            ex.printStackTrace();
            return null;
        }
    }
        *
        * */

    }


}