package com.ucas.cloudenterprise.ui

import android.text.TextUtils
import android.view.View
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.NET_POST
import com.ucas.cloudenterprise.app.REQUEST_SUCCESS_CODE
import com.ucas.cloudenterprise.app.URL_RESERT_PASSWORD
import com.ucas.cloudenterprise.app.URL_RESERT_PASS_VERIFY
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

/**
@author simpler
@create 2020年03月01日  20:45
 */
class ResetPassWordActivity : BaseActivity(), BaseActivity.OnNetCallback {
    var phone =""


    override fun GetContentViewId()= R.layout.activity_reset_password

    override fun InitView() {
        phone= intent.getStringExtra("phone")
            tv_title.text ="重置密码"
        tv_edit.visibility =View.INVISIBLE
        iv_back.setOnClickListener {
            finish()
        }


      }

    override fun InitData() {
    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {

            JSONObject(data).apply {
                if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){
                    when(request?.url){
                        URL_RESERT_PASSWORD->{
                            Toastinfo("密码重置成功")
                            finish()
                        }
                    }
                }
            }
    }

    fun resetpassword(view: View) {

        if(TextUtils.isEmpty(editTextcurrpassword.text)){
            Toastinfo("请输入当前密码")
            return
        }

        if(TextUtils.isEmpty(editTextnewpassword.text)){
            Toastinfo("请输入新密码")
            return
        }

        if(TextUtils.isEmpty(et_confirm_password.text)){
            Toastinfo("请输入新密码")
            return
        }

        if(!et_confirm_password.text.equals(editTextnewpassword.text)){
            Toastinfo("两次新密码不一致")
            return
        }

        NetRequest(URL_RESERT_PASSWORD, NET_POST,HashMap<String,Any>().apply {//TODO
            put("telephone","${editText_verification_code.text}")
            put("password","${et_confirm_password.text}")
        },this,this)
    }
}