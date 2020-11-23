package com.ucas.cloudenterprise.ui.helpandfeedback

import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.common_head.*

class HelpAndFeedbackActivity: BaseActivity() {


    override fun GetContentViewId()= R.layout.activity_help_and_feedback

    override fun InitView() {
        iv_back.setOnClickListener { finish() }
        tv_title.text = "帮助和反馈"
        tv_edit.text ="我的反馈"
        tv_edit.setOnClickListener {
            startActivity<MyFeedbackActivity>()
        }
    }

    override fun InitData() {
    }
}