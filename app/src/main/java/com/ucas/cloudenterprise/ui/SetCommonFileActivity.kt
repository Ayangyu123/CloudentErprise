package com.ucas.cloudenterprise.ui

import android.app.Activity
import android.content.Intent
import android.util.Log
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
        tv_edit.visibility =View.GONE
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
                putExtra("formtype",SelectMembersActivity.SELECT_TEAM_AND_MEMBER)
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


        //<editor-fold desc="修改">
        tv_see_able.setOnClickListener {
            startActivityForResult(Intent(this@SetCommonFileActivity,SelectMembersActivity::class.java).apply {
                putExtra("formtype",SelectMembersActivity.UPDATE_SELECT_INFO)
                putExtra("select_list",mCanSeeList)
            },ADD_CAN_SEE)
        }
        tv_add_upload_able.setOnClickListener {
            startActivityForResult(Intent(this@SetCommonFileActivity,SelectMembersActivity::class.java).apply {
                putExtra("formtype",SelectMembersActivity.UPDATE_SELECT_INFO)
                putExtra("select_list",mCanUploadList)
            }, ADD_CAN_UPLOAD)
        }
        tv_add_editable.setOnClickListener {
            startActivityForResult(Intent(this@SetCommonFileActivity,SelectMembersActivity::class.java).apply {
                putExtra("formtype",SelectMembersActivity.UPDATE_SELECT_INFO)
                putExtra("select_list",mCanEditList)
            }, ADD_CAN_EDIT)
        }


        //</editor-fold>



        tv_cancel_commom.setOnClickListener {
            NetRequest(URL_PUT_FILE_JURIS, NET_PUT,HashMap<String,Any>().apply {
                put("file_id",fileitem.file_id)
//                put("status", -1)//(状态)
            },this,this)

        }

    }

    override fun InitData() {
        fileitem?.pshare?.apply { //共享过-1 取消0
            when(fileitem.pshare){
                //<editor-fold desc="收藏过">
                -1->{
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
        NetRequest("${URL_GET_FILE_JURIS}/${fileitem.file_id}", NET_GET,null,this,object:BaseActivity.OnNetCallback{
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                Log.e("ok",data)
                if(!JSONObject(data).isNull("data")&&JSONObject(data).getInt("code")== REQUEST_SUCCESS_CODE){
                        if(JSONObject(data).getJSONObject("data").isNull("juris_obj")){
                            Toastinfo("分享数据为空")
                            return
                        }
                    var juris_obj = Gson().fromJson<List<Juris>>(JSONObject(data).getJSONObject("data").getJSONArray("juris_obj").toString(),object :TypeToken<List<Juris>>(){}.type)

                    mCanEditList.clear()
                    mCanSeeList.clear()
                    mCanUploadList.clear()
                    for (juris in juris_obj) {
                        when(juris.role_id){
                            CANEDIT_ID->{//可编辑

                                mCanEditList.add(juris.juris_item[0])

                            }
                            CANSEE_ID->{ //可查看

                                mCanSeeList.add(juris.juris_item[0])

                            }
                            CANUPLOAD_ID->{ //可上传

                                mCanUploadList.add(juris.juris_item[0])

                            }


                        }
                    }

                    if(mCanEditList.isEmpty()){
                        ll_can_edit.visibility = View.GONE
                        tv_add_can_edit.visibility = View.VISIBLE
                    }else{
                        ll_can_edit.visibility = View.VISIBLE
                        mCanEditAdApter.notifyDataSetChanged()
                        tv_add_can_edit.visibility = View.GONE
                    }
                    if(mCanSeeList.isEmpty()){
                        ll_can_see.visibility = View.GONE
                        tv_add_can_see.visibility = View.VISIBLE
                    }else{
                        ll_can_see.visibility = View.VISIBLE
                        mCanSeeAdApter.notifyDataSetChanged()
                        tv_add_can_see.visibility = View.GONE
                    }
                    if(mCanUploadList.isEmpty()){
                        ll_can_upload.visibility = View.GONE
                        tv_add_can_upload.visibility = View.VISIBLE
                    }else{
                        ll_can_upload.visibility = View.VISIBLE
                        mCanUploadAdApter.notifyDataSetChanged()
                        tv_add_can_upload.visibility = View.GONE
                    }
                    if(mCanEditList.isEmpty()&&mCanSeeList.isEmpty()&&mCanUploadList.isEmpty()){
                        Toastinfo("数据异常")
                    }else{
                        tv_cancel_commom.visibility =View.VISIBLE
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
                       for(item  in jurisItems){
                           if(!mCanEditList.contains(item)){
                               mCanEditList.add(item)
                           }
                       }

                        Log.e("ok","mCanEditList ="+mCanEditList.toString())
                    }
                    ADD_CAN_UPLOAD->{

                        for(item  in jurisItems){
                            if(!mCanUploadList.contains(item)){
                                mCanUploadList.add(item)
                            }
                        }
                    }
                    ADD_CAN_SEE->{
                        for(item  in jurisItems){
                            if(!mCanSeeList.contains(item)){
                                mCanSeeList.add(item)
                            }
                        }

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