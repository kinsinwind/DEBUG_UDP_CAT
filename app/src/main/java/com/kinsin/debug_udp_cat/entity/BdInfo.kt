package com.kinsin.debug_udp_cat.entity

import java.io.Serializable

class BdInfo : Serializable {
    var action = ""
    var filterList: ArrayList<String> = ArrayList()

}