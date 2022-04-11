package com.kinsin.debug_udp_cat.util

class EventBusMessage {

    private var type: Int = 0
    private var data: Any? = null

    constructor() {}

    constructor(type: Int) {
        this.type = type
    }

    constructor(type: Int, data: Any?) {
        this.type = type
        this.data = data
    }

    fun getType(): Int {
        return type
    }

    fun getData(): Any {
        return data!!
    }

    companion object {
        const val EVENTBUS_MSG_RECEIVER_DATA = 10001
        const val EVENTBUS_MSG_RECEIVER_SERVER_CLOSE = 10002

        const val EVENTBUS_MSG_UI_UPDATE_BD_LISTVIEW = 20001
        const val EVENTBUS_MSG_OPEN_BROADCAST = 20002
        const val EVENTBUS_MSG_CLOSE_BROADCAST = 20003
        const val EVENTBUS_MSG_MODIFY_BROADCAST = 20004
        const val EVENTBUS_MSG_UI_UPDATE_MODIFY_BD_LISTVIEW = 20005
        const val EVENTBUS_MSG_DELETE_BROADCAST = 20006

    }
}