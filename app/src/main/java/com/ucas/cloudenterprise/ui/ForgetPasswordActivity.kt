package com.ucas.cloudenterprise.ui

import android.content.Intent
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.StatusBarUtil
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import kotlinx.android.synthetic.main.activity_forget_password.*
import org.json.JSONObject
import java.util.regex.Pattern

class ForgetPasswordActivity : BaseActivity(), BaseActivity.OnNetCallback {
    var VerifcationCode="" //验证码
    var phone =""
     var mCountDownTimer :CountDownTimer? =null
    override fun GetContentViewId()= R.layout.activity_forget_password


    override fun InitView(){
        StatusBarUtil.setStatusDarkColor(window)
//        tv_title.text ="忘记密码"
        iv_back.setOnClickListener { finish() }

     }

    override fun InitData() {
    }

    fun getVerifcationCode(view: View) {
        if(TextUtils.isEmpty(editTextPhone.text)){
            Toastinfo("请输入手机号")
            return
        }

        if(!Pattern.compile("^1[3-9][0-9]{9}$").matcher(editTextPhone.text).matches()){
            Toastinfo("请输入正确的手机号")
            return
        }
        phone =editTextPhone.text.toString()
        NetRequest(URL_RESERT_PASS_MESSAGE, NET_PUT,HashMap<String,Any>().apply {
            put("telephone","${phone}")
        },this,this)


    }
    fun verifyMessageCode(view: View) {
        if(TextUtils.isEmpty(editTextPhone.text)){
            Toastinfo("请输入手机号")
            return
        }

        if(!VerifyUtils.VerifyPhone(editTextPhone.text.toString())){
            Toastinfo("请输入正确的手机号")
            return
        }
        if(TextUtils.isEmpty(editText_verification_code.text)){
            Toastinfo("请输入验证码")
            return
        }
        NetRequest(URL_RESERT_PASS_VERIFY, NET_POST,HashMap<String,Any>().apply {
          put("message_code","${editText_verification_code.text}")
          put("telephone","${editTextPhone.text}")
        },this,this)

    }
    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data).apply {
            if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){
                when(request?.url){
                    "${URL_RESERT_PASS_MESSAGE}"->{ //验证码
                            Toastinfo(getJSONObject("data").getString("send_status"))
                        tv_send_reset_password_message.apply {
                            text ="已发送"
                            isEnabled =false
                            mCountDownTimer= object :CountDownTimer(60 * 1000, 1000){
                                override fun onFinish() {
                                    isEnabled =true
                                    text ="重新获取验证码"
                                }

                                override fun onTick(millisUntilFinished: Long) {
                                    text = "已发送(" + millisUntilFinished / 1000 + ")"
                                }

                            }

                        }
                        mCountDownTimer!!.start()
                    }
                    "${URL_RESERT_PASS_VERIFY}"->{ //验证短信
                        //TODO 短信验证通过

                            Toastinfo("密码重置成功,新密码已发送至手机请注意查收"
//                                    + "新密码短信"
                            )
//                            Toastinfo("密码重置成功,请注意查收新密码短信")
                        mCountDownTimer?.cancel()
                            finish()
                        }


                    }
                }else{
                mCountDownTimer?.onFinish()
                mCountDownTimer?.cancel()
                Toastinfo(getString("message"))
            }



        }
    }



}
