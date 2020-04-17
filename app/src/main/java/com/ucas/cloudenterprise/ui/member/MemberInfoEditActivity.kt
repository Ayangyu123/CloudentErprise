package com.ucas.cloudenterprise.ui.member

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.NET_POST
import com.ucas.cloudenterprise.app.NET_PUT
import com.ucas.cloudenterprise.app.URL_ADD_MEMBER
import com.ucas.cloudenterprise.app.URL_MEMBER_INFO_UPDATE
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.BelongTeam
import com.ucas.cloudenterprise.model.MemberInfo
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import kotlinx.android.synthetic.main.activity_member_info.*
import kotlinx.android.synthetic.main.activity_member_info_edit.*
import kotlinx.android.synthetic.main.activity_member_info_edit.ll_teams
import kotlinx.android.synthetic.main.activity_member_info_edit.tv_acc_name
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.item_team.view.*
import org.json.JSONObject

/**
@author simpler
@create 2020年03月03日  13:28
 */
class MemberInfoEditActivity: BaseActivity(), BaseActivity.OnNetCallback {
    lateinit var  item : MemberInfo

    override fun GetContentViewId()= R.layout.activity_member_info_edit

    override fun InitView() {
        item = intent.getSerializableExtra("item") as MemberInfo
        iv_back.setOnClickListener{finish()}
        tv_edit.text = "完成"
        tv_edit.isEnabled=false
        tv_title.text =item.acc_name
        tv_acc_name.text =item.acc_name
        tv_company_name.text =item.comp_name



        et_cap.text = SetEt_Text(""+item.capacity)
        et_phone.text = SetEt_Text(""+item.telphone)
        et_email.text = SetEt_Text(""+item.email)

        et_cap.addTextChangedListener {
            if(!TextUtils.isEmpty(et_cap.text)&&!et_cap.text.equals(item.capacity)){
                tv_edit.isEnabled =true
            }
        }
        et_phone.addTextChangedListener {
            if(!TextUtils.isEmpty(et_phone.text)&&!et_phone.text.equals(item.telphone)){
                tv_edit.isEnabled =true
            }
        }
        et_email.addTextChangedListener {
            if(!TextUtils.isEmpty(et_email.text)&&!et_email.text.equals(item.email)){
                tv_edit.isEnabled =true
            }
        }

        tv_edit.setOnClickListener {
            //TODO 添加容量验证
            if(TextUtils.isEmpty(et_cap.text)){
                Toastinfo("请输入容量")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(et_phone.text)){
                Toastinfo("请输入手机号")
                return@setOnClickListener
            }
            if(!VerifyUtils.VerifyPhone(et_phone.text.toString())){
                Toastinfo("请输正确的手机号")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(et_phone.text)){
                Toastinfo("请输入手机号")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(et_email.text)){
                Toastinfo("请输入邮箱")
                return@setOnClickListener
            }

            NetRequest(URL_MEMBER_INFO_UPDATE, NET_POST,HashMap<String,Any>().apply {
                put("member_id",item.member_id)
                put("telphone",et_phone.text)
                put("email",et_email.text)
                put("capacity",et_cap.text.toString().toInt())

            },this,this)
        }



        item.belong_team?.apply {
            var tv_temname :View
            if(this.isEmpty()){
                tv_temname= LayoutInflater.from(this@MemberInfoEditActivity).inflate(R.layout.item_team,null)
                tv_temname.tv_team_name.text="无"
                tv_temname.iv_remove.visibility=View.GONE
                ll_teams.addView(tv_temname)


            }else{
                for(item: BelongTeam in this){
                    tv_temname= LayoutInflater.from(this@MemberInfoEditActivity).inflate(R.layout.item_team,null)
                    tv_temname.tv_team_name.text= item.team_name
                    tv_temname.iv_remove.visibility=View.GONE
                    ll_teams.addView(tv_temname)
                }
            }
        }




    }

    override fun InitData() {

    }

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        JSONObject(data)?.apply {
            if(getInt("code")==200){
                request?.apply {
                    when(this.url){
                        URL_MEMBER_INFO_UPDATE->{
                            Toastinfo("修改成功")

                        }
                        URL_ADD_MEMBER ->{
                            Toastinfo("删除成功")
                        }
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                }

             return
            }

        }
    }

    fun DeleteMemberInfo(view: View) {

        NetRequest(URL_ADD_MEMBER, NET_PUT,HashMap<String,Any>().apply {
            put("member_id","${item.member_id}")
        },this,this)
    }
}