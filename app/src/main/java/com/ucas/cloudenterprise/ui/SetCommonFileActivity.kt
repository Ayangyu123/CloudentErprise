package com.ucas.cloudenterprise.ui

import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*

class SetCommonFileActivity:BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_setcommonfile



    override fun InitView() {
        tv_title.text = "设置共享"
        iv_back.setOnClickListener {
            finish()
        }
        tv_edit.text ="权限说明"
        tv_edit.setOnClickListener {
//            startActivity<>()
        }


    }

    override fun InitData() {  }
}