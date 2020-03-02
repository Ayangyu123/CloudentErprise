package com.ucas.cloudenterprise.ui

import android.content.pm.PackageManager
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.personal_center_fragment.*

class AboutActivity: BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_about

    override fun InitView() {
     tv_title.text ="关于"
        iv_back.setOnClickListener { finish() }
        var  pm: PackageManager = getPackageManager()!!
        var packageinfo =pm.getPackageInfo(packageName,0)
        tv_version.text = "V ${packageinfo.versionName}"
        iv_title_search.apply {
            tv_edit.visibility = View.GONE
            visibility = View.VISIBLE
            setOnClickListener {
//                startActivity<>()
            }
        }
    }

    override fun InitData() {
    }
}