package com.ucas.cloudenterprise.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

/**
@author simpler
@create 2020年01月08日  10:34
 */
class LoginActivity :BaseActivity(),BaseActivity.OnNetCallback {
    val TAG = "LoginActivity"

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


        Log.e(TAG, "ACCESS_TOKEN=${ACCESS_TOKEN}")
        Log.e(TAG, "refresh_token=${REFRESH_TOKEN}")
        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE, Context.MODE_PRIVATE).edit()
            .putString("access_token", ACCESS_TOKEN).putString("refresh_token", REFRESH_TOKEN)
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
        startActivity<MainActivity>()
        finish()
    }

    override fun InitView() {
        et_user_name.text = SetEt_Text("${user_name_param}")
        et_user_password.text = SetEt_Text("${password_param}")
    }



    override fun InitData() {}

    override fun GetContentViewId(): Int = R.layout.activity_login
    fun Login(view: View) {
        val params = HashMap<String,Any>()
        params["mobile"] = "${et_user_name.text.toString()}"
        params["password"] = MD5encode("${et_user_password.text.toString()}",true)
        NetRequest(URL_LOGIN, NET_POST,params,this,this)
    }

    fun ToRegisterActivity(view: View) {
        startActivity<RegisterActivity>()

    }

    fun ToForgetPassword(view: View) {
        startActivity<ForgetPasswordActivity>()

    }
}