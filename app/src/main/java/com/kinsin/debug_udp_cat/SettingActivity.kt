package com.kinsin.debug_udp_cat

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.jaeger.library.StatusBarUtil
import com.kinsin.debug_udp_cat.adapter.BdInfoAdapter
import com.kinsin.debug_udp_cat.annotation.AopCheck
import com.kinsin.debug_udp_cat.databinding.ActivitySettingBinding
import com.kinsin.debug_udp_cat.dialog.DialogAddBroadcastReceiver
import com.kinsin.debug_udp_cat.entity.BdInfo
import com.kinsin.debug_udp_cat.entity.CATApplication
import com.kinsin.debug_udp_cat.entity.UdpInfo
import com.kinsin.debug_udp_cat.util.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    private var cat: CATApplication? = null
    private var bdInfoAdapter: BdInfoAdapter? = null
    private var bdInfoList: ArrayList<BdInfo> = ArrayList()
    private var bdStartMap: HashMap<String, Boolean> = HashMap<String, Boolean>()
    private var addBdDialog: DialogAddBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setColor(this, getColor(R.color.gray_ef))
        StatusBarUtil.setTransparent(this)
        EventBus.getDefault().register(this)
        cat = applicationContext as CATApplication

        initView()
        initData()
        initAction()
    }

    private fun initView() {
        // ListView需要滑动
        binding.broadcastListView.setFatherToEnableTouch(binding.scrollViewFather)
    }

    private fun initData() {
        // 加载开始按钮文字
        if (UdpTool.getInstance().isRunReceiver) {
            binding.startOrStopReceiverServerBtn.text = getString(R.string.stop_receive_server)
        } else {
            binding.startOrStopReceiverServerBtn.text = getString(R.string.start_receive_server)
        }

        // 加载本地ipv4
        binding.showLocalIpv4.text = StringBuffer().append(getString(R.string.local_ip_text)).append(IPUtil.getIpAddress(this))

        // 加载广播列表
        bdInfoList = BDDatabaseTool.getAll(this)
        // 初始化广播列表按钮
        val bdList: ArrayList<BdInfo> = intent.getSerializableExtra("bdList") as ArrayList<BdInfo>
        for (bdInfo in bdInfoList) {
            bdStartMap[bdInfo.action] = false
        }
        if (bdList.size > 0) {
            for (bdInfo in bdInfoList) {
                for (innerInfo in bdList) {
                    if (bdInfo.action == innerInfo.action) {
                        bdStartMap[bdInfo.action] = true
                    }
                }
            }
        }

        // 适配器
        bdInfoAdapter = BdInfoAdapter(this, bdInfoList, bdStartMap)
        binding.broadcastListView.adapter = bdInfoAdapter

        // 加载字符集spinner
        val charSetAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, CATApplication.CHARSETS_ARRAY)
        binding.charSetChoose.adapter = charSetAdapter
        binding.charSetChoose.setSelection(cat!!.shareTool!!.getShareInt(CATDefine.SK_UDP_INFO_SEND_CHARSET, 0))
    }

    private fun initAction() {
        // ip应用监听
        binding.ipConfirmBtnSetting.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                if ("" == binding.ipEditSetting.text.toString()) {
                    ToastUtil.showToast(this@SettingActivity, getString(R.string.ip_not_empty))
                    return
                }
                UdpInfo.ip = binding.ipEditSetting.text.toString()
                binding.ipEditSetting.hint = binding.ipEditSetting.text.toString()
                binding.ipEditSetting.setText("")
            }
        })

        // 发送端口监听
        binding.sendPortConfirmBtnSetting.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                if ("" == binding.sendPortEditSetting.text.toString()) {
                    ToastUtil.showToast(this@SettingActivity, getString(R.string.send_port_not_empty))
                    return
                }
                UdpInfo.sendPort = binding.sendPortEditSetting.text.toString().toInt()
                binding.sendPortEditSetting.hint = binding.sendPortEditSetting.text.toString()
                binding.sendPortEditSetting.setText("")
            }
        })

        // 接收端口监听
        binding.receiverConfirmBtnSetting.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                if ("" == binding.receiverEditSetting.text.toString()) {
                    ToastUtil.showToast(this@SettingActivity, getString(R.string.receive_port_not_empty))
                    return
                }
                UdpInfo.receivePort = binding.receiverEditSetting.text.toString().toInt()
                binding.receiverEditSetting.hint = binding.receiverEditSetting.text.toString()
                binding.receiverEditSetting.setText("")
            }
        })

        // 开启服务
        binding.startOrStopReceiverServerBtn.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                if (binding.startOrStopReceiverServerBtn.text.toString() == getString(R.string.start_receive_server)) {
                    if (UdpInfo.receivePort == 0) {
                        ToastUtil.showToast(this@SettingActivity, getString(R.string.receive_port_not_empty))
                        return
                    }
                    if (!PermissionUtil.getInstance().isNet(this@SettingActivity)) {
                        ToastUtil.showToast(this@SettingActivity, getString(R.string.need_internet_permission))
                        return
                    }
                    UdpTool.getInstance().startReceiver()
                    binding.startOrStopReceiverServerBtn.text = getString(R.string.stop_receive_server)
                } else if (binding.startOrStopReceiverServerBtn.text.toString() == getString(R.string.stop_receive_server)) {
                    UdpTool.getInstance().stopReceiver()
                    binding.startOrStopReceiverServerBtn.text = getString(R.string.start_receive_server)
                }
            }
        })

        // 返回键
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 添加广播接收器
        binding.addBroadcastReceiverBtn.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                if (addBdDialog == null) {
                    addBdDialog = DialogAddBroadcastReceiver(this@SettingActivity)
                }
                addBdDialog!!.show()
            }
        })

        // 字符集切换监听
        binding.charSetChoose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                cat!!.shareTool!!.setShareInt(CATDefine.SK_UDP_INFO_SEND_CHARSET, p2)
                CATApplication.CHARSET_NOW = CATApplication.CHARSETS_ARRAY[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(message: EventBusMessage) {
        when(message.getType()) {
            EventBusMessage.EVENTBUS_MSG_UI_UPDATE_BD_LISTVIEW -> {
                // 刷新广播列表显示
                var bdInfo = message.getData() as BdInfo
                bdInfoList.add(bdInfo)
                bdStartMap[bdInfo.action] = false
                bdInfoAdapter!!.notifyDataSetChanged()

                addBdDialog!!.dismiss()
            }
            EventBusMessage.EVENTBUS_MSG_UI_UPDATE_MODIFY_BD_LISTVIEW -> {
                // 刷新广播列表显示
                var bdInfo = message.getData() as BdInfo
                for (i in (0 until bdInfoList.size)) {
                    if (bdInfo.action == bdInfoList[i].action) {
                        bdInfoList[i] = bdInfo
                        break
                    }
                }
                bdInfoAdapter!!.notifyDataSetChanged()

                addBdDialog!!.dismiss()
            }
            EventBusMessage.EVENTBUS_MSG_MODIFY_BROADCAST -> {
                // 修改广播
                var bdInfo = message.getData() as BdInfo

                if (bdStartMap[bdInfo.action]!!) {
                    ToastUtil.showToast(this, getString(R.string.please_modify_in_pause_state))
                } else{
                    addBdDialog = DialogAddBroadcastReceiver(this, bdInfo)
                    addBdDialog!!.show()
                }
            }
            EventBusMessage.EVENTBUS_MSG_DELETE_BROADCAST -> {
                // 删除广播
                var bdInfo = message.getData() as BdInfo

                if (bdStartMap[bdInfo.action]!!) {
                    ToastUtil.showToast(this, getString(R.string.please_delete_in_pause_state))
                } else{
                    BDDatabaseTool.delete(this, bdInfo.action)
                    bdInfoList.remove(bdInfo)
                    bdInfoAdapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        SaveConfig.getInstance(this).saveUdpInfo()
        EventBus.getDefault().unregister(this)
    }

}