package com.ucas.cloudenterprise.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.BuildConfig
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.ui.*
import com.ucas.cloudenterprise.ui.helpandfeedback.HelpAndFeedbackActivity
import com.ucas.cloudenterprise.ui.member.MembersManageActivity
import com.ucas.cloudenterprise.ui.message.MessageNotificationActivity
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.personal_center_fragment.*
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
            mContext?.startActivity<HelpAndFeedbackActivity>() }
        tv_about.setOnClickListener {
            mContext?.startActivity<AboutActivity>() }


        fl_check_new_version.setOnClickListener {
            (activity as MainActivity).CheckNewVersion()
            }
        tv_to_logout.setOnClickListener {
        //TODO
            mContext?.startActivity<LoginActivity>()
            activity?.finish()
            }


        tv_version.text = "V ${BuildConfig.VERSION_NAME}"


    }

    override fun initData() {

        NetRequest(URL_GET_COMPANY_INFO+"${USER_ID}", NET_GET,null,this,this)
    }

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