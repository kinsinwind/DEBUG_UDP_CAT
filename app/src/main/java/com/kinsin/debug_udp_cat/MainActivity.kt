package com.kinsin.debug_udp_cat

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jaeger.library.StatusBarUtil
import com.kinsin.debug_udp_cat.annotation.AopCheck
import com.kinsin.debug_udp_cat.databinding.ActivityMainBinding
import com.kinsin.debug_udp_cat.entity.BdInfo
import com.kinsin.debug_udp_cat.entity.UdpInfo
import com.kinsin.debug_udp_cat.util.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
        private const val CLEAR_SEND_AREA_TEXT: Int = 1001
        private const val MODIFY_TOUCH_STATE: Int = 1002
    }

    private lateinit var binding: ActivityMainBinding
    private var sourceList: ArrayList<String> = ArrayList() // 从本地持久化中获取到的
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var isTouchFinger = false
    private var bdList = ArrayList<BdInfo>() // 用来判断开启与否的
    private val receiver = UpdReceiver()
    private var isClearReceiverAreaDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.setTransparent(this)
        EventBus.getDefault().register(this)
        initData()
        initAction()
    }

    private fun initData() {
        // 加载ip
        if (UdpInfo.ip == "") {
            binding.showIp.text = StringBuffer().append(getString(R.string.ip_text)).append(getString(R.string.not_set_now)).toString()
        } else {
            binding.showIp.text = StringBuffer().append(getString(R.string.ip_text)).append(UdpInfo.ip).toString()
        }

        // 加载发送端口
        if (UdpInfo.sendPort == 0) {
            binding.showSendPort.text = StringBuffer().append(getString(R.string.send_port_text)).append(getString(R.string.not_set_now)).toString()
        } else {
            binding.showSendPort.text = StringBuffer().append(getString(R.string.send_port_text)).append(UdpInfo.sendPort).toString()
        }

        // 加载接收端口
        if (UdpInfo.receivePort == 0) {
            binding.showReceiverPort.text = StringBuffer().append(getString(R.string.receive_port_text)).append(getString(R.string.not_set_now)).toString()
        } else {
            binding.showReceiverPort.text = StringBuffer().append(getString(R.string.receive_port_text)).append(UdpInfo.receivePort).toString()
        }

        // 加载服务开启标识
        if (UdpTool.getInstance().isRunReceiver) {
            binding.showServerRun.setImageResource(R.drawable.ic_receiver_run)
        } else {
            binding.showServerRun.setImageResource(R.drawable.ic_receiver_off)
        }

        // 接收区适配器
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sourceList)
        binding.receiverListView.adapter = arrayAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAction() {
        // 清除发送区
        binding.clearSendAreaBtn.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                handler.sendEmptyMessage(CLEAR_SEND_AREA_TEXT)
            }
        })

        // 清空接收区
        binding.clearReceiverAreaBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                if (isClearReceiverAreaDialog == null) {
                    isClearReceiverAreaDialog = AlertDialog.Builder(this@MainActivity)
                        .setIcon(R.drawable.ic_cat)
                        .setTitle("注意")
                        .setMessage("是否要清空接收区域")
                        .setNegativeButton("取消", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                            }
                        })
                        .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                sourceList.clear()
                                arrayAdapter.notifyDataSetChanged()
                            }
                        })
                        .create()
                }
                isClearReceiverAreaDialog!!.show()
            }
        })

        // 发送区软键盘
        binding.sendArea.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.sendBtn.performClick()
            }
            true
        }

        // 发送按钮
        binding.sendBtn.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                if ("".equals(binding.sendArea.text.toString())) {
                    ToastUtil.showToast(this@MainActivity, "不能为空")
                    return
                }
                UdpTool.getInstance().sendData(binding.sendArea.text.toString())
                // 清空发送区
                binding.sendArea.setText("√")
                handler.sendEmptyMessageDelayed(CLEAR_SEND_AREA_TEXT, 300)
            }
        })

        // 设置按钮
        binding.settingBtn.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                val intent = Intent()
                intent.setClass(this@MainActivity, SettingActivity().javaClass)
                intent.putExtra("bdList", bdList)
                startActivity(intent)
            }
        })

        // 开关接收服务
        binding.showServerRun.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                VibratorUtil.shortVibrator(this@MainActivity)
                if (!UdpTool.getInstance().isRunReceiver) {
                    if (UdpInfo.receivePort == 0) {
                        ToastUtil.showToast(this@MainActivity, "接收端口号不能为空")
                        return true
                    }
                    if (!PermissionUtil.getInstance().isNet(this@MainActivity)) {
                        ToastUtil.showToast(this@MainActivity, "请先授予网络权限")
                        return true
                    }
                    UdpTool.getInstance().startReceiver()
                    ToastUtil.showToast(this@MainActivity, "接收服务器已开启")
                } else {
                    UdpTool.getInstance().stopReceiver()
                    ToastUtil.showToast(this@MainActivity, "接收服务器已关闭")
                }
                // 加载服务开启标识
                if (UdpTool.getInstance().isRunReceiver) {
                    binding.showServerRun.setImageResource(R.drawable.ic_receiver_run)
                } else {
                    binding.showServerRun.setImageResource(R.drawable.ic_receiver_off)
                }
                return true
            }
        })

        binding.showServerRun.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                ToastUtil.showToast(this@MainActivity, "长按可以开关接收服务器")
            }
        })

        binding.receiverListView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                isTouchFinger = true
                handler.removeMessages(MODIFY_TOUCH_STATE)
                handler.sendEmptyMessageDelayed(MODIFY_TOUCH_STATE, 5 * 1000)
                return false
            }
        })

    }

    var handler: Handler = Handler(object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                CLEAR_SEND_AREA_TEXT -> {
                    binding.sendArea.setText("")
                }
                MODIFY_TOUCH_STATE -> {
                    isTouchFinger = false
                }
            }
            return false
        }
    })

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(message: EventBusMessage) {
        when (message.getType()) {
            EventBusMessage.EVENTBUS_MSG_RECEIVER_DATA -> {
                // 更新消息到接收显示区
                var result = message.getData() as String
//                binding.receiverArea.text = result as String?
                sourceList.add(result)
                if (!isTouchFinger) {
                    binding.receiverListView.requestFocusFromTouch()
                    binding.receiverListView.setSelection(sourceList.size - 1)
                }
                arrayAdapter.notifyDataSetChanged()
            }
            EventBusMessage.EVENTBUS_MSG_OPEN_BROADCAST -> {
                // 开启广播接收器
                val bdInfo = message.getData() as BdInfo
                for (bd in bdList) {
                    if (bd.action == bdInfo.action) {
                        return
                    }
                }
                bdList.add(bdInfo)
                val intentFilter = IntentFilter()
                intentFilter.addAction(bdInfo.action)
                registerReceiver(receiver, intentFilter)
            }
            EventBusMessage.EVENTBUS_MSG_CLOSE_BROADCAST -> {
                // 关闭广播接收器，如果最后一个也删除了就注销接收器
                val bdInfo = message.getData() as BdInfo
                for (bd in bdList) {
                    if (bd.action == bdInfo.action) {
                        bdList.remove(bd)
                        break
                    }
                }
                if (bdList.size == 0) {
                    unregisterReceiver(receiver)
                }
            }
        }
    }

    private inner class UpdReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            val index = isHaveBdInfo(action!!)
            val bufferString = StringBuffer()
            if (index != -1) {
                for (filter in bdList[index].filterList) {
                    bufferString.append(intent.getStringExtra(filter))
                }
                UdpTool.getInstance().sendData(bufferString.toString())
            }
        }
    }

    private fun isHaveBdInfo(action: String): Int {
        for (i in (0..bdList.size)) {
            if (i < bdList.size && bdList[i].action == action) {
                return i
            }
        }
        return -1
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}