package com.ucas.cloudenterprise.ui

import android.content.AbstractThreadedSyncAdapter
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
import com.ucas.cloudenterprise.adapter.SelectTeamAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.Resource
import com.ucas.cloudenterprise.model.Team
import com.ucas.cloudenterprise.ui.member.MemberSearchActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_select_members.*
import kotlinx.android.synthetic.main.activity_team.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

class SelectMembersActivity:BaseActivity(), BaseActivity.OnNetCallback {
    companion object{
        val ADD_TEAM=1
    }

    val pid ="root"
    lateinit var  mAdapter: SelectTeamAdapter
    var mList = ArrayList<Team>()
    lateinit var pid_stack : ArrayList<String>
    val TAG ="SelectMembersActivity"
    override fun GetContentViewId()= R.layout.activity_select_members

    override fun InitView() {
    tv_title.text ="选择可成员"//TODO
        iv_back.visibility=View.GONE
        iv_choose.apply {
            visibility =View.VISIBLE
            setOnClickListener { finish() }
        }
        iv_title_search.apply {
            tv_edit.visibility = View.GONE
            visibility = View.VISIBLE
            setOnClickListener {
                startActivity<MemberSearchActivity>()//TODO
            }
        }

        mAdapter= SelectTeamAdapter(this,mList)
        rc_teams.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rc_teams.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rc_teams.adapter = mAdapter
        mAdapter.SetOnRecyclerItemClickListener(object :OnRecyclerItemClickListener{
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as SelectTeamAdapter.ViewHolder).apply {
                    mList[position]?.apply {
                        tv_team_name.text = team_name
//                        tv_members_count.text = te
                    }
                }

            }

        })

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
                mList.addAll( Gson().fromJson<List<Team>>(getJSONArray("data").toString(),object :TypeToken<List<Team>>(){}.type) )
                mAdapter.notifyDataSetChanged()

            }else{

            }
        }
    }
}