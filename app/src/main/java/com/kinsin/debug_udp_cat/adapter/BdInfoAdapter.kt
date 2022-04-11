package com.kinsin.debug_udp_cat.adapter

import android.content.Context
import android.media.metrics.Event
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.transition.Hold
import com.kinsin.debug_udp_cat.R
import com.kinsin.debug_udp_cat.annotation.AopCheck
import com.kinsin.debug_udp_cat.databinding.ItemBdInfoBinding
import com.kinsin.debug_udp_cat.entity.BdInfo
import com.kinsin.debug_udp_cat.util.BDDatabaseTool
import com.kinsin.debug_udp_cat.util.EventBusMessage
import com.kinsin.debug_udp_cat.util.ToastUtil
import org.greenrobot.eventbus.EventBus

class BdInfoAdapter() : BaseAdapter() {
    val IS_START = R.id.TAG_IS_START

    var context: Context? = null
    var bdInfoList: ArrayList<BdInfo>? = null
    var bdMap: HashMap<String, Boolean>? = null

    constructor(
        context: Context?,
        bdInfoList: ArrayList<BdInfo>?,
        bdMap: HashMap<String, Boolean>?
    ) : this() {
        this.context = context
        this.bdInfoList = bdInfoList
        this.bdMap = bdMap
    }

    override fun getCount(): Int {
        return bdInfoList!!.size
    }

    override fun getItem(position: Int): Any {
        return bdInfoList!![position]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        var tempView: View? = null
        var binding: ItemBdInfoBinding? = null
        if (convertView == null) {
            binding = ItemBdInfoBinding.inflate(LayoutInflater.from(p2!!.context), p2, false)
            tempView = binding.root
            tempView.tag = binding
        } else {
            binding = convertView.tag as ItemBdInfoBinding
            tempView = convertView
        }

        binding.showAction.text = bdInfoList!![position].action
        if (bdMap!!.getValue(bdInfoList!![position].action)) {
            binding.startOrPause.setImageResource(R.drawable.ic_pause)
            binding.startOrPause.setTag(IS_START, true)
        } else {
            binding.startOrPause.setImageResource(R.drawable.ic_play)
            binding.startOrPause.setTag(IS_START, false)
        }

        binding.deleteBtn.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                EventBus.getDefault().post(EventBusMessage(EventBusMessage.EVENTBUS_MSG_DELETE_BROADCAST, bdInfoList!![position]))
            }
        })

        binding.editBtn.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                EventBus.getDefault().post(EventBusMessage(EventBusMessage.EVENTBUS_MSG_MODIFY_BROADCAST, bdInfoList!![position]))
            }
        })

        binding.startOrPause.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
//                ToastUtil.showToast(context!!, "你点击的是 " + position + "  名字是：" + binding.showAction.text.toString())
                if (binding.startOrPause.getTag(IS_START) as Boolean) {
                    // 目前处于运行状态，需要暂停
                    binding.startOrPause.setTag(IS_START, false)
                    binding.startOrPause.setImageResource(R.drawable.ic_play)
                    bdMap!![bdInfoList!![position].action] = false
                    EventBus.getDefault().post(
                        EventBusMessage(
                            EventBusMessage.EVENTBUS_MSG_CLOSE_BROADCAST,
                            bdInfoList!![position]
                        )
                    )
                } else {
                    // 目前处于暂停状态，需要运行
                    binding.startOrPause.setTag(IS_START, true)
                    binding.startOrPause.setImageResource(R.drawable.ic_pause)
                    bdMap!![bdInfoList!![position].action] = true
                    EventBus.getDefault().post(
                        EventBusMessage(
                            EventBusMessage.EVENTBUS_MSG_OPEN_BROADCAST,
                            bdInfoList!![position]
                        )
                    )
                }
            }
        })

        return tempView
    }

}