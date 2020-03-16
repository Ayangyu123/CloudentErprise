package com.ucas.cloudenterprise.ui

import android.content.pm.PackageManager
import android.view.View
import com.ucas.cloudenterprise.BuildConfig
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.personal_center_fragment.*

class AboutActivity: BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_about

    override fun InitView() {
     tv_title.text ="关于"
        iv_back.setOnClickListener { finish() }
        tv_version.text = "V ${BuildConfig.VERSION_NAME}"
        tv_edit.visibility = View.GONE

    }

    override fun InitData() {
    }

    fun GotoWebActivity(view: View) {
        startActivity<CommonWebActivity>()
    }
}