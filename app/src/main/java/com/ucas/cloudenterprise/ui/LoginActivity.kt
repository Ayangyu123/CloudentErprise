package com.ucas.cloudenterprise.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

/**
@author simpler
@create 2020年01月08日  10:34
 */
class LoginActivity :BaseActivity(),BaseActivity.OnNetCallback {
    val TAG = "LoginActivity"

    var phone :String?=null
    override fun OnNetPostSucces(request: Request<String, out Request<Any, Request<*, *>>>?, data: String) {
        Toastinfo("登陆成功")
        //TODO 添加/更新Token
        if(JSONObject(data).isNull("data")){
            return
        }
            SaveToken(JSONObject(data).getJSONObject("data").toString())

    }

    private fun SaveToken(data: String) {
        ACCESS_TOKEN = JSONObject(data).getString("access_token")
        REFRESH_TOKEN = JSONObject(data).getString("refresh_token")
        USER_ID = JSONObject(data).getString("user_id")
        COMP_ID = JSONObject(data).getString("company_id")
        IS_ROOT = USER_ID.equals(COMP_ID)

        Log.e(TAG, "ACCESS_TOKEN=${ACCESS_TOKEN}")
        Log.e(TAG, "refresh_token=${REFRESH_TOKEN}")
        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE, Context.MODE_PRIVATE).edit()
            .putString("access_token", ACCESS_TOKEN).putString("refresh_token", REFRESH_TOKEN)
            .putString("last_login_user_name", phone)
            .commit()

        AddToken(ACCESS_TOKEN)
        //TODO 加密
//       APP_ID = JSONObject(data).getString("app_id")
//       LOGID = JSONObject(data).getString("logid")
//        CLIENTTYPE = JSONObject(data).getInt("clienttype").toString()
//        OkGo.getInstance().addCommonHeaders(HttpHeaders("app_id",APP_ID))
//        OkGo.getInstance().addCommonHeaders(HttpHeaders("logid",LOGID))
//        OkGo.getInstance().addCommonHeaders(HttpHeaders("clienttype",CLIENTTYPE))
        Log.e(TAG, "token  is ${OkGo.getInstance().commonHeaders.get("Authorization")}")
//        startActivity<PacaktestActivity>()
        startActivity<MainActivity>()
        finish()
    }

    override fun InitView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           startForegroundService( Intent(this, DaemonService::class.java))
        } else {
//            startService( Intent(this, ServedService.class));
            startService<DaemonService>()
        }

         phone = getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE, Context.MODE_PRIVATE).getString("last_login_user_name", "")

        et_user_name.text = SetEt_Text("${phone}")
//        et_user_password.text = SetEt_Text("${password_param}")
    }



    override fun InitData() {}

    override fun GetContentViewId(): Int = R.layout.activity_login
    fun Login(view: View) {
        phone= "${et_user_name.text.toString()}"
        if(TextUtils.isEmpty(phone)){
            Toastinfo("请输入手机号")
            return
        }
        if(!VerifyUtils.VerifyPhone(phone!!)){
            Toastinfo("请输入正确手机号")
            return
        }

        //TODO  密码验证 待细化
        var password =  et_user_password.text.toString()
        Log.e("ok","et_user_password.text.length  ="+et_user_password.text.length)
        Log.e("ok","password.length  ="+password.length)


        if(TextUtils.isEmpty(password)){
            Toastinfo("请输入密码")
            return
        }

        if(password.length<MIN_PASSWORD_LENGTH){
            Toastinfo("密码不低于${MIN_PASSWORD_LENGTH}位")
            return
        }
        if(password.length> MAX_PASSWORD_LENGTH){
            Toastinfo("密码不能超过${MAX_PASSWORD_LENGTH}位")
            return
        }


        val params = HashMap<String,Any>()

        params["mobile"] = "${phone}"
        params["password"] = MD5encode("${password}",true)
        NetRequest(URL_LOGIN, NET_POST,params,this,this)
    }

    fun ToRegisterActivity(view: View) {
        startActivity<RegisterActivity>()

    }

    fun ToForgetPassword(view: View) {
        startActivity<ForgetPasswordActivity>()

    }
}