package com.ucas.cloudenterprise.ui

import android.text.TextUtils
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.activity_simple_feedback.*
import kotlinx.android.synthetic.main.common_head.*

/**
@author simpler
@create 2020年03月23日  17:02
 */
class SimpleFeedBackAcitivity:BaseActivity() {
//意见反馈
    override fun GetContentViewId()= R.layout.activity_simple_feedback

    override fun InitView() {
        iv_back.setOnClickListener { finish() }
        tv_title.text ="意见反馈"
        tv_edit.visibility=View.GONE

    }

    override fun InitData() {

    }

    fun commit_feedback(view: View) {
        if(TextUtils.isEmpty(et_feedback.text)){
           Toastinfo("请入您宝贵的意见")
            return
        }
        Toastinfo("您的意见已提交")
        et_feedback.text =SetEt_Text("")
        return
    }
}