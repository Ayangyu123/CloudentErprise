package com.ucas.cloudenterprise.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.adapter.FilesAdapter
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragemnt
import com.ucas.cloudenterprise.model.File_Bean
import kotlinx.android.synthetic.main.my_files_fragment.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.*
import kotlinx.android.synthetic.main.popwiond_sort.*


/**
@author simpler
@create 2020年01月10日  14:31
 */
class MyFilesFragment:BaseFragemnt(),BaseActivity.OnNetCallback {
    val TAG ="MyFilesFragment"
    var fileslist = ArrayList<File_Bean>()
    lateinit var adapter:FilesAdapter
     var CreateNewDirDialog:Dialog ?=null
     var UpFileTypeDialog:Dialog ?=null
     var SortPOPwiond:PopupWindow ?=null
    var pid ="root"



    override fun initView() {

       adapter = FilesAdapter(mContext,fileslist)
       rc_myfiles.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false)
       rc_myfiles.adapter = adapter
       rc_myfiles.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))


        iv_create_dir.setOnClickListener{
            if(CreateNewDirDialog == null){

                CreateNewDirDialog = GetCreateNewDirDialog(mContext!!,View.OnClickListener{
                    CreateNewDirDialog!!.et_dir_name.text  = SetEt_Text("")
                    CreateNewDirDialog!!.checkbox_is_common.isChecked = false
                    CreateNewDirDialog?.dismiss()
                }
                   ,View.OnClickListener{
                        if(CreateNewDirDialog!!.et_dir_name?.text==null||TextUtils.isEmpty(CreateNewDirDialog!!.et_dir_name?.text)){
                            Toastinfo("文件名不能为空")
                            return@OnClickListener
                        }
                        CreateNewDir( CreateNewDirDialog!!.et_dir_name?.text.toString(), CreateNewDirDialog!!.checkbox_is_common.isChecked)
                        CreateNewDirDialog?.dismiss()
                    })
            }

            CreateNewDirDialog?.show()

        }


        iv_show_up_file_type_dilaog.setOnClickListener{
            if(UpFileTypeDialog == null){

                UpFileTypeDialog = GetUploadFileTypeDialog(mContext!!,View.OnClickListener{

                    UpFileTypeDialog?.dismiss()
                }
                    ,View.OnClickListener{
                       when(it.id){
                           R.id.tv_image->{
                               //TODO
                               Toastinfo("图片")
                           }
                           R.id.tv_doc->{
                               Toastinfo("文档")
                           }
                           R.id.tv_video->{
                               Toastinfo("视频")
                           }
                           R.id.tv_all->{
                               Toastinfo("全部")
                           }
                       }

                        UpFileTypeDialog?.dismiss()
                    })
            }
            UpFileTypeDialog?.show()

        }


        iv_sort.setOnClickListener{
            if(SortPOPwiond == null){
                SortPOPwiond = GetSortPOPwiond(mContext!!,RadioGroup.OnCheckedChangeListener { radioGroup, i ->
                    when(id){
                        R.id.rb_sort_by_name->{


                        }
                        R.id.rb_sort_by_time->{}
                    }
                })

            }
            if(SortPOPwiond!!.isShowing){
                SortPOPwiond!!.dismiss()
            }else{
                SortPOPwiond!!.showAsDropDown(iv_sort,0,20,Gravity.CENTER)
            }

        }


    }

    private fun CreateNewDir(dirname: String, iscommon: Boolean) {
        //TODO 请求创建新的文件夹
        val params = HashMap<String,Any>()
        params["file_name"] = dirname
        params["is_dir"] = IS_DIR
        params["user_id"] = USER_ID //TODO
        params["fidhash"] = ""
        params["state"] = if (iscommon) IS_COMMON_DIR else IS_UNCOMMON_DIR
        params["pid"] = pid
        params["size"] = 0

        NetRequest(URL_ADD_File, NET_POST,params,this,this)

    }

    override fun initData() {
        NetRequest(URL_LIST_FILES +"${USER_ID}", NET_GET,null,this,this)
    }

    override fun GetRootViewID()= com.ucas.cloudenterprise.R.layout.my_files_fragment
    companion object{
         private var instance: MyFilesFragment? = null

        fun getInstance( param1:Boolean,  param2:String?): MyFilesFragment {
            if (instance == null) {
                synchronized(MyFilesFragment::class.java) {
                    if (instance == null) {
                        instance = MyFilesFragment().apply {
                            this.arguments =Bundle().apply {
                                putBoolean("param1",param1)
                                putString("param2",param2)
                            }
                        }
                    }
                }
            }
            return instance!!
        }
    }
    override fun OnNetPostSucces(request: Request<String, out Request<Any, Request<*, *>>>?, data: String) {
        when(request?.url){
            URL_LIST_FILES +"${USER_ID}"->{
                //获取我的文件列表
                Toastinfo("获取文件列表成功")
                fileslist.clear()
                fileslist.addAll(Gson().fromJson<List<File_Bean>>(data,object : TypeToken<List<File_Bean>>(){}.type) as ArrayList<File_Bean>)
                Log.e(TAG,"filelist size is ${fileslist.size}")
                adapter?.notifyDataSetChanged()
            }
            URL_ADD_File->{
                Toastinfo("添加文件列表成功")
                //刷新列表
                initData()
            }

        }

    }

}