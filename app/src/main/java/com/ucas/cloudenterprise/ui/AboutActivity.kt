package com.ucas.cloudenterprise.ui

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*

class AboutActivity: BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_about

    override fun InitView() {
     tv_title.text ="关于"
        iv_back.setOnClickListener { finish() }
        iv_title_search.apply {
            tv_edit.visibility = View.GONE
            visibility = View.VISIBLE
            setOnClickListener {
//                startActivity<>()
            }
        }
    }

    override fun InitData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}