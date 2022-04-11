package com.kinsin.debug_udp_cat.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.kinsin.debug_udp_cat.R
import com.kinsin.debug_udp_cat.annotation.AopCheck
import com.kinsin.debug_udp_cat.databinding.ActivitySettingBinding
import com.kinsin.debug_udp_cat.databinding.DialogAddBroadcastReceiverBinding
import com.kinsin.debug_udp_cat.entity.BdInfo
import com.kinsin.debug_udp_cat.util.BDDatabaseTool
import com.kinsin.debug_udp_cat.util.EventBusMessage
import com.kinsin.debug_udp_cat.util.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class DialogAddBroadcastReceiver : Dialog {

    constructor(context: Context) : super(context)
    constructor(context: Context, bdInfo: BdInfo) : this(context) {
        this.bdInfo = bdInfo
    }

    private lateinit var binding: DialogAddBroadcastReceiverBinding
    private var bdInfo: BdInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddBroadcastReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置动画
        var window = this.window
//        window.setWindowAnimations(R.style.PopWindowPreviewDeviceListAnim);

        // 点击外部消失
        this.setCanceledOnTouchOutside(true);

        // 设置背景
        window!!.setBackgroundDrawable(null)
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );

        // 去除弹出时的半透明背景
//        window.setDimAmount(0F);

        // 设置位置
        window.setGravity(Gravity.CENTER);

        // 初始化View
        initView()

        // 初始化监听器
        initListener()

        // 初始化数据
        initData()

    }

    private fun initData() {
        // 填充编辑框
        if (bdInfo != null) {
            binding.dialogActionInputAddBd.setText(bdInfo!!.action)
            var buffer: StringBuffer = StringBuffer("")
            for (item in bdInfo!!.filterList) {
                buffer.append(item).append("#")
            }
            binding.dialogFilterKeyInputAddBd.setText(buffer.toString().substring(0, buffer.length - 1))

            // 由于action是主键，所以action不允许修改
            binding.dialogActionInputAddBd.isEnabled = false
        } else{
            binding.dialogActionInputAddBd.isEnabled = true
        }
    }

    private fun initListener() {

        // 取消按钮
        binding.cancelBtnDialog.setOnClickListener {
            dismiss()
        }

        // 确认按钮
        binding.addBtnDialog.setOnClickListener(object : View.OnClickListener {
            @AopCheck
            override fun onClick(p0: View?) {
                var bdInfo = BdInfo()
                bdInfo.action = binding.dialogActionInputAddBd.text.toString()
                var filterString = binding.dialogFilterKeyInputAddBd.text.toString()
                if (binding.dialogActionInputAddBd.text.toString() == "") {
                    ToastUtil.showToast(context, context.getString(R.string.action_is_not_empty))
                    return
                }
                if (binding.dialogFilterKeyInputAddBd.text.toString() == "") {
                    // 过滤词是可以为空的
                } else {
                    if (filterString.contains("#")) {
                        // 有多个词
                        var filters = filterString.split("#")
                        for (filter in filters) {
                            bdInfo.filterList.add(filter)
                        }
                    } else {
                        // 只有一个
                        bdInfo.filterList.add(filterString)
                    }
                }

                // 持久化
                BDDatabaseTool.insertOrUpdate(context, bdInfo)

                if (binding.dialogActionInputAddBd.isEnabled) {
                    // 添加完成
                    EventBus.getDefault().post(EventBusMessage(EventBusMessage.EVENTBUS_MSG_UI_UPDATE_BD_LISTVIEW, bdInfo))
                } else{
                    // 修改完成
                    EventBus.getDefault().post(EventBusMessage(EventBusMessage.EVENTBUS_MSG_UI_UPDATE_MODIFY_BD_LISTVIEW, bdInfo))
                }

                // 处理后续
                operateRear()
            }
        })


    }

    private fun initView() {

    }

    private fun operateRear() {
        // 清空输入框
        binding.dialogActionInputAddBd.setText("")
        binding.dialogFilterKeyInputAddBd.setText("")

        // 置空bdInfo
        bdInfo = null
    }

}