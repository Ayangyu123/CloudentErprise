package com.ucas.cloudenterprise.ui

import android.content.Intent
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_setcommonfile.*
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
            startActivity<PermissionToIllustrateActivity>()
        }
        tv_add_seeable.setOnClickListener {
            startActivityForResult(Intent(this,SelectMembersActivity::class.java),1)
        }

    }

    override fun InitData() {  }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1&&data!=null){

        }
    }
}