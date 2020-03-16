package com.ucas.cloudenterprise.ui

import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.editText_verification_code
import kotlinx.android.synthetic.main.activity_reset_password.tv_send_reset_password_message
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

/**
@author simpler
@create 2020年03月01日  20:45
 */
class EditPersonalInfoActivity : BaseActivity(), BaseActivity.OnNetCallback {
   var acc_name: String=""
   var email: String=""
    var phone =""


    override fun GetContentViewId()= R.layout.activity_reset_password

    override fun InitView() {
            tv_title.text ="个人信息修改"
        tv_edit.visibility =View.INVISIBLE
        iv_back.setOnClickListener {
            finish()
        }


      }

    override fun InitData() {
        GetPersonalInfo()
    }

    //<editor-fold desc="获取个人信息">
    private fun GetPersonalInfo() {
        NetRequest("${URL_GET_USER_INFO}${USER_ID}", NET_GET,null,this,object :BaseActivity.OnNetCallback{
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                if(JSONObject(data).getInt("code")== REQUEST_SUCCESS_CODE){

                    JSONObject(data).getJSONObject("data").apply {

                        acc_name = getString("acc_name")
                        phone = getString("telphone")
                        email = getString("email")
                        et_acc_name.text = SetEt_Text(getString("acc_name"))
                        editText_phone.text = SetEt_Text(getString("telphone"))

                        phone = getString("telphone")
                        editText_eamil.text=SetEt_Text(getString("email"))
                    }
                }
            }

        })
    }
    //</editor-fold>

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {

            JSONObject(data).apply {
                if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){
                    when(request?.url){
                        URL_POST_USER_INFO_MODIFY->{
                            Toastinfo("信息修改成功")
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

//  TODO      if(){}


        NetRequest(URL_POST_USER_INFO_MODIFY, NET_POST,HashMap<String,Any>().apply {//TODO
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

        //<editor-fold desc="发送短信验证码">
    fun SendVerifyCode(view: View) {

            if(TextUtils.isEmpty(phone)){
                Toastinfo("手机号为空")
                return
            }

            NetRequest("${URL_POST_SEND_MESSAGE_CODE}", NET_POST,HashMap<String,Any>().apply{
                put("telephone",   editText_phone.text.toString() )
                put("type", SEND_MESSAGE_CODE_TYPE_RESET_PASSWORD)
            },this,object :BaseActivity.OnNetCallback{
                override fun OnNetPostSucces(
                    request: Request<String, out Request<Any, Request<*, *>>>?,
                    data: String
                ) {
                    if(VerifyUtils.VerifyRequestData(data)){
                        Toastinfo(JSONObject(data).getJSONObject("data").getString("send_status"))
                        tv_send_reset_password_message.apply {
                            text ="已发送"
                            isEnabled =false
                            object : CountDownTimer(60 * 1000, 1000){
                                override fun onFinish() {
                                    isEnabled =true
                                    text ="重新获取验证码"
                                }

                                override fun onTick(millisUntilFinished: Long) {
                                    text = "已发送(" + millisUntilFinished / 1000 + ")"
                                }

                            }.start()
                        }
                    }

                }

            })

        }
    //</editor-fold >
}