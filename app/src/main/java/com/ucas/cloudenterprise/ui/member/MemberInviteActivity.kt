package com.ucas.cloudenterprise.ui.member

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_member_invite.*
import kotlinx.android.synthetic.main.common_head.*

class MemberInviteActivity: BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_member_invite

    override fun InitView() {
      tv_edit.visibility = View.GONE
        tv_title.text ="邀请成员"
        iv_back.setOnClickListener { finish() }
        tv_edit_link.setOnClickListener {
            startActivity<EditMemberInviteLinkActivity>()
        }

    }

    override fun InitData() {
    }
}