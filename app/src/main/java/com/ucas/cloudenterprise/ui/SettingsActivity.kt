package com.ucas.cloudenterprise.ui

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.ROOT_DIR_PATH
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.common_head.*

class SettingsActivity : BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_settings

    override fun InitView() {
      tv_title.text ="设置"
        tv_edit.visibility = View.GONE
        iv_back.setOnClickListener { finish() }
        tv_default__down_dest_dir.text="${ROOT_DIR_PATH}"

    }

    override fun InitData() {
    }
}