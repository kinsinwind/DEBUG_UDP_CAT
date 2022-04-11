package com.kinsin.debug_udp_cat.util

import android.content.Context
import android.widget.Toast

class ToastUtil {

    companion object {
        private var toast: Toast? = null

        @Synchronized
        fun showToast(context: Context, content: String) {
            if (toast != null) {
                toast!!.cancel()
                toast = null
            }
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
            toast!!.show()
        }
    }

}