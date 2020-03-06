package com.ucas.cloudenterprise.ui

import android.content.Intent
import android.view.View
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_setcommonfile.*
import kotlinx.android.synthetic.main.common_head.*

class SetCommonFileActivity:BaseActivity(), BaseActivity.OnNetCallback {
    companion object{
        val ADD_CAN_EDIT=1
        val ADD_CAN_UPLOAD=2
        val ADD_CAN_SEE=3
    }
    lateinit var  fileitem:File_Bean//文件



    override fun GetContentViewId()= R.layout.activity_setcommonfile



    override fun InitView() {
        tv_title.text = "设置共享"
        fileitem = intent.getSerializableExtra("file") as File_Bean


        iv_back.setOnClickListener {
            finish()
        }
        tv_edit.text ="权限说明"
        tv_edit.setOnClickListener {
            startActivity<PermissionToIllustrateActivity>()
        }


        tv_add_can_edit.setOnClickListener {
            startActivityForResult(Intent(this,SelectMembersActivity::class.java), ADD_CAN_EDIT)
        }
        tv_add_can_upload.setOnClickListener {
            startActivityForResult(Intent(this,SelectMembersActivity::class.java), ADD_CAN_UPLOAD)
        }
        tv_add_can_see.setOnClickListener {
            startActivityForResult(Intent(this,SelectMembersActivity::class.java), ADD_CAN_SEE)
        }

    }

    override fun InitData() {
        fileitem?.store?.apply {
            when(fileitem.store){
                //<editor-fold desc="收藏过">
                -1->{
                    ll_can_edit.visibility = View.VISIBLE
                        tv_able_edit_count.text =""
                    ll_can_upload.visibility = View.VISIBLE
//                       t.text =""
                    ll_can_see.visibility = View.VISIBLE
//                    ll_can_stv_cancel_commom.visibility = View.VISIBLE
                       tv_able_edit_count.text =""

                }
                //<editor-fold desc="">

                //<editor-fold desc="没有收藏过">
                1->{
                    tv_add_can_edit.visibility = View.VISIBLE
                    tv_add_can_upload.visibility = View.VISIBLE
                    tv_add_can_see.visibility = View.VISIBLE
                    NetRequest("",0,null,this,object:BaseActivity.OnNetCallback{
                        override fun OnNetPostSucces(
                            request: Request<String, out Request<Any, Request<*, *>>>?,
                            data: String
                        ) {

                        }

                    })
                }
                //<editor-fold desc="">
            }
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1&&data!=null){

        }
    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {

    }
}