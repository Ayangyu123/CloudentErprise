package com.ucas.cloudenterprise.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.BuildConfig
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.*
import com.ucas.cloudenterprise.ui.helpandfeedback.HelpAndFeedbackActivity
import com.ucas.cloudenterprise.ui.member.MembersManageActivity
import com.ucas.cloudenterprise.ui.message.MessageNotificationActivity
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.personal_center_fragment.*
import kotlinx.android.synthetic.main.personal_center_fragment.tv_reset_password
import org.json.JSONObject

/**
@author simpler
@create 2020年01月10日  14:31
 */
class PersonalCenterFragment: BaseFragment(), BaseActivity.OnNetCallback {


    override fun initView() {

        tv_member_manager.setOnClickListener {
            mContext?.startActivity<MembersManageActivity>() }

            if(IS_ROOT){
                tv_member_manager.visibility =View.VISIBLE
            }else {
                tv_member_manager.visibility =View.GONE
            }
        tv_reset_password.setOnClickListener {
            mContext?.startActivity<EditPersonalInfoActivity>()
        }

        tv_message_notification.setOnClickListener {
            mContext?.startActivity<MessageNotificationActivity>() }
        tv_setting.setOnClickListener {
            mContext?.startActivity<SettingsActivity>() }
        tv_user_feedback.setOnClickListener {
            mContext?.startActivity<SimpleFeedBackAcitivity>()
//            mContext?.startActivity<HelpAndFeedbackActivity>()
        }
        tv_about.setOnClickListener {
            mContext?.startActivity<AboutActivity>() }



        fl_check_new_version.setOnClickListener {
            (activity as MainActivity).CheckNewVersion()
            }
        tv_to_logout.setOnClickListener {
        //TODO
            MyApplication.upLoad_Ing.clear()
            MyApplication.upLoad_completed.clear()
            MyApplication.downLoad_Ing.clear()
            MyApplication.downLoad_completed.clear()
            activity as MainActivity
            (activity as MainActivity).myBinder?.mDaemonService?.savaspall()
//            MyApplication.getInstance().GetSP().edit().apply {
//                putString("downLoad_Ing", Gson().toJson(MyApplication.downLoad_Ing))
//                putString("downLoad_completed", Gson().toJson(MyApplication.downLoad_completed))
//                putString("upLoad_Ing", Gson().toJson(MyApplication.upLoad_Ing))
//                putString("upLoad_completed", Gson().toJson(MyApplication.upLoad_completed))
//                apply()
//            }

            mContext?.startActivity<LoginActivity>()
            activity?.finish()
            }


        tv_version.text = "V ${BuildConfig.VERSION_NAME}"


    }

    override fun initData() {


        GetPersonalInfo()

//        NetRequest(URL_GET_COMPANY_INFO+"${USER_ID}", NET_GET,null,this,this)
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


                        tv_cap_info.text ="容量：${getInt("used_cap")}GB/${getInt("total_cap")}GB"
                        progressbar_cap.apply {
//                            capacity
                            max=getInt("total_cap")*100
                            progress = getInt("used_cap")*100
                        }

                        tv_user_name.text = getString("acc_name")
//                       var  phone = getString("telphone")
//                       var  email = getString("email")
//                        et_acc_name.text = SetEt_Text(getString("acc_name"))
//                        editText_phone.text = SetEt_Text(getString("telphone"))
//
//                        phone = getString("telphone")
//                        editText_eamil.text= SetEt_Text(getString("email"))
                    }
                }
            }

        })
    }
    //</editor-fold>

    override fun GetRootViewID()= R.layout.personal_center_fragment
    companion object{
         private var instance: PersonalCenterFragment? = null

        fun getInstance( param1:Boolean,  param2:String?): PersonalCenterFragment {
            if (instance == null) {
                synchronized(PersonalCenterFragment::class.java) {
                    if (instance == null) {
                        instance = PersonalCenterFragment().apply {
                            this.arguments =Bundle().apply {
                                putBoolean("param1",param1)
                                putString("param2",param2)
                            }
                        }
                    }
                }
            }
            return instance!!
        }
    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
            JSONObject(data).apply {
               if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){
                   this.getJSONObject("data").apply {
                       COMP_ID = "${getLong("comp_id")}"
                       tv_user_name.text =getString("comp_name")
                       tv_company_name.text =getString("comp_name")
                       tv_cap_info.text ="容量：${getInt("used_cap")}GB/${getInt("total_cap")}GB"
                        progressbar_cap.apply {
                            max=getInt("total_cap")*100
                            progress = getInt("used_cap")*100
                        }
                   }




               }
            }
            }


}