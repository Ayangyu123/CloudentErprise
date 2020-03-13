package com.ucas.cloudenterprise.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.adapter.FilesAdapter
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.File_Bean
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lzy.okgo.model.HttpMethod
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.BottomFilesOperateAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.*
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.common_head.tv_title
import kotlinx.android.synthetic.main.dialog_create_new_dir.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.et_dir_name
import kotlinx.android.synthetic.main.dialog_create_new_dir.view.*
import kotlinx.android.synthetic.main.swiperefreshlayout.*
import kotlinx.android.synthetic.main.top_file_operate.*
import org.json.JSONObject


/**
@author simpler
@create 2020年01月10日  14:31
 */
class MyFilesFragment: BaseFragment(),BaseActivity.OnNetCallback {
    val TAG ="MyFilesFragment"
    var fileslist = ArrayList<File_Bean>()
    lateinit var adapter:FilesAdapter
     var CreateNewDirDialog:Dialog ?=null
     var UpFileTypeDialog:Dialog ?=null
     var SortPOPwiond:PopupWindow ?=null
     var FileDeleteTipsDialog:Dialog ?=null
     var ReanmeDialog:Dialog ?=null
    var BottomFilesOperateDialog:BottomSheetDialog ? =null
    var pid ="root"
    lateinit var pid_stack : ArrayList<String>
    lateinit var pid_name_maps : HashMap<String,String>
    var Is_Checked_Sum =0



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
                        tv_file_name.text = item.file_name
                        tv_file_create_time.text = item.created_at

                        val isfile = if(item.is_dir==-1)true  else false


                        if(item.is_show_checked_view) {
                            iv_right_icon.visibility =View.GONE
                            checkbox_is_checked.visibility = View.VISIBLE
                            checkbox_is_checked.isChecked =item.is_checked
                            rl_file_item_root.setOnClickListener {
                                item.is_checked =  !item.is_checked
                                checkbox_is_checked.isChecked= item.is_checked

                            }
                            checkbox_is_checked.setOnCheckedChangeListener { button, ischecked ->
                                if(ischecked){
                                    Is_Checked_Sum = Is_Checked_Sum+1

                                }else{
                                    Is_Checked_Sum = Is_Checked_Sum-1

                                }
                                tv_title.text ="已选定${Is_Checked_Sum}个"
                                item.is_checked = ischecked
                            }

                        }else{
                            iv_right_icon.visibility =View.VISIBLE
                            checkbox_is_checked.visibility = View.GONE
                            rl_file_item_root.setOnClickListener {
                                //如果不是文件，是文件夹 点击获取改文件夹下的内容 pid = 该文件id
                                if(!isfile){
                                    iv_back.visibility = View.VISIBLE
                                    tv_title.text = item.file_name
                                    pid_stack.add(0,item.file_id)
                                    pid_name_maps.put( item.file_id, item.file_name)
                                    pid = item.file_id
                                    GetFileList()
                                }
                            }
                            iv_right_icon.setOnClickListener{
                                ShowBottomFilesOperateDialog(item,isfile)
                            }
                        }




                        if(!isfile){
                            iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_folder)
                        }else{
                            iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_unknown)
                        }
                    }



                }

            }
        })
        //</editor-fold >
        //<editor-fold  desc ="显示新建文件夹Dialog">
        iv_create_dir.setOnClickListener{
           ShowCreateNewDirDialog()
        }
        //</editor-fold >
        //<editor-fold  desc ="显示上传文件Dialog">
        iv_show_up_file_type_dilaog.setOnClickListener{
//            if(UpFileTypeDialog == null){
//
//                UpFileTypeDialog = GetUploadFileTypeDialog(mContext!!,View.OnClickListener{
//
//                    UpFileTypeDialog?.dismiss()
//                }
//                    ,View.OnClickListener{
                        if(checkPermission(activity!!)!! == false){
                            Toastinfo("没有sd卡读取权限")
                            return@setOnClickListener
                        }
                        var mintent =  Intent(Intent.ACTION_GET_CONTENT)
                        mintent.addCategory(Intent.CATEGORY_OPENABLE)

//                       when(it.id){
//                           R.id.tv_image->{
//                               //TODO
//                               Toastinfo("图片")
//                               mintent.setType("image/*")
//                           }
//                           R.id.tv_doc->{
//                               //TODO
//                               Toastinfo("文档")
//                               mintent.setType("*/*")
//
//                           }
//                           R.id.tv_video->{
//                               Toastinfo("视频")
//                               mintent.setType("video/*")
//                           }
//                           R.id.tv_all->{
//                               Toastinfo("全部")
                               mintent.setType("*/*")
//                           }
//                       }
                        startActivityForResult(Intent.createChooser(mintent,"文件选择"),FILE_CHOOSER_RESULT_CODE)
//                        UpFileTypeDialog?.dismiss()
//                    })
//            }
//            UpFileTypeDialog?.show()

        }
        //</editor-fold >
        //<editor-fold desc ="显示排序popwinod">
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
        //</editor-fold >
        //<editor-fold desc ="swipeRefresh settings">
        swipeRefresh.setColorSchemeResources(R.color.app_color)
        swipeRefresh.setOnRefreshListener {
            fileslist.clear()
            GetFileList()
            swipeRefresh.isRefreshing = false
        }
        //</editor-fold >
        //<editor-fold desc ="编辑按钮 设置">
        tv_edit.setOnClickListener {
            var text =(it as android.widget.TextView).text.toString()
            when(text){
                "编辑"->{
                    //TODO ui
                    it.text ="全选"
                    tv_title.text="已选定${Is_Checked_Sum}个"
                    iv_back.visibility =View.VISIBLE
                    fileslist.forEach {
                        it.is_show_checked_view =true
                    }
                    adapter.notifyDataSetChanged()
                }
                "全选"->{
                    it.text ="全不选"
                    fileslist.forEach {
                        it.is_checked =true
                    }
                    adapter.notifyDataSetChanged()
                }
                "全不选"->{
                    it.text ="全选"
                    fileslist.forEach {
                        it.is_checked =false
                    }
                    adapter.notifyDataSetChanged()
                }
            }

        }
        //TODO 多选不显示
        tv_edit.visibility =View.INVISIBLE
        //</editor-fold >


        //<editor-fold  desc ="返回按钮 设置" >
        iv_back.setOnClickListener {

            if( tv_edit.text.equals("编辑")&&  pid_name_maps.containsValue(tv_title.text)&&pid_stack.size>1){ //选择文件夹显示返回

                pid_name_maps.remove(pid_stack[0])
                pid_stack.remove(pid_stack[0])
                pid = pid_stack[0]
                tv_title.text =pid_name_maps[pid_stack[0]]
                if(pid.equals("root")){
                    iv_back.visibility =View.INVISIBLE
                }
                GetFileList()

            }else{ //多选操作显示
                tv_title.text =  pid_name_maps[pid_stack[0]]//"我的文件"
                tv_edit.text ="编辑"
                iv_back.visibility =View.INVISIBLE
                fileslist.forEach {
                    it.is_show_checked_view =false
                    it.is_checked =false
                }
                Is_Checked_Sum =0
                adapter.notifyDataSetChanged()
            }

        }
        //</editor-fold >
        //<editor-fold  desc ="搜索按钮 设置" >
        iv_search.setOnClickListener {
            startActivity(Intent(mContext,SearchFileActivity::class.java).putExtra("form","myfiles"))
        }  //</editor-fold >
        //<editor-fold  desc ="刷新 设置" >
        tv_refresh.setOnClickListener {  GetFileList() }
        //</editor-fold >
        tv_edit.visibility = View.INVISIBLE
    }

    //<editor-fold  desc ="新建文件夹 Dialog" >
    fun ShowCreateNewDirDialog() {
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
    //</editor-fold >
//<editor-fold  desc ="底部操作 Dialog" >
    private fun ShowBottomFilesOperateDialog(
        item: File_Bean,
        isfile: Boolean
    ) {

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
                                    mContext?.startActivity(Intent(context, SetCommonFileActivity::class.java).apply {
                                        putExtra("file",item)
                                    })

                                }
                                "链接分享"->{Toastinfo("链接分享")
                                    //TODO 跳转链接分享页面
                                    mContext?.startActivity(Intent(context, LinkSharedActivity::class.java).apply {
                                        putExtra("file",item)
                                    })
                                }
                                "下载"->{
                                    Toastinfo("下载")
                                    var  mainActivity=activity as MainActivity
                                    if(!checkPermission(mainActivity)!!){
                                        Toastinfo("没有sd卡写入权限")
                                        return@setOnClickListener
                                    }
                                    if(item.is_dir== IS_DIR){
                                        Toastinfo("暂不支持下载文件夹")
                                        return@setOnClickListener
                                    }
//                    context.startService(Intent(context,DaemonService::class.java).apply {
//                        action ="downFiles"
//                        putExtra("file",item)
//                    })

                                    if(DaemonService.daemon!=null ){
                                        mainActivity.myBinder as DaemonService.MyBinder
                                        (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.GetFile(item)
                                    }else{
                                        Toastinfo("未启动服务")
                                    }


                                }
                                "复制到"->{Toastinfo("复制到")
                                   startActivityForResult(Intent(context, ChooseDestDirActivity::class.java).apply {
                                        putExtra("file",item)
                                        putExtra("type",ChooseDestDirActivity.COPY)
                                    },ChooseDestDirActivity.COPY)}
                                "移动到"->{Toastinfo("移动到")
                                    startActivityForResult(Intent(context, ChooseDestDirActivity::class.java).apply {
                                        putExtra("file",item)
                                        putExtra("type",ChooseDestDirActivity.MOVE)
                                    },ChooseDestDirActivity.MOVE)
                                }
                                "重命名"->{Toastinfo("重命名")
                                        ShowRenameDialog(item)
                                }
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




        BottomFilesOperateDialog?.show()
    }
    //</editor-fold >
    //<editor-fold  desc ="重命名 Dialog" >
    private fun ShowRenameDialog(item: File_Bean) {
         ReanmeDialog =Dialog(mContext!!).apply {
             var contentview =
                 LayoutInflater.from(mContext).inflate(R.layout.dialog_file_rename,null)
             contentview.apply {
                 et_dir_name.text = SetEt_Text("${item.file_name}")
                 et_dir_name.addTextChangedListener {
                         if(et_dir_name.text.equals(item.file_name)){
                             tv_commit.isEnabled = false
                         }else{
                             tv_commit.isEnabled = true
                         }

                 }
                 tv_cancle.setOnClickListener{
                     ReanmeDialog?.dismiss()
                 }
                 tv_commit.setOnClickListener {
                     NetRequest("${URL_FILE_RENAME}", NET_POST,HashMap<String,Any>().apply {
                         put("user_id","${USER_ID}")
                         put("file_id","${item.file_id}")
                         put("file_name","${et_dir_name.text}")

                     },this,this@MyFilesFragment)
                     ReanmeDialog?.dismiss()
                 }

             }
             setContentView(contentview)
             setCancelable(true)
         }


        ReanmeDialog?.show()
    }
    //</editor-fold >
    //<editor-fold  desc ="删除提示 Dialog" >
    fun ShowFileDeleteTipsDialog(file_id:String) {

            FileDeleteTipsDialog = GetFileDeleteTipsDialog(mContext!!,View.OnClickListener {FileDeleteTipsDialog?.dismiss()  },View.OnClickListener{
                DeleteFile(file_id,this,this@MyFilesFragment)
                FileDeleteTipsDialog?.dismiss()

            })

        FileDeleteTipsDialog?.show()
    }
    //</editor-fold >

    override fun initData() {

        pid_stack = ArrayList<String>().apply {
            add(0,"root")
        }
        pid_name_maps = HashMap()
        pid_name_maps.put("root","我的文件")
        pid = pid_stack[0]

        GetFileList()
    }
    //<editor-fold  desc =" 获取文件列表" >
    public fun GetFileList() {
        GetFilesListForNet(URL_LIST_FILES +"${USER_ID}/status/${IS_UNCOMMON_DIR}/p/${pid}/dir/${ALL_FILE}",this,this)
    }
    //</editor-fold >


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
            URL_LIST_FILES +"${USER_ID}/status/${IS_UNCOMMON_DIR}/p/${pid}/dir/${ALL_FILE}"->{
                Log.e(TAG,"request.method.name is ${request.method.name}")
                when(request.method.name){
                    HttpMethod.GET.name->{
                        //获取我的文件列表
                        if(JSONObject(data).isNull("data")){
                            ll_empty.visibility = View.VISIBLE
                            swipeRefresh.visibility = View.INVISIBLE
                            tv_edit.visibility = View.INVISIBLE
                            return

                        }else{
                            ll_empty.visibility = View.INVISIBLE
                            swipeRefresh.visibility = View.VISIBLE

                        }

                        //获取我的文件列表
                        Toastinfo("获取文件列表成功")
                        fileslist.clear()
                        fileslist.addAll(Gson().fromJson<List<File_Bean>>(JSONObject(data).getJSONArray("data").toString(),object : TypeToken<List<File_Bean>>(){}.type) as ArrayList<File_Bean>)
                        adapter?.notifyDataSetChanged()
//              TODO  编辑按钮显示         tv_edit.visibility =  if (!fileslist.isEmpty()) View.VISIBLE  else View.INVISIBLE


                    }

                }


            }
            URL_ADD_File->{

                Toastinfo("添加文件列表成功")
                //刷新列表
                //TODO USER_ID ->PID
                GetFileList()
            }
            URL_DELETE_FILE ->{
                Toastinfo("删除文件成功")
                //TODO USER_ID ->PID
                GetFileList()
            }
            URL_FILE_RENAME ->{
                Toastinfo("文件重命名成功")
                GetFileList()

            }
            URL_FILE_COPY ->{
                Toastinfo("文件复制成功")
                GetFileList()

            }
            URL_FILE_MOV ->{
                Toastinfo("文件移动成功")
                GetFileList()

            }

        }

    }
    //</editor-fold>

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( data !=null) {

            when(requestCode){
                FILE_CHOOSER_RESULT_CODE ->{ //选择文件上传文件

                        var  mainActivity=activity as MainActivity
                        mainActivity.myBinder as DaemonService.MyBinder
                        (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.AddFile(data.dataString,pid,this,this)


                }
                ChooseDestDirActivity.COPY ->{ //文件复制
                  var   file_id= data.getStringExtra("file_id")
                   var  pid= data.getStringExtra("pid")
                    var params =HashMap<String,Any>().apply {
                        put("user_id","${ USER_ID}")
                        put("file_id",file_id)
                        put("pid",pid)
                    }
                    NetRequest(URL_FILE_COPY, NET_POST,params,this,this)

                }
                ChooseDestDirActivity.MOVE ->{ //文件移动
                    var   file_id= data.getStringExtra("file_id")
                    var  pid= data.getStringExtra("pid")
                    var params =HashMap<String,Any>().apply {
                        put("user_id","${ USER_ID}")
                        put("file_id",file_id)
                        put("pid",pid)
                    }
                    NetRequest(URL_FILE_MOV, NET_POST,params,this,this)
                }

            }




        }


    }


}

fun main() {
    var testArrayList = ArrayList<String >()
    testArrayList.add("root")
    testArrayList.add(0,"test")
    testArrayList.forEach {
        println(it)
    }
    testArrayList.remove(testArrayList[0])
    print("romove 0")
    testArrayList.forEach {
        println(it)
    }
}