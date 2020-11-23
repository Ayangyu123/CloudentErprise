package com.ucas.cloudenterprise.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.BuildConfig
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.event.MessageEvent
import com.ucas.cloudenterprise.ui.*
import com.ucas.cloudenterprise.ui.helpandfeedback.HelpAndFeedbackActivity
import com.ucas.cloudenterprise.ui.member.MembersManageActivity
import com.ucas.cloudenterprise.ui.message.MessageNotificationActivity
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.personal_center_fragment.*
import kotlinx.android.synthetic.main.personal_center_fragment.tv_reset_password
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
//个人中心
/**
@author simpler
@create 2020年01月10日  14:31
 */
class PersonalCenterFragment : BaseFragment(), BaseActivity.OnNetCallback {


    override fun initView() {

        tv_member_manager.setOnClickListener {
            mContext?.startActivity<MembersManageActivity>()
        }

        if (IS_ROOT) {
            tv_member_manager.visibility = View.VISIBLE
        } else {
            tv_member_manager.visibility = View.GONE
        }
        tv_reset_password.setOnClickListener {
            mContext?.startActivity<EditPersonalInfoActivity>()
        }

        tv_message_notification.setOnClickListener {
            mContext?.startActivity<MessageNotificationActivity>()
        }
        tv_setting.setOnClickListener {
            mContext?.startActivity<SettingsActivity>()
        }
        tv_user_feedback.setOnClickListener {
            mContext?.startActivity<SimpleFeedBackAcitivity>()
        }
        tv_about.setOnClickListener {
            mContext?.startActivity<AboutActivity>()
        }



        fl_check_new_version.setOnClickListener {
            (activity as MainActivity).CheckNewVersion()
        }
        tv_to_logout.setOnClickListener {
            //TODO
            OkGo.put<String>(URL_LOGOUT).tag(this).execute(
                object : StringCallback() {
                    override fun onSuccess(response: Response<String>?) {
                        response?.body()?.apply {
                            if (VerifyUtils.VerifyResponseData(this)) {
                                MyApplication.upLoad_Ing.clear()
                                MyApplication.upLoad_completed.clear()
                                MyApplication.downLoad_Ing.clear()
                                MyApplication.downLoad_completed.clear()

                                MyApplication.getInstance().GetSP().edit()
                                    .remove(ACCESS_TOKEN)
                                    .remove(REFRESH_TOKEN)

                                OkGo.getInstance().commonHeaders.clear()
                                activity as MainActivity
                                (activity as MainActivity).myBinder?.mDaemonService?.savaspall()


                                mContext?.startActivity<LoginActivity>()
                                activity?.finish()
                            } else {
                                Toastinfo("${JSONObject(this).getString("message")}")
                            }

                        }

                    }
                }
            )
        }
        tv_version.text = "V ${BuildConfig.VERSION_NAME}"


    }

    override fun initData() {


        GetPersonalInfo()
    }

    //<editor-fold desc="获取个人信息">
    private fun GetPersonalInfo() {

        OkGo.get<String>("${URL_GET_USER_INFO}${USER_ID}").tag(this)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.body()?.apply {
                        var data = this
                        if (JSONObject(data).getInt("code") == REQUEST_SUCCESS_CODE) {

                            JSONObject(data).getJSONObject("data").apply {

                                var used_cap = getLong("used_cap")
                                var total_cap = getLong("total_cap")

                                var used_cap_info = FormatFileSize(used_cap)
                                var total_cap_info = FormatFileSize(total_cap)


                                tv_cap_info.text = "容量：${used_cap_info}/${total_cap_info}"
                                progressbar_cap.apply {
                                    max = 100
                                    progress =
                                        ((used_cap.toFloat() / total_cap.toFloat()) * 100).toInt()
                                }

                                tv_user_name.text = getString("acc_name")

                            }
                        }
                    }

                }
            })


    }
    //</editor-fold>

    override fun GetRootViewID() = R.layout.personal_center_fragment

    companion object {
        private var instance: PersonalCenterFragment? = null

        fun getInstance(param1: Boolean, param2: String?): PersonalCenterFragment {
            if (instance == null) {
                synchronized(PersonalCenterFragment::class.java) {
                    if (instance == null) {
                        instance = PersonalCenterFragment().apply {
                            this.arguments = Bundle().apply {
                                putBoolean("param1", param1)
                                putString("param2", param2)
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
            if (!isNull("data") && getInt("code") == REQUEST_SUCCESS_CODE) {
                this.getJSONObject("data").apply {
                    COMP_ID = "${getLong("comp_id")}"
                    tv_user_name.text = getString("comp_name")
                    tv_company_name.text = getString("comp_name")
                    tv_cap_info.text = "容量：${getInt("used_cap")}GB/${getInt("total_cap")}GB"
                    progressbar_cap.apply {
                        max = getInt("total_cap") * 100
                        progress = getInt("used_cap") * 100
                    }
                }


            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        GetPersonalInfo()

        /* Do something */
    };

}
