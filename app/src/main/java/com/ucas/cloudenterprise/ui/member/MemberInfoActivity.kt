package com.ucas.cloudenterprise.ui.member

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.BelongTeam
import com.ucas.cloudenterprise.model.MemberInfo
import kotlinx.android.synthetic.main.activity_member_info.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.item_team.view.*

class MemberInfoActivity : BaseActivity() {
    lateinit  var mMemberInfo_Item :MemberInfo
    override fun GetContentViewId()= R.layout.activity_member_info

    override fun InitView() {

        mMemberInfo_Item =  intent.getSerializableExtra("item") as MemberInfo
        mMemberInfo_Item.apply {
            //TODO
            tv_title.text = acc_name+""
            tv_user_name.text =acc_name+""
            tv_acc_name.text =acc_name+""
            tv_cap.text =capacity.toString()+""
            tv_phone.text = telphone+""
            tv_email.text =email+""
            var tv_temname: View
            belong_team?.apply {
                if(belong_team.isEmpty()){


                    tv_temname= LayoutInflater.from(this@MemberInfoActivity).inflate(R.layout.item_team,null)
                    tv_temname.tv_team_name.text="无"
                    ll_teams.addView(tv_temname)


                }else{
                    for(item:BelongTeam in belong_team){
                        tv_temname= LayoutInflater.from(this@MemberInfoActivity).inflate(R.layout.item_team,null)
                        tv_temname.tv_team_name.text= item.team_name
                        ll_teams.addView(tv_temname)
                    }
                }
            }



        }

        iv_back.setOnClickListener { finish() }
        tv_edit.setOnClickListener {
            startActivity(Intent(this,MemberInfoEditActivity::class.java).apply {
                putExtra("item",mMemberInfo_Item)
            }
            )
            finish()
        }

    }

    override fun InitData() {
    }
}