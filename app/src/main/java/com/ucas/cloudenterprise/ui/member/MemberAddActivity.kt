package com.ucas.cloudenterprise.ui.member

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.TeamAdapter
import com.ucas.cloudenterprise.app.COMP_ID
import com.ucas.cloudenterprise.app.NET_POST
import com.ucas.cloudenterprise.app.REQUEST_SUCCESS_CODE
import com.ucas.cloudenterprise.app.URL_ADD_MEMBER
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.BelongTeam
import com.ucas.cloudenterprise.model.Team
import com.ucas.cloudenterprise.ui.SelectMembersActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.intent
import kotlinx.android.synthetic.main.activtity_member_add.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.InterfaceAddress

class MemberAddActivity: BaseActivity(), BaseActivity.OnNetCallback {
    var mTeamlist=ArrayList<BelongTeam>()
    lateinit var  mTeamsAdapter: TeamAdapter
    override fun GetContentViewId()= R.layout.activtity_member_add

    override fun InitView() {
        //<editor-fold desc="公共头设置">
        iv_back.setOnClickListener { finish() }
        tv_title.text ="添加成员"
        tv_edit.visibility =View.GONE
        //</editor-fold>
        mTeamsAdapter = TeamAdapter(this,mTeamlist)
        rc_teams.adapter =mTeamsAdapter
        mTeamsAdapter.SetOnRecyclerItemClickListener(object :OnRecyclerItemClickListener{
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as  TeamAdapter.ViewHolder).apply {
                    mTeamlist[position].apply {
                        tv_team_name.text =team_name
                    }
                    iv_remove.setOnClickListener {
                        mTeamlist.remove(mTeamlist[position])
                        mTeamsAdapter.notifyDataSetChanged()

                    }
                    fl_root.setOnClickListener {
                        //TODO
                    }
                }
            }

        })



     }

    override fun InitData() {
        mTeamlist.add(BelongTeam("1579510310535725518-6950669919882193906","ming"))
        mTeamsAdapter.notifyDataSetChanged()
      }

    fun AddTeams(view: View) {
        startActivityForResult(intent<SelectMembersActivity>(),SelectMembersActivity.ADD_TEAM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== Activity.RESULT_OK&&data!=null){
            //TODO
        }
    }

    fun AddMemberItem(view: View) {
        if(TextUtils.isEmpty(et_acc_name.text.toString())){
            Toastinfo("请输入成员名称")
            return
        }
        if(TextUtils.isEmpty(et_phone.text.toString())){
            Toastinfo("请输入成员手机号码")
            return
        }

        NetRequest(URL_ADD_MEMBER, NET_POST,HashMap<String,Any>().apply {
            put("comp_id","${COMP_ID}")
            put("acc_name","${et_acc_name.text.toString()}")
            put("telphone","${et_phone.text.toString()}")
            put("email","${et_email.text.toString()}")
            put("capacity",if(TextUtils.isEmpty(et_cap.text.toString())) 10 else et_cap.text.toString().toInt())
            Log.e("ok",Gson().toJson(mTeamlist).toString())
            put("belong_team",JSONArray(Gson().toJson(mTeamlist)))
//            put("belong_team",mTeamlist.toArray())
//            put("belong_team",ArrayList<BelongTeam>())
            put( "Invit_mode","1")


        },this,this)
    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data)?.apply {
            if(getInt("code")== REQUEST_SUCCESS_CODE){
                Toastinfo("成员添加成功")
                setResult(Activity.RESULT_OK)
                finish()
            }

        }
    }

}
fun  main(){
    var mTeamlist=ArrayList<BelongTeam>()
    mTeamlist.add(BelongTeam("1579510310535725518-6950669919882193906","ming"))
//    println(Gson().toJson(mTeamlist).toString())
//    var list=Gson().fromJson<List<BelongTeam>>("[{\"belong_teamId\":\"1579510310535725518-6950669919882193906\",\"team_name\":\"ming\"}]",object :TypeToken<List<BelongTeam>>(){}.type) as ArrayList<BelongTeam>
//    println(list.toString())
    var params =HashMap<String,Any>().apply {
        put("comp_id","COMP_ID")
        put("acc_name","${1231}")
        put("telphone","${123123}")
        put("email","${123}")
        put("capacity", 10 )
//        Log.e("ok",Gson().toJson(mTeamlist).toString())
        put("belong_team",Gson().toJson(mTeamlist).toString())
//            put("belong_team",JSONArray(mTeamlist))
//            put("belong_team",ArrayList<BelongTeam>())
        put( "Invit_mode",1)


    }
//    println(JSONArray(mTeamlist).toString())
    println(JSONObject(params as Map<String, Any>))

}
