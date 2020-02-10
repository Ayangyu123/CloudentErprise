package com.ucas.cloudenterprise.ui

import android.content.Context
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.common_head.*

class SelectMembersActivity:BaseActivity() {
    override fun GetContentViewId()= R.layout.activity_select_members

    override fun InitView() {
    tv_title.text ="选择可成员"//TODO
        iv_back.apply {
            setImageResource(R.drawable.title_close_icon_normal)
            setOnClickListener { finish() }
        }
        iv_title_search.apply {
            tv_edit.visibility = View.GONE
            visibility = View.VISIBLE
            setOnClickListener {
//                startActivity<>()//TODO
            }
        }

    }

    override fun InitData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}