package com.ucas.cloudenterprise.ui

import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*

class LinkSharedActivity : BaseActivity()  {
    override fun GetContentViewId()= R.layout.activity_link_shared

    override fun InitView() {
        tv_title.text="链接分享"
        iv_back.setOnClickListener { finish() }
        tv_edit.apply {
            text ="取消"
            setOnClickListener {
                finish()
            }
        }
    }

    override fun InitData() {

    }
}