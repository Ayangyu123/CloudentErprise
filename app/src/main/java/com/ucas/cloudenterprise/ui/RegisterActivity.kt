package com.ucas.cloudenterprise.ui

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.Company
import com.ucas.cloudenterprise.model.Resource
import com.ucas.cloudenterprise.utils.StatusBarUtil
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.et_user_password
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject
import kotlin.collections.HashMap


/**
@author simpler
@create 2020年01月06日  10:10
 */
class RegisterActivity : BaseActivity(), BaseActivity.OnNetCallback {

    override fun GetContentViewId()=R.layout.activity_register

    override fun InitView() {
//        StatusBarUtil.setStatusDarkColor(window)
        iv_back.setOnClickListener {
            finish()
        }
        tv_title.text ="企业注册"
        tv_edit.visibility = View.GONE
    }

    override fun InitData() {}

    val TAG = "RegisterActivity"


    fun register(view: View) {
        Log.e(TAG,"comp_name=${et_comp_name.text}")
        Log.e(TAG,"comp_con_email=${et_comp_con_email.text}")
        Log.e(TAG,"comp_con_tel=${et_comp_con_tel.text}")
        Log.e(TAG,"vip_type=${vip_type}")
        if(TextUtils.isEmpty(et_comp_name.text.toString())){
            Toastinfo("请输入公司名称")
            return
        }

        if(TextUtils.isEmpty(et_comp_con_tel.text.toString())){
            Toastinfo("请输入公司联系手机号码")
            return
        }
        if(!VerifyUtils.VerifyPhone(et_comp_con_tel.text.toString())){
            Toastinfo("请输入正确的手机号码")
            return
        }


        //TODO  密码验证 待细化

        var password =  et_user_password.text.toString()
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

        var confirm_password =  et_confirm_user_password.text.toString()
        if(TextUtils.isEmpty(confirm_password)){
            Toastinfo("请确认密码")
            return
        }
        if(!confirm_password.equals(password)){
            Toastinfo("两次密码不一致")
            return
        }

        if(!check_box_register_able.isChecked){
           Toastinfo("请阅读服务协议后勾选")
            return
        }


        val params = HashMap<String,Any>()
        params["comp_name"] = "${et_comp_name.text}"
        params["comp_con_email"] = "${et_comp_con_email.text}"
        params["comp_con_tel"] = "${et_comp_con_tel.text}"
        params["vip_type"] = vip_type
        params["password"] = MD5encode("${et_user_password.text}",true)
        val json = JSONObject(params as Map<String, Any>)
        NetRequest(URL_REGISTER_COMPANY, NET_POST,params,this,this)

    }
    override fun OnNetPostSucces(request: Request<String, out Request<Any, Request<*, *>>>?, data: String) {
            when(request?.url){
                URL_REGISTER_COMPANY ->{
                    if (JSONObject(data).isNull("data")){
                        Toastinfo("注册失败")
                        return
                    }
                        Toastinfo("注册成功")
                    var company = Gson().fromJson(JSONObject(data).getJSONObject("data").toString(),Company::class.java)
                    Log.e(TAG,company.toString())
                    COMP_ID = company.comp_id
                    COMP = company
                    //TODO
                    finish()
                }


            }

//        tv_result.text = data
//        AddToken("Token_test")
//        startActivity<MainActivity>()


    }


    fun ToTeamActivity(view: View) = startActivity<TeamAcitvity>()
    fun GetToken(view: View) {

        val id = "221670472561987584"
        val params = HashMap<String,Any>()
        params["user_id"] = "${id}"
        params["password"] = "F2Xw66"
        NetRequest(URL_LOGIN, NET_POST,params,this,this)

    }

    fun GotoWebActivity(view: View) = startActivity<CommonWebActivity>()


}