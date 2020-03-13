package com.ucas.cloudenterprise.ui.member

import android.app.Activity
import android.view.View
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.MemberAuditInfo
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.activity_member_audit_info.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

/**
@author simpler
@create 2020年02月27日  16:34
 成员审核详情
 */
class MemberAuditInfoActivity : BaseActivity(), BaseActivity.OnNetCallback {

    lateinit var  item:MemberAuditInfo
    override fun GetContentViewId() = R.layout.activity_member_audit_info

    override fun InitView() {
        item = intent.getSerializableExtra("item") as MemberAuditInfo

        tv_edit.visibility = View.GONE
        iv_back.setOnClickListener { finish() }
        tv_title.text =item.acc_name
        item?.apply {
        tv_acc_name.text = acc_name
        tv_reason_time.text = create_time
            tv_phone.text = telphone
            tv_email.text = email
            tv_remark.text =remark

        }
        when(item.status){
            UNAUDIT_STATE->{ //未审核

                ll_update_state.visibility =View.VISIBLE
                tv_pass.setOnClickListener {
                    UpdateMemberAuditInfo(true)
                }
                tv_not_pass.setOnClickListener {
                    UpdateMemberAuditInfo(false)
                }

            }
            else->{
                fl_state.visibility =View.VISIBLE
                tv_state.text = when(item.status){
                    NOT_PASS_STATE -> "已拒绝"
                    PASS_STATE -> "已通过"
                    else->""
                }
            }

        }


   }
    fun UpdateMemberAuditInfo(passresult:Boolean){
        NetRequest(URL_MEMBER_AUDIT_UPDATE_INFO, NET_PUT,HashMap<String,Any>().apply {
            put("member_id","${item.member_id}")
            put("status", if(passresult) PASS_STATE else NOT_PASS_STATE)
        },this,this)
    }

    override fun InitData() {
    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data)?.apply {
            if (getInt("code")== REQUEST_SUCCESS_CODE){
                Toastinfo("审核完成")//TODO
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}