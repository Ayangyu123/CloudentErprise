package com.ucas.cloudenterprise.ui.member

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*

class EditMemberInviteLinkActivity: BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_edit_member_invite_link

    override fun InitView() {
      tv_edit.visibility = View.GONE
        tv_title.text ="修改邀请链接"
        iv_back.setOnClickListener { finish() }
    }

    override fun InitData() {
    }
}