package com.ucas.cloudenterprise.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.TextView
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lzy.okgo.model.HttpMethod
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.BottomFilesOperateAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.FileInfoActivity
import com.ucas.cloudenterprise.ui.MainActivity
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.*


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
     var FileDeleteTipsDialog:Dialog ?=null
    var BottomFilesOperateDialog:BottomSheetDialog ? =null
    var pid ="root"



    override fun initView() {
        //<editor-fold desc=" 设置files RecyclerView  ">
       adapter = FilesAdapter(mContext,fileslist)
       rc_myfiles.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false)
       rc_myfiles.adapter = adapter
       rc_myfiles.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        adapter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener{
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                var item =fileslist[position]
                holder.apply {
                    holder as FilesAdapter.ViewHolder
                    holder.apply {
                        tv_title.text = item.file_name
                        tv_time.text = item.created_at
                        val isfile = if(item.is_dir==-1)true  else false

                        iv_right_icon.setOnClickListener{
                           ShowBottomFilesOperateDialog(item,isfile)
                        }



                        if(!isfile){
                            iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_folder)
                        }else{
                            var filetype = item.file_name.substringAfterLast(".")
                            Log.e(TAG,"filetype is ${filetype}")
                            if(filetype.equals("text")||filetype.equals("txt")){
                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_txtfile)
                            }
                            if(filetype.equals("doc")||filetype.equals("docx")){
                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_doc)
                            }
                            if(filetype.equals("pdf")){
                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_pdf)
                            }
                            if(filetype.equals("exe")){
                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_exe)
                            }
                            if(filetype.equals("apk")){
                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_apk)
                            }
                            if(filetype in arrayOf("jpg","png","jpge","psd","svg")){
                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_image)
                            }
                        }
                    }



                }

            }
        })
        //</editor-fold >

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
                        CreateNewDir( CreateNewDirDialog!!.et_dir_name?.text.toString(),
                            CreateNewDirDialog!!.checkbox_is_common.isChecked,
                            pid,
                            this,
                            this
                        )
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
                        if(checkPermission(activity!!)!! == false){
                            Toastinfo("没有sd卡读取权限")
                            return@OnClickListener
                        }
                        var mintent =  Intent(Intent.ACTION_GET_CONTENT)
                        mintent.addCategory(Intent.CATEGORY_OPENABLE)

                       when(it.id){
                           R.id.tv_image->{
                               //TODO
                               Toastinfo("图片")
                               mintent.setType("image/*")
                           }
                           R.id.tv_doc->{
                               //TODO
                               Toastinfo("文档")
                               mintent.setType("*/*")

                           }
                           R.id.tv_video->{
                               Toastinfo("视频")
                               mintent.setType("video/*")
                           }
                           R.id.tv_all->{
                               Toastinfo("全部")
                               mintent.setType("*/*")
                           }
                       }
                        startActivityForResult(Intent.createChooser(mintent,"文件选择"),FILE_CHOOSER_RESULT_CODE)
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
                            Toastinfo("名称排序")
                            fileslist.sortBy { it.file_name }
                        }
                        R.id.rb_sort_by_time->{
//
                            Toastinfo("时间排序")
                            fileslist.sortBy { it.created_at }
                        }
                    }

                    SortPOPwiond?.dismiss()
                })

            }
            if(SortPOPwiond!!.isShowing){
                SortPOPwiond!!.dismiss()
            }else{
                SortPOPwiond!!.showAsDropDown(iv_sort,0,20,Gravity.CENTER)
            }

        }


        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            GetFilesListForNet(URL_LIST_FILES +"$USER_ID",this,this)
            swipeRefresh.isRefreshing = false
        }
    }

    private fun ShowBottomFilesOperateDialog(
        item: File_Bean,
        isfile: Boolean
    ) {
        if(BottomFilesOperateDialog == null){
            BottomFilesOperateDialog = BottomSheetDialog(context!!)
            val contentview = LayoutInflater.from(context!!).inflate(R.layout.dialog_bottom_files,null) as RecyclerView
            contentview.layoutManager = GridLayoutManager(context,4)
            Log.e(TAG,"isfile is ${isfile}")
            contentview.adapter = BottomFilesOperateAdapter(context,item,isfile)
            (contentview.adapter as BottomFilesOperateAdapter).SetOnRecyclerItemClickListener(object :OnRecyclerItemClickListener{
                override fun onItemClick(
                    holder: RecyclerView.ViewHolder,
                    position: Int
                ) {
                    var iteminfo = (contentview.adapter as BottomFilesOperateAdapter).InfoList[position]
                    val topdrawable = context!!.resources.getDrawable( (contentview.adapter as BottomFilesOperateAdapter).DrawableList[position])


                    topdrawable.setBounds(0, 0, topdrawable.minimumWidth, topdrawable.minimumHeight)
                    holder as BottomFilesOperateAdapter.ViewHolder
                    holder.apply {
                        var tv_text =holder.itemView as TextView
                        tv_text.text=iteminfo
                        tv_text.setCompoundDrawablesWithIntrinsicBounds(null,topdrawable,null,null)

                        tv_text.setOnClickListener{
                            var tv=it as TextView
                            when(tv.text.toString()){
                                "设置共享"->{ Toastinfo("设置共享")
                                    //TODO 文件夹分享页面

                                }
                                "链接分享"->{Toastinfo("链接分享")
                                    //TODO 跳转链接分享页面
//                                        startActivity()
                                }
                                "下载"->{Toastinfo("下载")
                                    if(item.is_dir== IS_DIR){
                                        Toastinfo("暂不支持下载文件夹")
                                        return@setOnClickListener
                                    }
//                    context.startService(Intent(context,DaemonService::class.java).apply {
//                        action ="downFiles"
//                        putExtra("file",item)
//                    })
                                    val  mainActivity=activity as MainActivity
                                    mainActivity.myBinder as DaemonService.MyBinder
                                    (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.GetFile(item)

                                }
                                "复制到"->{Toastinfo("复制到")}
                                "移动到"->{Toastinfo("移动到")}
                                "重命名"->{Toastinfo("重命名")}
                                "删除"->{
                                    Toastinfo("删除")
                                    ShowFileDeleteTipsDialog(item.file_id)
                                }
                                "详细信息"->{Toastinfo("详细信息")
                                    mContext?.startActivity(Intent(context, FileInfoActivity::class.java).apply {
                                        putExtra("file",item)
                                    })
                                }
                            }
                            BottomFilesOperateDialog?.dismiss()
                        }
                    }

                }

            })
            BottomFilesOperateDialog?.setContentView(contentview)
        }



        BottomFilesOperateDialog?.show()
    }

    private fun ShowFileDeleteTipsDialog(file_id:String) {
        if(FileDeleteTipsDialog ==null){
            FileDeleteTipsDialog = GetFileDeleteTipsDialog(mContext!!,View.OnClickListener {FileDeleteTipsDialog?.dismiss()  },View.OnClickListener{
                DeleteFile(file_id,this,this@MyFilesFragment)
                FileDeleteTipsDialog?.dismiss()

            })
        }
        FileDeleteTipsDialog?.show()
    }


    override fun initData() {


        GetFileList(USER_ID)
    }

    private fun GetFileList(pid: String) {
        GetFilesListForNet(URL_LIST_FILES +"$pid",this,this)
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

    //<editor-fold desc=" 网络请求回调  ">
    override fun OnNetPostSucces(request: Request<String, out Request<Any, Request<*, *>>>?, data: String) {
        when(request?.url){
            URL_LIST_FILES +"${USER_ID}"->{
                Log.e(TAG,"request.method.name is ${request.method.name}")
                when(request.method.name){
                    HttpMethod.GET.name->{
                        //获取我的文件列表
                        Toastinfo("获取文件列表成功")
                        fileslist.clear()
                        fileslist.addAll(Gson().fromJson<List<File_Bean>>(data,object : TypeToken<List<File_Bean>>(){}.type) as ArrayList<File_Bean>)
                        Log.e(TAG,"filelist size is ${fileslist.size}")
                        adapter?.notifyDataSetChanged()
                    }

                }


            }
            URL_ADD_File->{

                Toastinfo("添加文件列表成功")
                //刷新列表
                //TODO USER_ID ->PID
                GetFileList(USER_ID)
            }
            URL_DELETE_FILE ->{
                Toastinfo("删除文件成功")
                //TODO USER_ID ->PID
                GetFileList(USER_ID)
            }

        }

    }
    //</editor-fold>

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE && data !=null) {
            AddFile(data.dataString!!,pid,this,this)

        }


    }


}