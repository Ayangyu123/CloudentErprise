package com.ucas.cloudenterprise.ui.member

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.COMP_ID
import com.ucas.cloudenterprise.app.MyApplication.Companion.context
import com.ucas.cloudenterprise.app.NET_POST
import com.ucas.cloudenterprise.app.REQUEST_SUCCESS_CODE
import com.ucas.cloudenterprise.app.URL_TEAM_INVITE
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.activity_member_invite.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

class MemberInviteActivity: BaseActivity(), BaseActivity.OnNetCallback {

    var shared_url =""
    override fun GetContentViewId()= R.layout.activity_member_invite

    override fun InitView() {
      tv_edit.visibility = View.GONE
        tv_title.text ="邀请成员"
        iv_back.setOnClickListener { finish() }
        tv_edit_link.setOnClickListener {
            startActivity<EditMemberInviteLinkActivity>()
        }

        tv_link_share.isEnabled = false
        tv_link_share.setOnClickListener {
            if(TextUtils.isEmpty(shared_url)){
                Toastinfo("分享链接为空")
                return@setOnClickListener
            }
            ShareUtils.sharebyandroidSheet("来自土星云的邀请 链接地址：${shared_url} ",this)
        }
    }

    override fun InitData() {
        CreateMemberInvite()
    }

    public fun CreateMemberInvite() {
        var params = HashMap<String,Any>().apply {
            put("comp_id", COMP_ID)
        }
        NetRequest(URL_TEAM_INVITE, NET_POST, params,this,this)
    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data).apply {
            if(!isNull("data")&&getInt("code")== REQUEST_SUCCESS_CODE){
                getJSONObject("data")?.apply {
//                    Glide.with(context).load(getString("qrcode_url")).into(iv_qr_code)
                    //TODO 分享链接字段
                    shared_url ="${getString("invite_url")}"
                    iv_qr_code.setImageBitmap(QRCodeUtils.CreateQRCode(shared_url,this@MemberInviteActivity,R.drawable.ic_launcher,(iv_qr_code.width/2)))
                    tv_cap.text = "${getLong("capacity")/1024/1024/1024}G"
                    tv_link_share.isEnabled = true
//                    tv_indate.text = AppUtils.timestamptoString(getLong("timestamp"))
                }

            }
        }
    }
}