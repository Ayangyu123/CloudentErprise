package com.ucas.cloudenterprise.ui.helpandfeedback

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*

class MyFeedbackActivity: BaseActivity() {

    override fun GetContentViewId()= R.layout.activity_my_feedback

    override fun InitView() {
        iv_back.setOnClickListener { finish() }

        tv_title.text ="我的反馈"
        tv_edit.visibility = View.GONE
    }

    override fun InitData() {
    }
}