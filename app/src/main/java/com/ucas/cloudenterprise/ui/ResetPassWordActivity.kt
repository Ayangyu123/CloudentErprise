package com.ucas.cloudenterprise.ui

import android.text.TextUtils
import android.view.View
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.Toastinfo
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
//            tv_title.text ="重置密码"
            tv_title.text ="个人信息修改"
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
        if(TextUtils.isEmpty(editText_phone.text)){
            Toastinfo("请输入手机号")
            return
        }


        if(TextUtils.isEmpty(et_acc_name.text)){
            Toastinfo("请输入请输入用户名称")
            return
        }

        if(TextUtils.isEmpty(editText_eamil.text)){
            Toastinfo("请输入新密码")
            return
        }

        if(TextUtils.isEmpty(editTextnewpassword.text)){
            Toastinfo("请输入新密码")
            return
        }
        if(TextUtils.isEmpty(editText_verification_code.text)){
            Toastinfo("请输入验证码")
            return
        }



        NetRequest(URL_RESERT_PASSWORD, NET_POST,HashMap<String,Any>().apply {//TODO
            put("user_id","${USER_ID}")
            put("telephone","${editText_phone.text}")
            put("email","${editText_eamil.text}")
            put("password","${editTextnewpassword.text}")
            put("acc_name","${et_acc_name.text}")
            put("verification_code","${editText_verification_code.text}")
        },this,object :BaseActivity.OnNetCallback{
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                if(JSONObject(data).getInt("code") == REQUEST_SUCCESS_CODE){
                    Toastinfo("修改成功")
                    finish()
                }else{
                    Toastinfo(JSONObject(data).getString("message"))
                }

            }
        })
    }
}