package com.ucas.cloudenterprise.ui.member

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.MemberInfoAdapter
import com.ucas.cloudenterprise.app.*

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
//        rc_members.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                   var holder_item = holder as MemberInfoAdapter.ViewHolder
                holder_item.apply {
                    //TODO
                    tv_name.text = mMemberlist[position].acc_name
//                    tv_state.text =mMemberlist[position].acc_name
                    ll_root.setOnClickListener {
                        startActivityForResult(intent<MemberInfoActivity>().apply {
                            putExtra("item", mMemberlist[position])
                        },1)
                    }


                }
            }
        })


        tv_member_add.setOnClickListener {
            startActivityForResult(intent<MemberAddActivity>(),1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            getMemberlist()

        }
    }
    fun getMemberlist(){
        mMemberlist.clear()
        adapter.notifyDataSetChanged()
        NetRequest(URL_LIST_MEMBER +"company/${COMP_ID}/status/$PASS_STATE", NET_GET,null,this,this)
    }

    override fun InitData() {
        getMemberlist()

    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data).apply {
            if(!isNull("data")&&getInt("code")==200){

               for(item in Gson().fromJson<List<MemberInfo>>(JSONObject(data).getJSONArray("data").toString(),object : TypeToken<List<MemberInfo>>(){}.type) as ArrayList<MemberInfo>){

                   //TODO
                   if(item.puuid.equals("root")){
                        tv_member_manager.text=item.acc_name

                 }else{
                       mMemberlist.add(item)
                   }
               }

                adapter.notifyDataSetChanged()
            }
        }
    }
}