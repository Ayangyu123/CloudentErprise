package com.ucas.cloudenterprise.ui.member


import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.MemberAuditAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.MemberAuditInfo
import com.ucas.cloudenterprise.model.MemberInfo
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.intent
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.swiperefreshlayout.*
import org.json.JSONObject

/**
@author simpler
@create 2020年02月27日  16:23
 审核成员
 */
class MemberAuditActivity : BaseActivity(), BaseActivity.OnNetCallback {

    lateinit var   mAdapter: MemberAuditAdapter
    var mMemberAuditlist =ArrayList<MemberAuditInfo>()
    override fun GetContentViewId() = R.layout.activity_memeber_audit

    override fun InitView() {
        iv_back.setOnClickListener { finish() }
        tv_title.text="审核成员"
        tv_edit.visibility =View.GONE
        mAdapter=    MemberAuditAdapter(this,mMemberAuditlist)
        rc_myfiles.apply {
            adapter= mAdapter
            layoutManager = LinearLayoutManager(this@MemberAuditActivity,LinearLayoutManager.VERTICAL,false)
         addItemDecoration(DividerItemDecoration(this@MemberAuditActivity, DividerItemDecoration.VERTICAL))

        }
        //<editor-fold desc ="swipeRefresh settings">
        swipeRefresh.setColorSchemeResources(R.color.app_color)
        swipeRefresh.setOnRefreshListener {
            mMemberAuditlist.clear()
            getMemberAuditList()
            swipeRefresh.isRefreshing = false
        }
        //</editor-fold >


        mAdapter.SetOnRecyclerItemClickListener(object :OnRecyclerItemClickListener{
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as MemberAuditAdapter.ViewHolder).apply {
                    tv_member_name.text =mMemberAuditlist[position].acc_name
                    //TODO
                    tv_reason_for_application.text = "申请理由 ${if(TextUtils.isEmpty(mMemberAuditlist[position].remark)) "无" else mMemberAuditlist[position].remark} "
                    tv_state.text = when(mMemberAuditlist[position].status){
                                UNAUDIT_STATE-> "未审核"
                                NOT_PASS_STATE-> "已拒绝"
                               PASS_STATE-> "已通过"
                        else -> ""
                    }
                    ll_root.setOnClickListener {
                        var intent =intent<MemberAuditInfoActivity>().apply {
                            putExtra("item",mMemberAuditlist[position])
                        }
                        if(mMemberAuditlist[position].status==mMemberAuditlist[position].status){
                            startActivityForResult(intent,1)
                        }else{
                        startActivity(intent)}
                    }

                }

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            getMemberAuditList()
        }
    }

    override fun InitData() {
        getMemberAuditList()
        }

    fun  getMemberAuditList(){
        mMemberAuditlist.clear()
        mAdapter.notifyDataSetChanged()
        NetRequest("${URL_MEMBER_AUDIT_LIST}${COMP_ID}/status/${UNAUDIT_STATE}", NET_GET,null,this,this)

    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
            JSONObject(data)?.apply {
                if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){

                    mMemberAuditlist.addAll( Gson().fromJson<List<MemberAuditInfo>>(getJSONArray("data").toString(),object : TypeToken<List<MemberAuditInfo>>(){}.type) as ArrayList<MemberAuditInfo>)
                    Log.e("ok","${mMemberAuditlist.size}")
                    if(!mMemberAuditlist.isEmpty()){
                        swipeRefresh.visibility =View.VISIBLE
                        ll_empty.visibility =View.GONE
                        mAdapter.notifyDataSetChanged()
                    }else{
                        swipeRefresh.visibility =View.GONE
                        ll_empty.visibility =View.VISIBLE

                        tv_refresh.setOnClickListener {
                            getMemberAuditList()
                        }
                    }

                }else{ Toastinfo("${getString("message")}")
                }

            }
    }
}