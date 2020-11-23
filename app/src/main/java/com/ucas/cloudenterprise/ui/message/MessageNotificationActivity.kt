package com.ucas.cloudenterprise.ui.message

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.activity_message_notification.*
import kotlinx.android.synthetic.main.common_head.*

class MessageNotificationActivity : BaseActivity() {

    override fun GetContentViewId()= R.layout.activity_message_notification

    override fun InitView() {
        tv_title.text ="消息通知"
        tv_edit.text ="多选"
    iv_back.setOnClickListener { finish() }
        tl.apply {
            addTab(this.newTab().setText("文件通知"))
            addTab(this.newTab().setText("共享通知"))
            addTab(this.newTab().setText("系统通知"))
        }
    }

    override fun InitData() {

    }
}