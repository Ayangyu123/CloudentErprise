package com.ucas.cloudenterprise.ui.fragment

import android.os.Bundle
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.ui.AboutActivity
import com.ucas.cloudenterprise.ui.LoginActivity
import com.ucas.cloudenterprise.ui.SettingsActivity
import com.ucas.cloudenterprise.ui.helpandfeedback.FeedbackInfoActivity
import com.ucas.cloudenterprise.ui.helpandfeedback.HelpAndFeedbackActivity
import com.ucas.cloudenterprise.ui.member.MembersManageActivity
import com.ucas.cloudenterprise.ui.message.MessageNotificationActivity
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.personal_center_fragment.*

/**
@author simpler
@create 2020年01月10日  14:31
 */
class PersonalCenterFragment: BaseFragment() {


    override fun initView() {
        tv_member_manager.setOnClickListener {
            mContext?.startActivity<MembersManageActivity>() }
        tv_message_notification.setOnClickListener {
            mContext?.startActivity<MessageNotificationActivity>() }
        tv_setting.setOnClickListener {
            mContext?.startActivity<SettingsActivity>() }
        tv_user_feedback.setOnClickListener {
            mContext?.startActivity<HelpAndFeedbackActivity>() }
        tv_about.setOnClickListener {
            mContext?.startActivity<AboutActivity>() }
        fl_check_new_version.setOnClickListener {
        //TODO
            }
        tv_to_logout.setOnClickListener {
        //TODO
            mContext?.startActivity<LoginActivity>()
            activity?.finish()
            }



    }

    override fun initData() {}

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

}