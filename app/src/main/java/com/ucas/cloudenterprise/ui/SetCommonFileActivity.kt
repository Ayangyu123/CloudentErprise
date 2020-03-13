package com.ucas.cloudenterprise.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.JurisAdapter
import com.ucas.cloudenterprise.adapter.TeamAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.model.Juris
import com.ucas.cloudenterprise.model.JurisItem
import com.ucas.cloudenterprise.model.Team
import com.ucas.cloudenterprise.utils.Drawablewhitenull
import com.ucas.cloudenterprise.utils.LeftDrawable
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_setcommonfile.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONArray
import org.json.JSONObject

class SetCommonFileActivity:BaseActivity(), BaseActivity.OnNetCallback {
    companion object{
        val ADD_CAN_EDIT=1
        val ADD_CAN_UPLOAD=2
        val ADD_CAN_SEE=3
    }
    val CANEDIT_ID="7856a0ba-6b0a-4295-9d81-5b165a17d620"
    val CANSEE_ID="8c582b39-4be9-440f-8dfc-329a43a31076"
    val CANUPLOAD_ID="9227733a-7fcf-4620-82ef-73ac85a1eba2"
    lateinit var  fileitem:File_Bean//文件
    lateinit var  mCanSeeAdApter:JurisAdapter
    lateinit var  mCanEditAdApter:JurisAdapter
    lateinit var  mCanUploadAdApter:JurisAdapter


    var mCanSeeList = ArrayList<JurisItem>()
    var mCanEditList = ArrayList<JurisItem>()
    var mCanUploadList = ArrayList<JurisItem>()


    override fun GetContentViewId()= R.layout.activity_setcommonfile



    override fun InitView() {
        tv_title.text = "设置共享"
        fileitem = intent.getSerializableExtra("file") as File_Bean


        iv_back.setOnClickListener {
            finish()
        }
        tv_edit.text ="权限说明"
        tv_edit.setOnClickListener {
            startActivity<PermissionToIllustrateActivity>()
        }

        mCanEditAdApter = JurisAdapter(this, mCanEditList)
        rc_can_edit.adapter = mCanEditAdApter
        mCanEditAdApter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as JurisAdapter.ViewHolder).apply {
                    mCanEditList[position]?.apply {
                        tv_team_name.text = juris_user_name
                        if(juris_flag==1){
                            LeftDrawable(tv_team_name,R.drawable.group_icon)
                        }else{
                            Drawablewhitenull(tv_team_name)
                        }
                        iv_remove.visibility=View.GONE
                        iv_remove.setOnClickListener {
                            mCanEditList.remove(this)
                            mCanEditAdApter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
        mCanSeeAdApter = JurisAdapter(this, mCanSeeList)
        rc_can_see.adapter = mCanSeeAdApter
        mCanSeeAdApter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as JurisAdapter.ViewHolder).apply {
                    mCanSeeList[position]?.apply {
                        tv_team_name.text = juris_user_name
                        if(juris_flag==1){
                            LeftDrawable(tv_team_name,R.drawable.group_icon)
                        }else{
                            Drawablewhitenull(tv_team_name)
                        }
                        iv_remove.visibility=View.GONE
                        iv_remove.setOnClickListener {
                            mCanSeeList.remove(this)
                            mCanSeeAdApter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
        mCanUploadAdApter = JurisAdapter(this, mCanUploadList)
        rc_can_upload.adapter = mCanUploadAdApter
        mCanUploadAdApter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as JurisAdapter.ViewHolder).apply {
                    mCanUploadList[position]?.apply {
                        tv_team_name.text = juris_user_name
                        if(juris_flag==1){
                            LeftDrawable(tv_team_name,R.drawable.group_icon)
                        }else{
                            Drawablewhitenull(tv_team_name)
                        }
                        iv_remove.visibility=View.GONE
                        iv_remove.setOnClickListener {
                            mCanUploadList.remove(this)
                            mCanUploadAdApter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })

        tv_add_can_edit.setOnClickListener {
            startActivityForResult(Intent(this@SetCommonFileActivity,SelectMembersActivity::class.java).apply {
                putExtra("fromtype",SelectMembersActivity.SELECT_TEAM_AND_MEMBER)
            }, ADD_CAN_EDIT)
        }
        tv_add_can_upload.setOnClickListener {
            startActivityForResult(Intent(this@SetCommonFileActivity,SelectMembersActivity::class.java).apply {
                putExtra("formtype",SelectMembersActivity.SELECT_TEAM_AND_MEMBER)
            }, ADD_CAN_UPLOAD)
        }
        tv_add_can_see.setOnClickListener {
            startActivityForResult(Intent(this@SetCommonFileActivity,SelectMembersActivity::class.java).apply {
                putExtra("formtype",SelectMembersActivity.SELECT_TEAM_AND_MEMBER)
            }, ADD_CAN_SEE)
        }


        tv_cancel_commom.setOnClickListener {
            NetRequest(URL_PUT_FILE_JURIS, NET_PUT,HashMap<String,Any>().apply {
                put("file_id",fileitem.file_id)
                put("status", -1)//(状态)
            },this,this)

        }

    }

    override fun InitData() {
        fileitem?.pshare?.apply { //共享过-1 取消0
            when(fileitem.pshare){
                //<editor-fold desc="收藏过">
                -1->{
                    ll_can_edit.visibility = View.VISIBLE

                    ll_can_upload.visibility = View.VISIBLE
//                       t.text =""
                    ll_can_see.visibility = View.VISIBLE
//                    ll_can_stv_cancel_commom.visibility = View.VISIBLE
                       tv_able_edit_count.text =""

                  getFilejuris()
                }
                //<editor-fold desc="">

                //<editor-fold desc="没有收藏过">
                0->{
                    tv_add_can_edit.visibility = View.VISIBLE
                    tv_add_can_upload.visibility = View.VISIBLE
                    tv_add_can_see.visibility = View.VISIBLE
                   }
                //<editor-fold desc="">
            }
        }


    }

    private fun getFilejuris() {
        NetRequest("${URL_GET_FILE_JURIS}/file_id/${fileitem.file_id}", NET_GET,null,this,object:BaseActivity.OnNetCallback{
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                if(!JSONObject(data).isNull("data")&&JSONObject(data).getInt("code")== REQUEST_SUCCESS_CODE){

                    var juris_obj = Gson().fromJson<List<Juris>>(JSONObject(data).getJSONObject("data").getJSONArray("juris_obj").toString(),object :TypeToken<List<Juris>>(){}.type)


                    for (juris in juris_obj) {
                        when(juris.role_id){
                            CANEDIT_ID->{//可编辑
                                mCanEditList.clear()
                                mCanEditList.addAll(juris.juris_item)
                                if(mCanEditList.isEmpty()){
                                    ll_can_edit.visibility = View.GONE
                                    tv_add_can_edit.visibility = View.VISIBLE
                                    tv_able_edit_count.text ="可编辑成员（共0人）"
                                }else{
                                    ll_can_edit.visibility = View.VISIBLE
                                    mCanEditAdApter.notifyDataSetChanged()
                                    tv_add_can_edit.visibility = View.GONE
                                }
                            }
                            CANSEE_ID->{ //可查看
                                mCanSeeList.clear()
                                mCanSeeList.addAll(juris.juris_item)
                                if(mCanSeeList.isEmpty()){
                                    ll_can_see.visibility = View.GONE
                                    tv_add_can_see.visibility = View.VISIBLE
                                }else{
                                    ll_can_see.visibility = View.VISIBLE
                                    mCanSeeAdApter.notifyDataSetChanged()
                                    tv_add_can_see.visibility = View.GONE
                                }
                            }
                            CANUPLOAD_ID->{ //可上传
                                mCanUploadList.clear()
                                mCanUploadList.addAll(juris.juris_item)
                                if(mCanUploadList.isEmpty()){
                                    ll_can_upload.visibility = View.GONE
                                    tv_add_can_upload.visibility = View.VISIBLE
                                }else{
                                    ll_can_upload.visibility = View.VISIBLE
                                    mCanUploadAdApter.notifyDataSetChanged()
                                    tv_add_can_upload.visibility = View.GONE
                                }
                            }


                        }
                    }


                }
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&data!=null){
           var jurisItems= data.getSerializableExtra("select_list") as ArrayList<JurisItem>
                when(requestCode){
                    ADD_CAN_EDIT->{
                        mCanEditList.addAll(jurisItems)
                    }
                    ADD_CAN_UPLOAD->{
                        mCanUploadList.addAll(jurisItems)
                    }
                    ADD_CAN_SEE->{
                        mCanSeeList.addAll(jurisItems)
                    }

                }
            SetFileJuris()

        }
    }
    fun SetFileJuris(){
        var juris_obj =ArrayList<Juris>().apply {
            add(Juris(CANEDIT_ID,4,mCanEditList))
            add(Juris(CANSEE_ID,1,mCanSeeList))
            add(Juris(CANUPLOAD_ID,2,mCanUploadList))
        }


        NetRequest(URL_SET_FILE_JURIS, NET_POST,HashMap<String,Any>().apply {
            put("file_id","${fileitem.file_id}")
            put("file_name","${fileitem.file_name}")
            put("status",-1)
            put("flag",1)
            put("user_id","${USER_ID}")
            put("juris_obj",JSONArray(Gson().toJson(juris_obj)))

        },this,object :BaseActivity.OnNetCallback{
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                    if(JSONObject(data).getInt("code")== REQUEST_SUCCESS_CODE){
//
//                        var juris_obj = Gson().fromJson<List<Juris>>(JSONObject(data).getJSONObject("data").getJSONArray("juris_obj").toString(),object :TypeToken<List<Juris>>(){}.type)
//                        for (juris in juris_obj) {
//                            when(juris.role_id){
//                                CANEDIT_ID->{//可编辑
//                                    mCanEditList.addAll(juris.juris_item)
//                                    if(mCanEditList.isEmpty()){
//                                        ll_can_edit.visibility = View.GONE
//                                        tv_add_can_edit.visibility = View.VISIBLE
//                                        tv_able_edit_count.text ="可编辑成员（共0人）"
//                                    }else{
//                                        ll_can_edit.visibility = View.VISIBLE
//                                        mCanEditAdApter.notifyDataSetChanged()
//                                        tv_add_can_edit.visibility = View.GONE
//                                        tv_able_edit_count.text ="可编辑成员（共人）"
//                                    }
//                                }
//                                CANSEE_ID->{ //可查看
//                                    mCanSeeList.addAll(juris.juris_item)
//                                    if(mCanSeeList.isEmpty()){
//                                        ll_can_see.visibility = View.GONE
//                                        tv_add_can_see.visibility = View.VISIBLE
//                                    }else{
//                                        ll_can_see.visibility = View.VISIBLE
//                                        mCanSeeAdApter.notifyDataSetChanged()
//                                        tv_add_can_see.visibility = View.GONE
//                                    }
//                                }
//                                CANUPLOAD_ID->{ //可上传
//                                    mCanUploadList.addAll(juris.juris_item)
//                                    if(mCanUploadList.isEmpty()){
//                                        ll_can_upload.visibility = View.GONE
//                                        tv_add_can_upload.visibility = View.VISIBLE
//                                    }else{
//                                        ll_can_upload.visibility = View.VISIBLE
//                                        mCanUploadAdApter.notifyDataSetChanged()
//                                        tv_add_can_upload.visibility = View.GONE
//                                    }
//                                }
//
//
//                            }
//                        }

                     getFilejuris()
                    }
            }

        })
    }


    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
            when(request?.url){
                URL_PUT_FILE_JURIS ->{
                    if(JSONObject(data).getInt("code")== REQUEST_SUCCESS_CODE){
                        Toastinfo("文件取消共享成功")
                        finish()
                    }
                }

            }
    }
}