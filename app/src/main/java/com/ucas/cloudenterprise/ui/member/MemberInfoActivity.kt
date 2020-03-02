package com.ucas.cloudenterprise.ui.member

import android.view.LayoutInflater
import android.widget.TextView
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.BelongTeam
import com.ucas.cloudenterprise.model.MemberInfo
import kotlinx.android.synthetic.main.activity_member_info.*
import kotlinx.android.synthetic.main.common_head.*

class MemberInfoActivity : BaseActivity() {
    lateinit  var mMemberInfo_Item :MemberInfo
    override fun GetContentViewId()= R.layout.activity_member_info

    override fun InitView() {

        mMemberInfo_Item =  intent.getSerializableExtra("item") as MemberInfo
        mMemberInfo_Item.apply {
            //TODO
            tv_title.text = acc_name
            tv_user_name.text =acc_name
            tv_acc_name.text =acc_name
            tv_cap.text =capacity
            tv_phone.text = telphone
            tv_email.text =email
           if(!belong_team.isEmpty()&&belong_team.size<0){
               var tv_temname:TextView
               for(item:BelongTeam in belong_team){
                   tv_temname= LayoutInflater.from(this@MemberInfoActivity).inflate(R.layout.item_team,null) as TextView
                    tv_temname.text= item.belong_teamId
                   ll_teams.addView(tv_temname)
               }

           }


        }

        iv_back.setOnClickListener { finish() }
        tv_edit.setOnClickListener {
            //TODO
        }

    }

    override fun InitData() {
    }
}