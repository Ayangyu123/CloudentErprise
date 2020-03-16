package com.ucas.cloudenterprise.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.model.HttpMethod
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.BottomFilesOperateAdapter
import com.ucas.cloudenterprise.adapter.FilesAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.ui.*
import com.ucas.cloudenterprise.utils.GetFileDeleteTipsDialog
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.view.*
import kotlinx.android.synthetic.main.others_share_fragment.*
import kotlinx.android.synthetic.main.swiperefreshlayout.*
import org.json.JSONObject

/**
@author simpler
@create 2020年01月10日  14:31
 */
class OthersShareFragment : BaseFragment(),BaseActivity.OnNetCallback {
    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        when(request?.url){
            URL_LIST_FILES +"$USER_ID/status/$IS_UNCOMMON_DIR/p/${pid}/dir/$ALL_FILE"->{
                when(request.method.name){
                    HttpMethod.GET.name->{
                        //获取我的文件列表
                        if(JSONObject(data).isNull("data")){
                            ll_empty.visibility = View.VISIBLE
                            swipeRefresh.visibility = View.INVISIBLE
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

                    }

                }
            }
        }
    }

    val TAG ="OthersShareFragment"
    var fileslist = ArrayList<File_Bean>()
    lateinit var adapter: FilesAdapter
    var CreateNewDirDialog: Dialog?=null
    var UpFileTypeDialog: Dialog?=null
    var SortPOPwiond: PopupWindow?=null
    var FileDeleteTipsDialog: Dialog?=null
    var BottomFilesOperateDialog: BottomSheetDialog? =null
    var ReanmeDialog:Dialog ?=null
    var pid ="root"
    var Is_Checked_Sum =0
    lateinit var pid_stack : ArrayList<String>
    lateinit var pid_name_maps : HashMap<String,String>

    override fun initView() {
        tv_title.text ="他人共享"
        iv_back.visibility =View.INVISIBLE
        view_head_to_search_activity.setOnClickListener {
            startActivity(Intent(mContext, SearchFileActivity::class.java).putExtra("form","commonfiles"))
        }

        //<editor-fold desc=" 设置files RecyclerView  ">
        adapter = FilesAdapter(mContext,fileslist)
        rc_myfiles.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false)
        rc_myfiles.adapter = adapter
        rc_myfiles.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        adapter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                var item =fileslist[position]
                holder.apply {
                    holder as FilesAdapter.ViewHolder
                    holder.apply {
                        tv_file_name.text = item.file_name
                        tv_file_create_time.text = item.created_at

                        val isfile = if(item.is_dir==-1)true  else false
                        if(item.is_show_checked_view){
                            iv_right_icon.visibility =View.GONE
                            checkbox_is_checked.visibility = View.VISIBLE
                            checkbox_is_checked.isChecked =item.is_checked
                            checkbox_is_checked.setOnCheckedChangeListener { button, ischecked ->
//                                if(ischecked){
//                                    Is_Checked_Sum = Is_Checked_Sum+1
//
//                                }else{
//                                    Is_Checked_Sum = Is_Checked_Sum-1
//
//                                }
//                                tv_title.text ="已选定${Is_Checked_Sum}个"
//                                item.is_checked = ischecked
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
//                            var filetype = item.file_name.substringAfterLast(".")
//                            Log.e(TAG,"filetype is ${filetype}")
//                            if(filetype.equals("text")||filetype.equals("txt")){
//                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_txtfile)
//                            }
//                            if(filetype.equals("doc")||filetype.equals("docx")){
//                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_doc)
//                            }
//                            if(filetype.equals("pdf")){
//                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_pdf)
//                            }
//                            if(filetype.equals("exe")){
//                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_exe)
//                            }
//                            if(filetype.equals("apk")){
//                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_apk)
//                            }
//                            if(filetype in arrayOf("jpg","png","jpge","psd","svg")){
//                                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_image)
//                            }
                        }
                    }



                }

            }
        })
        //</editor-fold >
        //TODO 多选不显示
        tv_edit.visibility =View.INVISIBLE


        //<editor-fold  desc ="返回按钮 设置" >
        iv_back.setOnClickListener {

            if( tv_edit.text.equals("编辑")&&  pid_name_maps.containsValue(tv_title.text.toString())&&pid_stack.size>1){ //选择文件夹显示返回

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

    }


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
                                    putExtra("type", ChooseDestDirActivity.COPY)
                                }, ChooseDestDirActivity.COPY)}
                            "移动到"->{Toastinfo("移动到")
                                startActivityForResult(Intent(context, ChooseDestDirActivity::class.java).apply {
                                    putExtra("file",item)
                                    putExtra("type", ChooseDestDirActivity.MOVE)
                                }, ChooseDestDirActivity.MOVE)
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
//                    tv_text.setOnClickListener{
//                        var tv=it as TextView
//                        when(tv.text.toString()){
//                            "设置共享"->{ Toastinfo("设置共享")
//                                //TODO 文件夹分享页面
//                                mContext?.startActivity(Intent(context, FileInfoActivity::class.java).apply {
//                                    putExtra("file",item)
//                                })
//
//                            }
//                            "链接分享"->{
//                                Toastinfo("链接分享")
//                                //TODO 跳转链接分享页面
//                                mContext?.startActivity(Intent(context, FileInfoActivity::class.java).apply {
//                                    putExtra("file",item)
//                                })
//                            }
//                            "下载"->{
//                                Toastinfo("下载")
//                                var  mainActivity=activity as MainActivity
//                                if(!checkPermission(mainActivity)!!){
//                                    Toastinfo("没有sd卡写入权限")
//                                    return@setOnClickListener
//                                }
//                                if(item.is_dir== IS_DIR){
//                                    Toastinfo("暂不支持下载文件夹")
//                                    return@setOnClickListener
//                                }
////                    context.startService(Intent(context,DaemonService::class.java).apply {
////                        action ="downFiles"
////                        putExtra("file",item)
////                    })
//
//                                mainActivity.myBinder as DaemonService.MyBinder
//                                (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.GetFile(item)
//
//                            }
//                            "复制到"->{
//                                Toastinfo("复制到")
//                            }
//                            "移动到"->{
//                                Toastinfo("移动到")
//                            }
//                            "重命名"->{
//                                Toastinfo("重命名")
//                            }
//                            "删除"->{
//                                Toastinfo("删除")
//                                ShowFileDeleteTipsDialog(item.file_id)
//                            }
//                            "详细信息"->{
//                                Toastinfo("详细信息")
//                                mContext?.startActivity(Intent(context, FileInfoActivity::class.java).apply {
//                                    putExtra("file",item)
//                                })
//                            }
//                        }
//                        BottomFilesOperateDialog?.dismiss()
//                    }
                }

            }

        })
        BottomFilesOperateDialog?.setContentView(contentview)




        BottomFilesOperateDialog?.show()
    }
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

                    },this,this@OthersShareFragment)
                    ReanmeDialog?.dismiss()
                }

            }
            setContentView(contentview)
            setCancelable(true)
        }


        ReanmeDialog?.show()
    }
    //</editor-fold >


    fun ShowFileDeleteTipsDialog(file_id:String) {

        FileDeleteTipsDialog = GetFileDeleteTipsDialog(mContext!!,View.OnClickListener {FileDeleteTipsDialog?.dismiss()  },View.OnClickListener{
            DeleteFile(file_id,this,this)
            FileDeleteTipsDialog?.dismiss()

        })

        FileDeleteTipsDialog?.show()
    }

    override fun initData(){
        pid_stack = ArrayList<String>().apply {
            add(0,"root")
        }
        pid_name_maps = HashMap()
        pid_name_maps.put("root","他人共享")
        pid = pid_stack[0]
        GetFileList()
    }

    private fun GetFileList() {
        GetFilesListForNet(URL_LIST_FILES +"$USER_ID/status/$IS_UNCOMMON_DIR/p/${pid}/dir/$ALL_FILE",this,this)
    }

    override fun GetRootViewID()= R.layout.others_share_fragment
    companion object{
         private var instance: OthersShareFragment? = null

        fun getInstance( param1:Boolean?,  param2:String?): OthersShareFragment {
            if (instance == null) {
                synchronized(OthersShareFragment::class.java) {
                    if (instance == null) {
                        instance = OthersShareFragment()
//                            .apply {
//                            this.arguments =Bundle().apply {
//                                putBoolean("param1", param1)
//                                putString("param2",param2)
//                            }
//                        }
                    }
                }
            }
            return instance!!
        }
    }

}