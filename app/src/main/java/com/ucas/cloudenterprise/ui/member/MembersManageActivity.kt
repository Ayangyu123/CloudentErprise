package com.ucas.cloudenterprise.ui.member

import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*

class MembersManageActivity: BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_membersmanage

    override fun InitView() {
     tv_title.text ="成员管理"
        iv_back.setOnClickListener { finish() }
        iv_title_search.apply {
            tv_edit.visibility = View.GONE
            visibility = View.VISIBLE
            setOnClickListener {
//                startActivity<>()
            }
        }
    }

    override fun InitData() {

    }
}