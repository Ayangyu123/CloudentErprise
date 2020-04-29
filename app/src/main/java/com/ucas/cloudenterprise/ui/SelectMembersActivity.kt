package com.ucas.cloudenterprise.ui

import android.app.Activity
import android.content.AbstractThreadedSyncAdapter
import android.content.ClipData
import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.JurisAdapter
import com.ucas.cloudenterprise.adapter.SelectTeamAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.JurisItem
import com.ucas.cloudenterprise.model.Resource
import com.ucas.cloudenterprise.model.Team
import com.ucas.cloudenterprise.ui.member.MemberSearchActivity
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.activity_select_members.*
import kotlinx.android.synthetic.main.activity_team.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

class SelectMembersActivity:BaseActivity(), BaseActivity.OnNetCallback {
    companion object{
        val SELECT_TEAM_AND_MEMBER:Int=1
        val SELECT_TEAM:Int=2
        val UPDATE_SELECT_INFO=3
    }

    var pid ="root"
    var fromtype = 2
    var rc_is_show_falg =false
    lateinit var  mAdapter: SelectTeamAdapter
    lateinit var  mSelectAdapter: JurisAdapter
    var mList = ArrayList<Team>()
    var mSelectList = ArrayList<JurisItem>()
    var teamcount = 0
    var membercount = 0
    var select_list= ArrayList<String>()
    lateinit var pid_stack : ArrayList<String>
    val TAG ="SelectMembersActivity"
    override fun GetContentViewId()= R.layout.activity_select_members

    override fun InitView() {
   //TODO
        fromtype = intent.getIntExtra("formtype",0)
        Log.e("ok","fromtype=${fromtype}")
        when(fromtype){
            SELECT_TEAM->{
                tv_title.text ="选择团队"
            }
            SELECT_TEAM_AND_MEMBER->{
                tv_title.text ="选择成员"
            }
            UPDATE_SELECT_INFO->{
                tv_title.text ="选择成员"


                   for (jurisItem in intent.getSerializableExtra("select_list") as ArrayList<JurisItem>) {
                       select_list.add(jurisItem.juris_user_id)
                       Log.e("ok","select_list = ${select_list.toString()}")
                       mSelectList.add(jurisItem)
                       when(jurisItem.juris_flag){
                           1->{//团队

                               teamcount++
                           }
                           else->{//个人
                               membercount++
                           }
                       }
                   }


            }
           }
        tv_commit.setOnClickListener {
            setResult(Activity.RESULT_OK,intent.putExtra("select_list",mSelectList))
            finish()
        }
        iv_back.visibility=View.GONE
        iv_choose.apply {
            visibility =View.VISIBLE
            setOnClickListener { finish() }
        }
        iv_back.setOnClickListener {
            if(pid_stack.size>1){
                pid_stack.remove(pid_stack[0])
                pid=pid_stack[0]
            }else{
                iv_back.visibility=View.GONE
            }

        }

        iv_title_search.apply {
            tv_edit.visibility = View.GONE
            visibility = View.VISIBLE
            setOnClickListener {
                startActivity<MemberSearchActivity>()//TODO
            }
        }
        mSelectAdapter = JurisAdapter(this,mSelectList)
        rc_select_show.adapter = mSelectAdapter
        mSelectAdapter.SetOnRecyclerItemClickListener(object :OnRecyclerItemClickListener{
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as JurisAdapter.ViewHolder).apply {
                    mSelectList[position]?.apply {
                        tv_team_name.text = juris_user_name
                        if(juris_flag==1){
                            LeftDrawable(tv_team_name,R.drawable.group_icon)
                        }else{
                          Drawablewhitenull(tv_team_name)
                        }
                        iv_remove.visibility =View.GONE
                        iv_remove.setOnClickListener {
                            mList.find { this.juris_user_id.equals(it.is_team)}?.apply{
                                this.select_status =false
                                mAdapter.notifyDataSetChanged()
                            }
                            mSelectList.remove(this)
                            mSelectAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })



        mAdapter= SelectTeamAdapter(this,mList)
        rc_teams.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rc_teams.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rc_teams.adapter = mAdapter
        mAdapter.SetOnRecyclerItemClickListener(object :OnRecyclerItemClickListener{
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as SelectTeamAdapter.ViewHolder).apply {
                    mList[position]?.apply {
                        tv_team_name.text = team_name
                        if(is_team){
                        tv_members_count.text = "${people_count}人"
                            iv_team_falg.visibility = View.VISIBLE
                            tv_members_count.visibility = View.VISIBLE
                            if (people_count!=0){
                            ll_root.setOnClickListener {
                                pid_stack.add(0,team_id)
                                pid =pid_stack[0]
                                if(iv_back.visibility==View.GONE){
                                    iv_back.visibility =View.VISIBLE
                                }
                                GetTeamMembers()
                            }}
                        }else{

                            iv_team_falg.visibility = View.INVISIBLE
                            tv_members_count.visibility = View.INVISIBLE

                        }
                        check_box_select_team_all.isChecked = select_status

                       check_box_select_team_all.setOnCheckedChangeListener { buttonView, isChecked ->
                            if(isChecked){
                                if(is_team){
                                teamcount++
                                    mSelectList.add(JurisItem(team_id,team_name,1))
                                }else{
                                     membercount++
                                    mSelectList.add(JurisItem(team_id,team_name,0))
                                    }

                                if( ll_select_member.visibility ==View.GONE){
                                    ll_select_member.visibility =View.VISIBLE
                                }




                            }else{
                                if(is_team){
                                    teamcount--
                                    mSelectList.remove(JurisItem(team_id,team_name,1))
                                }else{
                                    membercount--
                                    mSelectList.remove(JurisItem(team_id,team_name,0))
                                }


                            }
                           if(mSelectList.isEmpty()){
                               rc_select_show.visibility =View.GONE
                               ll_select_member.visibility =View.GONE
                           }else{
                               mSelectAdapter.notifyDataSetChanged()
                           }

                           tv_select_info.text = "已选${getteamcountinfo()}${getmembercountinfo()}"
                       }


                    }
                }

            }

        })


        check_box_select_all.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked){
                teamcount ++
                mSelectList.add(JurisItem(pid,tv_team_name.text.toString(),1))
                if( ll_select_member.visibility ==View.GONE){
                    ll_select_member.visibility =View.VISIBLE
                }

            }else{
                teamcount --
                mSelectList.remove(JurisItem(pid,tv_team_name.text.toString(),1))
            }
            if(mSelectList.isEmpty()){

                rc_select_show.visibility =View.GONE
                ll_select_member.visibility =View.GONE
            }else{
                mSelectAdapter.notifyDataSetChanged()
            }
            tv_select_info.text = "已选${getteamcountinfo()}${getmembercountinfo()}"
            for (item in mList){
                item.select_status =isChecked
            }
            mAdapter.notifyDataSetChanged()
        }


        iv_select_rc_show_flag.setOnClickListener {
            rc_is_show_falg =!rc_is_show_falg
            if(rc_is_show_falg){
                //TODO 黑色view
                iv_select_rc_show_flag.setImageResource(R.drawable.unexpand_button)
                rc_select_show.visibility =View.VISIBLE
            }else{
                iv_select_rc_show_flag.setImageResource(R.drawable.expand_button)
                rc_select_show.visibility =View.GONE
            }

        }

    }

    private fun getmembercountinfo(): String {
        var info =""
        if(membercount!=0){
            info ="${membercount}个成员"
        }

        return  info
    }

    private fun getteamcountinfo(): String {
        var info =""
            if(teamcount!=0){
                info ="${teamcount}个团队"
            }

        return  info
    }

    override fun InitData() {
        pid_stack = ArrayList()
        pid_stack.add(pid)
        //<editor-fold desc="test">
        GetTeamMembers()
        //</editor-fold>

    }

    private fun GetTeamMembers() {
        NetRequest(URL_GET_TEAM_LIST+"/$COMP_ID/team/${pid}", NET_GET,null,this,this)

    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data)?.apply{
            if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){
                mList.clear()
//                if(pid.equals("root")){
                    tv_team_name.text = getJSONObject("data").getString("comp_name")
                    tv_members_count.text = "${getJSONObject("data").getInt("people_count")}人"
//                }
                if(!getJSONObject("data").isNull("team_list")){
                     var list =Gson().fromJson<List<Team>>(getJSONObject("data").getJSONArray("team_list").toString(),object :TypeToken<List<Team>>(){}.type)
                    when(fromtype){
                        SELECT_TEAM->{
                           for (item in  list){
                               if(item.is_team==true){
                                   mList.add(item)
                               }

                           }
                        }
                        UPDATE_SELECT_INFO,SELECT_TEAM_AND_MEMBER->{
                            mList.addAll(list)
                                check_box_select_all.isChecked =select_list.contains(pid)

                            for (item in mList){
                                if(select_list.contains(item.team_id)){
                                    item.select_status =true
                                }
                            }
                        }

                    }

                    mAdapter.notifyDataSetChanged()
                }else{
                    Toastinfo("请添加团队成员")
                }


            }else{

            }
        }
    }
}