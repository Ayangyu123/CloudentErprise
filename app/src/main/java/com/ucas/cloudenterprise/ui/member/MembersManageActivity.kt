package com.ucas.cloudenterprise.ui.member

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.MemberInfoAdapter
import com.ucas.cloudenterprise.app.COMP_ID
import com.ucas.cloudenterprise.app.NET_GET
import com.ucas.cloudenterprise.app.URL_LIST_MEMBER
import com.ucas.cloudenterprise.app.URL_TEAM

import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.model.MemberInfo
import com.ucas.cloudenterprise.utils.intent
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_membersmanage.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

class MembersManageActivity: BaseActivity(), BaseActivity.OnNetCallback {

      var mMemberlist:ArrayList<MemberInfo> =ArrayList()
      lateinit   var adapter: MemberInfoAdapter

    override fun GetContentViewId()= R.layout.activity_membersmanage

    override fun InitView() {
     tv_title.text ="成员管理"
        iv_back.setOnClickListener { finish() }
        iv_title_search.apply {
            tv_edit.visibility = View.GONE
            visibility = View.VISIBLE
            setOnClickListener {
                startActivity(Intent(this@MembersManageActivity,MemberSearchActivity::class.java))
            }
        }
        tv_invite_member.setOnClickListener {
            startActivity<MemberInviteActivity>()
        }
        tv_member_audit.setOnClickListener {
            startActivity<MemberAuditActivity>()
        }

        adapter=MemberInfoAdapter(this,mMemberlist)
        rc_members.adapter =adapter
        adapter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                   var holder_item = holder as MemberInfoAdapter.ViewHolder
                holder_item.apply {
                    //TODO
                    tv_name.text = mMemberlist[position].acc_name
                    tv_state.text =mMemberlist[position].acc_name
                    ll_root.setOnClickListener {
                        startActivity(intent<MemberInfoActivity>().apply {
                            putExtra("item", mMemberlist[position])
                        })
                    }


                }
            }
        })

    }

    override fun InitData() {
       //TODO 获取成员list /api/cloud/v1/member_list/company/:pid/mem/:mid
         NetRequest(URL_LIST_MEMBER +"company/$COMP_ID", NET_GET,null,this,this)

    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        Log.e("ok",data)
        JSONObject(data).apply {
            if(!isNull("data")&&getInt("code")==200){
                mMemberlist.addAll(Gson().fromJson<List<MemberInfo>>(JSONObject(data).getJSONArray("data").toString(),object : TypeToken<List<MemberInfo>>(){}.type) as ArrayList<MemberInfo>)
            }
        }
    }
}