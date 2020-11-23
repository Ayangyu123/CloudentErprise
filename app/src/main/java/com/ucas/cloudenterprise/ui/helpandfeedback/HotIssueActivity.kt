package com.ucas.cloudenterprise.ui.helpandfeedback

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_hot_issue.*
import kotlinx.android.synthetic.main.common_head.*

class HotIssueActivity : BaseActivity() {
    override fun GetContentViewId() = R.layout.activity_hot_issue

    override fun InitView() {
        iv_back.setOnClickListener { finish() }
        tv_title.text = "热门问题"
        tv_edit.visibility = View.GONE
        tv_solve.setOnClickListener {
            Toastinfo("谢谢支持")
        }
        tv_no_solve.setOnClickListener {
            startActivity<MyFeedbackActivity>()
        }

    }

    override fun InitData() {
    }
}