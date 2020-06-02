package com.ucas.cloudenterprise.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.BottomFilesOperateAdapter
import com.ucas.cloudenterprise.adapter.FilesAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.app.MyApplication.Companion.context
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.utils.GetFileDeleteTipsDialog
import com.ucas.cloudenterprise.utils.SetEt_Text
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import kotlinx.android.synthetic.main.activity_searchfile.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.view.*
import kotlinx.android.synthetic.main.swiperefreshlayout.*
import org.json.JSONObject

/**
@author simpler
@create 2020年01月21日  14:05
 */
class SearchFileActivity : BaseActivity(),BaseActivity.OnNetCallback{

    val TAG ="SearchFileActivity"
    var searchtype = 1 // 1 my  2 other
    var fileslist = ArrayList<File_Bean>()
    lateinit var adapter:FilesAdapter
    var FileDeleteTipsDialog:Dialog ? =null
    var ReanmeDialog:Dialog ?=null

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
            when(request!!.url){
                URL_FILE_SEARCH ->{
                    if(JSONObject(data).isNull("data")){
                        Toastinfo("没有搜索到该文件")
                        return
                    }
                    Toastinfo("获取文件列表成功")
                    fileslist.clear()
                    fileslist.addAll(Gson().fromJson<List<File_Bean>>(JSONObject(data).getJSONArray("data").toString(),object : TypeToken<List<File_Bean>>(){}.type) as ArrayList<File_Bean>)
                    Log.e(TAG,"filelist size is ${fileslist.size}")
                    adapter?.notifyDataSetChanged()
                    if(fileslist.size==0){
                        ll_empty.visibility = View.VISIBLE
                        swipeRefresh.visibility = View.INVISIBLE
                    }else{
                        ll_empty.visibility = View.INVISIBLE
                        swipeRefresh.visibility = View.VISIBLE
                    }
                }
            }
    }

    override fun GetContentViewId(): Int= R.layout.activity_searchfile

    override fun InitView() {
        tv_cancle_search.setOnClickListener {
            finish()
        }
        when (intent.getStringExtra("from")) {
            "myfiles" -> {
                searchtype =1
                et_search_key_word.hint = SetEt_Text("搜索我的文件")
            }
            "commonfiles" -> {
                searchtype =2
                et_search_key_word.hint = SetEt_Text("他人共享的文件")
            }
        }


        // 监听软键盘的按键
        et_search_key_word.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                //回车等操作
                if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_GO
                    || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                            && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    // 搜索
                search()
                }
                return true;
            }

        })


        adapter = FilesAdapter(this,fileslist)
        rc_myfiles.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rc_myfiles.adapter = adapter
        rc_myfiles.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
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
                            iv_right_icon.visibility = View.GONE
                            checkbox_is_checked.visibility = View.VISIBLE
                            checkbox_is_checked.isChecked =item.is_checked
                            checkbox_is_checked.setOnCheckedChangeListener { button, ischecked ->
                                item.is_checked = ischecked
                            }

                        }else{
                            iv_right_icon.visibility = View.VISIBLE
                            checkbox_is_checked.visibility = View.GONE
                            iv_right_icon.setOnClickListener{
                                ShowBottomFilesOperateDialog(item,isfile)
                            }
                        }




                        if(!isfile){
                            iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_folder)
                            rl_file_item_root.setOnClickListener {
                                var mintent =intent.putExtra("destfold",item)
                                setResult(Activity.RESULT_OK,mintent)
                                finish()
                            }
                        }else{
                            iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_unknown)


                        }
                    }



                }

            }
        })
    }


    private fun ShowBottomFilesOperateDialog(
        item: File_Bean,
        isfile: Boolean
    ) {

        var BottomFilesOperateDialog = BottomSheetDialog(this)
        val contentview = LayoutInflater.from(context!!).inflate(R.layout.dialog_bottom_files,null) as RecyclerView
        contentview.layoutManager = GridLayoutManager(context,4)
        Log.e(TAG,"isfile is ${isfile}")
        contentview.adapter =  BottomFilesOperateAdapter(context,item,isfile,item.weight,isroot_file = item.pid.equals("root"),ispshare_file = searchtype==2)
//        contentview.adapter = if(searchtype==2) BottomFilesOperateAdapter(context,item,isfile,item.weight,isroot_file = item.pid.equals("root"),ispshare_file = item.pshare==-1) else BottomFilesOperateAdapter(context,item,isfile)
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
                             startActivity(Intent(context, SetCommonFileActivity::class.java).apply {
                                    putExtra("file",item)
                                })

                            }
                            "链接分享"->{Toastinfo("链接分享")
                                //TODO 跳转链接分享页面
                                startActivity(Intent(context, LinkSharedActivity::class.java).apply {
                                    putExtra("file",item)
                                })
                            }
                            "下载"->{
                                Toastinfo("下载")

                                if(!com.ucas.cloudenterprise.app.checkPermission(this@SearchFileActivity)!!){
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
                                                                    context.startService(Intent(context,DaemonService::class.java).apply {
                                                                            action ="downFiles"
                                                                            putExtra("file",item)
                                                                        })
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
                                ShowFileDeleteTipsDialog(item)
                            }
                            "详细信息"->{Toastinfo("详细信息")
                                startActivity(Intent(context, FileInfoActivity::class.java).apply {
                                    putExtra("file",item)
                                })
                            }
                        }
                        when(tv.text.toString()){
                            "设置共享"->{ Toastinfo("设置共享")
                                //TODO 文件夹分享页面
                                startActivity(Intent(context, FileInfoActivity::class.java).apply {
                                    putExtra("file",item)
                                })

                            }
                            "链接分享"->{Toastinfo("链接分享")
                                //TODO 跳转链接分享页面
                               startActivity(Intent(context, FileInfoActivity::class.java).apply {
                                    putExtra("file",item)
                                })
                            }
                            "下载"->{
                                Toastinfo("下载")

                                if(!com.ucas.cloudenterprise.app.checkPermission(this@SearchFileActivity)!!){
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

                                (myBinder as DaemonService.MyBinder).GetDaemonService()?.GetFile(item)

                            }
                            "复制到"->{Toastinfo("复制到")}
                            "移动到"->{Toastinfo("移动到")}
                            "重命名"->{Toastinfo("重命名")}
                            "删除"->{
                                Toastinfo("删除")
                                ShowFileDeleteTipsDialog(item)
                            }
                            "详细信息"->{Toastinfo("详细信息")
                               startActivity(Intent(context, FileInfoActivity::class.java).apply {
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


    fun ShowFileDeleteTipsDialog(item: File_Bean) {

        FileDeleteTipsDialog = GetFileDeleteTipsDialog(this,View.OnClickListener {FileDeleteTipsDialog?.dismiss()  },View.OnClickListener{
            val params = HashMap<String, Any>()
            params["file_id"] = "${item.file_id}"
            params["user_id"] = USER_ID
            if(searchtype ==2){
            params["falg"] = 1}
            NetRequest(URL_DELETE_FILE, NET_PUT, params, this, object :BaseActivity.OnNetCallback{
                override fun OnNetPostSucces(
                    request: Request<String, out Request<Any, Request<*, *>>>?,
                    data: String
                ) {
                    Toastinfo("删除文件成功")
                    fileslist.remove(item)
                    adapter.notifyDataSetChanged()

                }

            })
            FileDeleteTipsDialog!!.dismiss()

        })

        FileDeleteTipsDialog?.show()
    }
    private fun search() {
        if(TextUtils.isEmpty(et_search_key_word.text)){
            Toastinfo("搜索内容不能为空")
            return
        }
        var imm:InputMethodManager =   getSystemService(INPUT_METHOD_SERVICE)  as InputMethodManager
            if (imm.isActive()){
                imm.hideSoftInputFromWindow(et_search_key_word.getWindowToken(), 0); //隐藏软键盘
            }
        NetRequest("${URL_FILE_SEARCH}", NET_POST,HashMap<String,Any>().apply{
            put("user_id","$USER_ID")
            put("file_name","${et_search_key_word.text}")
            if(searchtype==2){
                put("falg","1")
            }
        },this,this)

    }

    //<editor-fold  desc ="重命名 Dialog" >
    private fun ShowRenameDialog(item: File_Bean) {
        ReanmeDialog =Dialog(this@SearchFileActivity).apply {
            var contentview =
                LayoutInflater.from(this@SearchFileActivity).inflate(R.layout.dialog_file_rename,null)
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
                    NetRequest("$URL_FILE_RENAME", NET_POST,HashMap<String,Any>().apply {
                        put("user_id","${USER_ID}")
                        put("file_id","${item.file_id}")
                        put("file_name","${et_dir_name.text}")

                    },this,object :BaseActivity.OnNetCallback{
                        override fun OnNetPostSucces(
                            request: Request<String, out Request<Any, Request<*, *>>>?,
                            data: String
                        ) {
                            if(VerifyUtils.VerifyResponseData(data)){

                            }
                        }

                    })
                    ReanmeDialog?.dismiss()
                }

            }
            setContentView(contentview)
            setCancelable(true)
        }


        ReanmeDialog?.show()
    }
    //</editor-fold >

    override fun InitData() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( data !=null) {

            when(requestCode){
//                FILE_CHOOSER_RESULT_CODE ->{ //选择文件上传文件
//
//                    var  mainActivity=activity as MainActivity
//                    mainActivity.myBinder as DaemonService.MyBinder
//                    (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.AddFile(data.dataString,pid,this,this)
//
//
//                }
                ChooseDestDirActivity.COPY ->{ //文件复制
                    var   file_id= data.getStringExtra("file_id")
                    var  pid= data.getStringExtra("pid")
                    var params =HashMap<String,Any>().apply {
                        put("user_id","${ USER_ID}")
                        put("file_id",file_id)
                        put("pid",pid)
                        if(searchtype==2){
                        put("falg",1)}
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
                        if(searchtype==2){
                        put("falg",1)}
                    }
                    NetRequest(URL_FILE_MOV, NET_POST,params,this,this)
                }

            }




        }


    }

}