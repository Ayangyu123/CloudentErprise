package com.ucas.cloudenterprise.ui.fragment

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
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
import com.ucas.cloudenterprise.base.BaseActivity.OnNetCallback
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.event.MessageEvent
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.ui.*
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.*
import kotlinx.android.synthetic.main.dialog_create_new_dir.view.*
import kotlinx.android.synthetic.main.swiperefreshlayout.ll_empty
import kotlinx.android.synthetic.main.swiperefreshlayout.rc_myfiles
import kotlinx.android.synthetic.main.swiperefreshlayout.swipeRefresh
import kotlinx.android.synthetic.main.swiperefreshlayout.tv_refresh
import kotlinx.android.synthetic.main.swiperefreshlayout_myfilesfragment.*
import kotlinx.android.synthetic.main.top_file_operate.*
import me.rosuh.filepicker.config.FilePickerManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.File

/**
@author simpler
@create 2020年01月10日  14:31
 */
class MyFilesFragment : BaseFragment(), OnNetCallback {
    private val SET_PSHARE_CODE: Int = 384
    val TAG = "MyFilesFragment"
    var fileslist = ArrayList<File_Bean>()
    lateinit var adapter: FilesAdapter
    var CreateNewDirDialog: Dialog? = null

    var UpFileTypeDialog: Dialog? = null
    var SortPOPwiond: PopupWindow? = null
    var FileDeleteTipsDialog: Dialog? = null
    var ReanmeDialog: Dialog? = null
    var BottomFilesOperateDialog: BottomSheetDialog? = null

    var pid = "root"
    var item_file_id: String? = null
    var File_path_yy: String? = null
    var File_path_yy_name: String? = null
    var item_file_name: String? = null

    lateinit var pid_stack: ArrayList<String>
    lateinit var get_path: String
    lateinit var pid_name_maps: HashMap<String, String>
    var Is_Checked_Sum = 0
    override fun initView() {
        Log.e("39.106.216.189", "123")
        //<editor-fold desc=" 设置files RecyclerView  ">
        //获取适配器
        adapter = FilesAdapter(mContext, fileslist)
        //设置布局方向
        rc_myfiles.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        //绑定适配器
        rc_myfiles.adapter = adapter
//       rc_myfiles.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))

        //对获取的条目进行监听，主要是做条目后面图片的一个监听效果
        adapter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                //  Log.e("222","要分享的id"+ mMyDirsFragment.fileslist[position].file_id)
                var item = fileslist[position]
                if (item.is_dir == 1) {  //必须是文件夹才能显示fileid
                    item_file_id = item.file_id
                    item_file_name = item.file_name
                    Log.e("yyy", "我的文件页面遍历:" + item_file_id + "--" + item_file_name)
                    // Log.e("yyy", "点击的:" + item)
                    /*   Log.e("hhh", "item_file_id:" + item_file_id+"--"+item.file_name)
                       Log.e("hhh", "item_path1  :" + item_file_id+"--"+item.file_name)*/
                    // Log.e("yangyu", "MyFilesFragmentent的item:" + item)
                }
                //打印点击的信息 从信息中获取fild_id  然后传送给addFile
                // holder.itemView.

                holder.apply {
                    holder as FilesAdapter.ViewHolder
                    holder.apply {
                        tv_file_name.text = item.file_name
                        tv_file_create_time.text = item.created_at
                        //  Log.e("yyy",item.file_id+"-----"+item.file_name)
                        //文件
                        val isfile = if (item.is_dir == -1) true else false
                        //文件夹
                        val isfolder = if (item.is_dir == 1) true else false
                        if (item.is_show_checked_view) {
                            iv_right_icon.visibility = View.GONE
                            checkbox_is_checked.visibility = View.VISIBLE
                            checkbox_is_checked.isChecked = item.is_checked
                            rl_file_item_root.setOnClickListener {
                                item.is_checked = item.is_checked
                                checkbox_is_checked.isChecked = item.is_checked
                            }
                            checkbox_is_checked.setOnCheckedChangeListener { button, ischecked ->
                                if (ischecked) {
                                    Is_Checked_Sum = Is_Checked_Sum + 1
                                } else {
                                    Is_Checked_Sum = Is_Checked_Sum - 1
                                }
                                tv_title.text = "已选定${Is_Checked_Sum}个"
                                item.is_checked = ischecked
                            }
                        } else {
                            iv_right_icon.visibility = View.VISIBLE
                            checkbox_is_checked.visibility = View.GONE
                            rl_file_item_root.setOnClickListener {
                                //如果不是文件，是文件夹 点击获取改文件夹下的内容 pid = 该文件id
                                if (!isfile) {
                                    iv_back.visibility = View.VISIBLE
                                    tv_title.text = item.file_name
                                    pid_stack.add(0, item.file_id)
                                    pid_name_maps.put(item.file_id, item.file_name)
                                    pid = item.file_id
                                    File_path_yy = item.file_id
                                    File_path_yy_name = item.file_name
                                    Log.e(
                                        "yyy",
                                        "点击条目文件夹的file_id:" + File_path_yy + "---" + File_path_yy_name
                                    )
                                    /*   var id: String? = mMyDirsFragment.item_file_id_mydirsfragment
                                       var name: String? = mMyDirsFragment.item_file_name_mydirsfragment
                                       Log.e(
                                           "yyy",
                                           "获取分享页面点击的item内容:" + id + "--" + name
                                       )*/
                                    //Toastinfo("您进入了" + File_path_yy_name + "文件夹")
                                    GetFileList()
                                }
                            }
                            //我的页面 条目点击的内个图片
                            iv_right_icon.setOnClickListener {
                                ShowBottomFilesOperateDialog(item, isfile)
                            }
                        }
                        //不等于文件 和不等于复合文件就是文件夹
                        if (!isfile) { //文件夹
                            iv_icon.setImageResource(if (item.pshare == 0) R.drawable.icon_list_folder else R.drawable.icon_list_share_folder) //共享过-1 取消0
                            //不等于文件夹 和不等于复合文件就是  文件
                        } else { //文件
                            iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_unknown)
                            //如果说既不是文件夹也不是文件 就是复合文件
                        }
                    }
                }
            }
        })
        //</editor-fold >
        //<editor-fold  desc ="显示新建文件夹Dialog">
        iv_create_dir.setOnClickListener {
            ShowCreateNewDirDialog()
        }
        //</editor-fold >
        /*
        * 点击上传进行从根目录下进行提取文件尽心展示*/
        //<editor-fold  desc ="显示上传文件Dialog">
//        //点击状态   进入到SD
//        iv_show_up_file_type_dilaog.setOnClickListener {
////            if(UpFileTypeDialog == null){
////
////                UpFileTypeDialog = GetUploadFileTypeDialog(mContext!!,View.OnClickListener{
////
////                    UpFileTypeDialog?.dismiss()
////                }
////                    ,View.OnClickListener{
//
//            if (checkPermission(activity!!)!! == false) {
//                Toastinfo("没有sd卡读取权限")
//                return@setOnClickListener
//            }
//            //进入到SD卡页面
//            FilePickerManager
//                .from(this)
//                //.setTheme(getRandomTheme())  //主题
//                .enableSingleChoice()//是否启用单选模式
//                .forResult(FilePickerManager.REQUEST_CODE)
////                        var mintent =  Intent(Intent.ACTION_GET_CONTENT)
////                        mintent.addCategory(Intent.CATEGORY_OPENABLE)
////
////                       when(it.id){
////                           R.id.tv_image->{
////                               //TODO
////                               Toastinfo("图片")
////                               mintent.setType("image/*")
////                           }
////                           R.id.tv_doc->{
////                               //TODO
////                               Toastinfo("文档")
////                               mintent.setType("*/*")
////
////                           }
////                           R.id.tv_video->{
////                               Toastinfo("视频")
////                               mintent.setType("video/*")
////                           }
////                           R.id.tv_all->{
////                               Toastinfo("全部")
////                               mintent.setType("*/*")
////                           }
////                       }
////                        startActivityForResult(Intent.createChooser(mintent,"文件选择"),FILE_CHOOSER_RESULT_CODE)
////                        UpFileTypeDialog?.dismiss()
////                    })
////            }
////            UpFileTypeDialog?.show()
//        }
        //</editor-fold >
        //<editor-fold desc ="显示排序popwinod">
        iv_sort.setOnClickListener {
            if (SortPOPwiond == null) {
                SortPOPwiond = GetSortPOPwiond(
                    mContext!!,
                    RadioGroup.OnCheckedChangeListener { radioGroup, i ->
                        when (i) {
                            R.id.rb_sort_by_name -> {
                                Toastinfo("名称排序")
                                fileslist.sortBy { it.file_name }
                                adapter.notifyDataSetChanged()
                            }
                            R.id.rb_sort_by_time -> {
                                Toastinfo("时间排序")
//                            fileslist.sortBy { it.created_at }
                                fileslist.sortByDescending { it.created_at }
                                adapter.notifyDataSetChanged()
                            }
                        }
                        SortPOPwiond?.dismiss()
                    })
            }
            if (SortPOPwiond!!.isShowing) {
                SortPOPwiond!!.dismiss()
            } else {
                SortPOPwiond!!.showAsDropDown(iv_sort, 0, 20, Gravity.CENTER)
            }
        }
        //</editor-fold >
        //MyFilesFragment 页面的悬浮按钮的监听-
        image_yy_files.setOnClickListener {
            //获取pupopwindow 布局
            var root: View = LayoutInflater.from(context).inflate(R.layout.pop_view, null)
            //获取Pupopwindow对象
            var popupWindow: PopupWindow = PopupWindow(
                root, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true
            )
            popupWindow.setContentView(root)
            //获取contentView  对象
            var contentView: View = popupWindow.contentView
            //获取contentView 的监听
            contentView.setOnClickListener {
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
            }
            //获取弹窗里面的控件
            val tv1 = root.findViewById<View>(R.id.pop_computer) as TextView
            val tv2 = root.findViewById<View>(R.id.pop_financial) as TextView
            val tv3 = root.findViewById<View>(R.id.pop_manage) as TextView
            //相机监听
            tv1.setOnClickListener { //打开相机
                Toastinfo("您点击了相机    飞速赶往·····")
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, "")
                startActivityForResult(intent, 200)
            }
            //相册监听
            tv2.setOnClickListener { //打开相册
                Toastinfo("您点击了相册 飞速赶往·····")
                val intenta = Intent(Intent.ACTION_PICK)
                intenta.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intenta, 300)
            }
            //SD卡监听
            tv3.setOnClickListener { //SD
                Toastinfo("您点击了SD 飞速赶往·····")
                if (checkPermission(activity!!)!! == false) {
                    Toastinfo("没有sd卡读取权限")
                    return@setOnClickListener
                }
                //进入到SD卡页面
                FilePickerManager
                    .from(this)
                    //.setTheme(getRandomTheme())  //主题
                    .enableSingleChoice()//是否启用单选模式
                    .forResult(FilePickerManager.REQUEST_CODE)
            }
            //设置弹窗的宽高  都是自适应
            //设置弹窗的宽高  都是自适应
            popupWindow.width = ViewGroup.LayoutParams.MATCH_PARENT
            popupWindow.height = ViewGroup.LayoutParams.MATCH_PARENT
            //点击PoPup外
            popupWindow.isOutsideTouchable = true
            //显示PopupWindow
            popupWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0)
            popupWindow.showAsDropDown(root)
            //设置背景 操作完退出
            popupWindow.setBackgroundDrawable(ColorDrawable())
        }

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
            var text = (it as android.widget.TextView).text.toString()
            when (text) {
                "编辑" -> {
                    //TODO ui
                    it.text = "全选"
                    tv_title.text = "已选定${Is_Checked_Sum}个"
                    iv_back.visibility = View.VISIBLE
                    fileslist.forEach {
                        it.is_show_checked_view = true
                    }
                    adapter.notifyDataSetChanged()
                }
                "全选" -> {
                    it.text = "全不选"
                    fileslist.forEach {
                        it.is_checked = true
                    }
                    adapter.notifyDataSetChanged()
                }
                "全不选" -> {
                    it.text = "全选"
                    fileslist.forEach {
                        it.is_checked = false
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
//TODO 多选不显示
        tv_edit.visibility = View.INVISIBLE
        //</editor-fold >
        //<editor-fold  desc ="返回按钮 设置" >
        iv_back.visibility = View.GONE
        iv_back.setOnClickListener {
            if (tv_edit.text.equals("编辑") && pid_name_maps.containsValue(tv_title.text) && pid_stack.size > 1) { //选择文件夹显示返回
                pid_name_maps.remove(pid_stack[0])
                pid_stack.remove(pid_stack[0])
                pid = pid_stack[0]
                tv_title.text = pid_name_maps[pid]
                if (pid.equals(File_path_yy)) {  //root  替换成了File_path_yy
                    iv_back.visibility = View.INVISIBLE
                } else {
                    Toastinfo("pid不等于root:" + pid)
                }
                GetFileList()
            } else { //多选操作显示
                tv_title.text = pid_name_maps[pid_stack[0]]//"我的文件"
                tv_edit.text = "编辑"
                iv_back.visibility = View.INVISIBLE
                fileslist.forEach {
                    it.is_show_checked_view = false
                    it.is_checked = false
                }
                Is_Checked_Sum = 0
                adapter.notifyDataSetChanged()
            }
        }
        //</editor-fold >
        //<editor-fold  desc ="搜索按钮 设置" >
        iv_search.setOnClickListener {
            startActivityForResult(
                Intent(mContext, SearchFileActivity::class.java).putExtra(
                    "form",
                    "myfiles"
                ), SEARCH_CODE
            )
        }  //</editor-fold >
        //<editor-fold  desc ="刷新 设置" >
        tv_refresh.setOnClickListener { GetFileList() }
        //</editor-fold >
        tv_edit.visibility = View.INVISIBLE
    }

    //<editor-fold  desc ="新建文件夹 Dialog" >
    val SEARCH_CODE = 10009
    fun ShowCreateNewDirDialog() {
//        if(CreateNewDirDialog == null){
        CreateNewDirDialog = GetCreateNewDirDialog(mContext!!, View.OnClickListener {
            CreateNewDirDialog?.et_dir_name?.text = SetEt_Text("")
            CreateNewDirDialog?.checkbox_is_common?.isChecked = false
            CreateNewDirDialog?.dismiss()
        }
            , View.OnClickListener {
                if (CreateNewDirDialog!!.et_dir_name?.text == null || TextUtils.isEmpty(
                        CreateNewDirDialog!!.et_dir_name?.text
                    )
                ) {
                    Toastinfo("文件名不能为空")
                    return@OnClickListener
                }
                CreateNewDir(
                    CreateNewDirDialog!!.et_dir_name?.text.toString(),
                    CreateNewDirDialog!!.checkbox_is_common.isChecked,
                    pid,
                    this,
                    this
                )
                CreateNewDirDialog?.dismiss()
            })
//        }
        CreateNewDirDialog?.show()
    }

    //</editor-fold >
    //<editor-fold  desc ="底部操作 Dialog" >
//底部导航
    fun ShowBottomFilesOperateDialog(
        item: File_Bean,
        isfile: Boolean
    ) {
        ///Log.e("yangyu", "" + item)
        // var fid: String = item.file_id
        // Log.e("yangyu", "item_id:" + fid)
        BottomFilesOperateDialog = BottomSheetDialog(context!!)
        val contentview = LayoutInflater.from(context!!)
            .inflate(R.layout.dialog_bottom_files, null) as RecyclerView
//            contentview.layoutManager = LinearLayoutManager(context)
//            contentview.layoutManager = GridLayoutManager(context,4)
        Log.e(TAG, "isfile is ${isfile}")
        contentview.adapter = BottomFilesOperateAdapter(
            context,
            item,
            isfile,
            isroot_file = item.pid.equals("root"),
            ispshare_file = false
        )
        contentview.addItemDecoration(
            DividerItemDecoration(
                mContext,
                DividerItemDecoration.VERTICAL
            ).apply {
                setDrawable(mContext!!.getDrawable(R.drawable.bottom_dialog_line_bg))
            })
        (contentview.adapter as BottomFilesOperateAdapter).SetOnRecyclerItemClickListener(object :
            OnRecyclerItemClickListener {
            override fun onItemClick(
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                var iteminfo = (contentview.adapter as BottomFilesOperateAdapter).InfoList[position]
                val topdrawable =
                    context!!.resources.getDrawable((contentview.adapter as BottomFilesOperateAdapter).DrawableList[position])
                topdrawable.setBounds(0, 0, topdrawable.minimumWidth, topdrawable.minimumHeight)
                holder as BottomFilesOperateAdapter.ViewHolder
                holder.apply {
                    var tv_text = holder.itemView as TextView


                    tv_text.text = iteminfo
//                        if(position==0){
//                            tv_text.background=context!!.getDrawable(R.drawable.bottom_dialog_bg)
//                        }else{
//                        }
                    tv_text.setCompoundDrawablesWithIntrinsicBounds(topdrawable, null, null, null)
                    //条目监听事件
                    tv_text.setOnClickListener {
                        var tv = it as TextView
                        when (tv.text.toString()) {
                            "设置共享" -> {
                                Toastinfo("设置共享")
                                //TODO 文件夹分享页面
                                startActivityForResult(
                                    Intent(
                                        context,
                                        SetCommonFileActivity::class.java
                                    ).apply {
                                        putExtra("file", item)
                                    }, SET_PSHARE_CODE
                                )
                            }
                            "链接分享" -> {
                                Toastinfo("链接分享")
                                //TODO 跳转链接分享页面
                                mContext?.startActivity(
                                    Intent(
                                        context,
                                        LinkSharedActivity::class.java
                                    ).apply {
                                        putExtra("file", item)
                                    })
                            }
                            //现在要改动的问题是点击一条条目下载，在下载页面会重复进行下载问题
                            "下载" -> {
                                Toastinfo("下载")
                                //获取本类对象
                                var mainActivity = activity as MainActivity
                                //判断是否有sd卡权限   仅限在MainActivity里面
                                if (!checkPermission(mainActivity)!!) {
                                    Toastinfo("没有sd卡写入权限")
                                    return@setOnClickListener
                                }
                                //判断支不支持下载文件夹
                                if (item.is_dir == IS_DIR) {
                                    Toastinfo("暂不支持下载文件夹")
                                    return@setOnClickListener
                                }
                                //判断我们软件的最大内存 和需要下载的文件大小
                                if (!CheckFreeSpace(item.size * 2.5.toLong())) {
                                    Toastinfo("存储空间不足，无法完成下载操作")
                                    return@setOnClickListener
                                }
//                    context.startService(Intent(context,DaemonService::class.java).apply 1{
//                        action ="downFiles"
//                        putExtra("file",item)
//                    })
                                //判断服务是否开启成功
                                if (DaemonService.daemon != null) {
                                    /* //如果开启成功进行下载
                                     mainActivity.myBinder as DaemonService.MyBinder
                                     (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.GetFile(item)*/
                                    //如果开启成功进行下载
                                    val myBinder = mainActivity.myBinder
                                    myBinder?.GetDaemonService()?.GetFile(item)
                                } else {
                                    //如果说服务器没有正常启动成功将进行提示未启动服务
                                    Toastinfo("未启动服务")
                                }
                            }
                            "复制到" -> {
                                Toastinfo("复制到")
                                startActivityForResult(
                                    Intent(
                                        context,
                                        ChooseDestDirActivity::class.java
                                    ).apply {
                                        putExtra("file", item)
                                        putExtra("type", ChooseDestDirActivity.COPY)
                                    }, ChooseDestDirActivity.COPY
                                )
                            }
                            "移动到" -> {
                                Toastinfo("移动到")
                                startActivityForResult(
                                    Intent(
                                        context,
                                        ChooseDestDirActivity::class.java
                                    ).apply {
                                        putExtra("file", item) //item为File_Bean的实体类
                                        putExtra(
                                            "type", ChooseDestDirActivity.MOVE
                                        )
                                    }, ChooseDestDirActivity.MOVE  //1  移动
                                )
                                Log.e("yy", item.toString())
                            }
                            "重命名" -> {
                                Toastinfo("重命名")
                                ShowRenameDialog(item)
                            }
                            "删除" -> {
                                Toastinfo("删除")
                                ShowFileDeleteTipsDialog(item.file_id)
                            }
                            "详细信息" -> {
                                Toastinfo("详细信息")
                                mContext?.startActivity(
                                    Intent(
                                        context,
                                        FileInfoActivity::class.java
                                    ).apply {
                                        putExtra("file", item)
                                    })
                            }
                        }
                        BottomFilesOperateDialog?.dismiss()
                    }
                }
            }
        })
        BottomFilesOperateDialog?.setContentView(contentview).apply {
            BottomFilesOperateDialog?.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                ?.setBackgroundResource(android.R.color.transparent)
        }
        BottomFilesOperateDialog?.show()
    }
//</editor-fold >


    //<editor-fold  desc ="重命名 Dialog" >
    private fun ShowRenameDialog(item: File_Bean) {
        ReanmeDialog = Dialog(mContext!!).apply {
            var contentview =
                LayoutInflater.from(mContext).inflate(R.layout.dialog_file_rename, null)
            contentview.apply {

                et_dir_name.text = SetEt_Text("${item.file_name}")
                if (et_dir_name.text.toString().equals(item.file_name)) {
                    tv_commit.isEnabled = false
                    tv_commit.setTextColor(Color.parseColor("#999999"))
                } else {
                    tv_commit.isEnabled = true
                    tv_commit.setTextColor(Color.parseColor("#2864DE"))
                }
                et_dir_name.addTextChangedListener {

                    if (et_dir_name.text.toString().equals(item.file_name)) {
                        tv_commit.isEnabled = false
                        tv_commit.setTextColor(Color.parseColor("#999999"))
                    } else {
                        tv_commit.isEnabled = true
                        tv_commit.setTextColor(Color.parseColor("#2864DE"))
                    }
                }
                tv_cancle.setOnClickListener {
                    ReanmeDialog?.dismiss()
                }
                tv_commit.setOnClickListener {
                    NetRequest("${URL_FILE_RENAME}", NET_POST, HashMap<String, Any>().apply {
                        put("user_id", "${USER_ID}")
                        put("file_id", "${item.file_id}")
                        put("file_name", "${et_dir_name.text}")

                    }, this, this@MyFilesFragment)
                    ReanmeDialog?.dismiss()
                }
            }
            setContentView(contentview)
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
        }
        ReanmeDialog?.show()
    }
//</editor-fold >

    //<editor-fold  desc ="删除提示 Dialog" >
    fun ShowFileDeleteTipsDialog(file_id: String) {
        FileDeleteTipsDialog = GetFileDeleteTipsDialog(
            mContext!!,
            View.OnClickListener { FileDeleteTipsDialog?.dismiss() },
            View.OnClickListener {
                DeleteFile(file_id, this, this@MyFilesFragment)
                FileDeleteTipsDialog?.dismiss()
            })
        FileDeleteTipsDialog?.show()
    }
//</editor-fold >

    override fun initData() {
        pid_stack = ArrayList<String>().apply {
            add(0, "root")
        }
        pid_name_maps = HashMap()
        pid_name_maps.put("root", "我的文件")
        pid = pid_stack[0]

        GetFileList()

    }


    //<editor-fold  desc =" 获取文件列表" >
    fun GetFileList() {
        GetFilesListForNet(
            URL_LIST_FILES + "${USER_ID}/status/${IS_UNCOMMON_DIR}/p/${pid}/dir/${ALL_FILE}",
            this,
            this
        )
    }
//</editor-fold >

    //MyFilesFragment 的xml文件
    override fun GetRootViewID() = com.ucas.cloudenterprise.R.layout.my_files_fragment

    companion object {
        private var instance: MyFilesFragment? = null
        fun getInstance(param1: Boolean, param2: String?): MyFilesFragment {
//            if (instance == null) {
//                synchronized(MyFilesFragment::class.java) {
//                    if (instance == null) {
            instance = MyFilesFragment().apply {
                this.arguments = Bundle().apply {
                    putBoolean("param1", param1)
                    putString("param2", param2)
                }
            }
            return instance!!
        }
    }

    //<editor-fold desc=" 网络请求回调  ">
    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        data?.apply {
            if (JSONObject(data).getInt("code") == 5000 && !((URL_LIST_FILES + "${USER_ID}/status/${IS_UNCOMMON_DIR}/p/${pid}/dir/${ALL_FILE}").equals(
                    request?.url
                ))
            ) {
                Toastinfo("${(JSONObject(data).getString("message"))}")
                return
            }
        }
        when (request?.url) {
            URL_LIST_FILES + "${USER_ID}/status/${IS_UNCOMMON_DIR}/p/${pid}/dir/${ALL_FILE}" -> {
                Log.e(TAG, "request.method.name is ${request.method.name}")
                when (request.method.name) {
                    HttpMethod.GET.name -> {
                        //获取我的文件列表
                        if (JSONObject(data).isNull("data")) {
                            ll_empty.visibility = View.VISIBLE
                            swipeRefresh.visibility = View.INVISIBLE
                            tv_edit.visibility = View.INVISIBLE
                            return
                        } else {
                            ll_empty.visibility = View.INVISIBLE
                            swipeRefresh.visibility = View.VISIBLE
                        }
                        //获取我的文件列表
                        Toastinfo("获取文件列表成功")
                        fileslist.clear()
                        /* if(JSONObject(data) is JSONObject){
                             true
                         }*/
                        // Toastinfo("即将执行把分享过来的数据进行添加到列表")
                        val toString = JSONObject(data).getJSONArray("data").toString()

                        fileslist.addAll(
                            Gson().fromJson(
                                toString, object : TypeToken<List<File_Bean>>() {}.type
                            ) as ArrayList<File_Bean>
                        )
                        Log.e("我的文件  列表信息", toString)
                        adapter?.notifyDataSetChanged()
                        //startActivity(Intent.setClass(context,TransferlistItemFragment::class.java))
                        Toastinfo("刷新完")
                        //   Toastinfo(toString)
//                      TODO  编辑按钮显示  tv_edit.visibility =  if (!fileslist.isEmpty()) View.VISIBLE  else View.INVISIBLE
                    }
                }
            }
            URL_ADD_File -> {
                if (JSONObject(data).isNull("data") || JSONObject(data).getInt("code") != 200) {
                    Toastinfo(JSONObject(data).getString("message"))
                    return
                }

                Toastinfo("添加文件列表成功")
                //刷新列表
                //TODO USER_ID ->PID
                GetFileList()
            }
            URL_DELETE_FILE -> {

                Toastinfo("删除文件成功")
                //TODO USER_ID ->PID
                GetFileList()
            }
            URL_FILE_RENAME -> {
                Toastinfo("文件重命名成功")
                GetFileList()

            }
            URL_FILE_COPY -> {
                Toastinfo("文件复制成功")
                GetFileList()

            }
            URL_FILE_MOV -> {
                Toastinfo("文件移动成功")
                GetFileList()
            }
        }
    }


    //</editor-fold>  回调方法
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SET_PSHARE_CODE && resultCode == RESULT_OK) {
            GetFileList()
        }
        if (data != null) {
            when (requestCode) {
                FilePickerManager.REQUEST_CODE -> { //选择文件上传文件
                    if (FilePickerManager.obtainData().isEmpty()) {
                        Toastinfo("没有选择文件")
                        return
                    }
                    Log.e("ok", "file  path" + FilePickerManager.obtainData()[0])
                    //调用成功后会进行返回到  调用下载的内个方法   在进行从外界分享图片的时候分享是走不到这里的
                    CheckFileIsExists(FilePickerManager.obtainData()[0]) //检查文件是否存在
                }
                //文件复制
                ChooseDestDirActivity.COPY -> {
                    var file_id = data.getStringExtra("file_id")
                    var pid = data.getStringExtra("pid")
                    var params = HashMap<String, Any>().apply {
                        put("user_id", "${USER_ID}")
                        put("file_id", file_id)
                        put("pid", pid)
                    }
                    NetRequest(URL_FILE_COPY, NET_POST, params, this, this)
                }
                //文件移动
                ChooseDestDirActivity.MOVE -> {
                    var file_id = data.getStringExtra("file_id")
                    var pid = data.getStringExtra("pid")
                    var params = HashMap<String, Any>().apply {
                        put("user_id", "${USER_ID}")
                        put("file_id", file_id)
                        put("pid", pid)
                    }
                    NetRequest(URL_FILE_MOV, NET_POST, params, this, this)
                }
                //新建文件夹
                SEARCH_CODE -> {
                    //TODO
                    if (resultCode == RESULT_OK && data != null) {
                        var item = data.getSerializableExtra("destfold") as File_Bean
                        iv_back.visibility = View.VISIBLE
                        tv_title.text = item.file_name
                        pid_stack.add(0, item.file_id)
                        pid_name_maps.put(item.file_id, item.file_name)
                        pid = item.file_id
                        GetFileList()
                    }
                }

            }
        }
        //回调  拿到相册  相机照片  然后走单张分享方法   直接存方到当前的一个目录下
        if (requestCode == 200) {   //200 等于相机
            if (requestCode == 200 && resultCode == RESULT_OK) {
                val bitmap = data!!.getParcelableExtra<Bitmap>("data")
                //将获取的bitmap转换成Uri
                val parse = Uri.parse(
                    MediaStore.Images.Media.insertImage(
                        context?.getContentResolver(), bitmap, null, null
                    )
                )
                val path1: String? = null
                val proj1 = arrayOf(MediaStore.Images.Media.DATA)
                val cursor1: Cursor = activity!!.managedQuery(parse, proj1, null, null, null)
                if (cursor1 != null && cursor1.moveToFirst()) {
                    var column_index1 = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    var stra1 = cursor1.getString(column_index1)
                    Toastinfo("即将进行上传-MyFilesFrangment——相机")
                    CheckFileIsExists_Xiangji_Xiangce(stra1)
                }
            }
            Toastinfo("我是相机")
        } else if (requestCode == 300) { //300 等于相册
            Toastinfo("我是相册")
            val data1 = data!!.data
            val path: String? = null
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            // 获取选中图片的路径
            // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
            val cursor: Cursor = activity!!.managedQuery(data1, proj, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                var column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                var stra = cursor.getString(column_index)
                CheckFileIsExists_Xiangji_Xiangce(stra)
                // 第二个参数是想要获取的数据
                Log.e("yyy", "相册返回的字符串:$stra")
            }
        } else {
            Toastinfo("回调请求码为:" + requestCode)
            Log.e("yyy", "回调请求码为:" + requestCode)
        }
    }

    //<editor-fold desc="检查文件是否已存在">
    //现在  我要断点执行从SD卡进行分享数据   开始
    //现在的流程是正确的一个流程   成功可以进行上传到服务器
    //现在在进行通过从外界进行分享图片的时候一个流程
    //val picPath: ArrayList<String> = java.util.ArrayList()
    //相机  相册
    fun CheckFileIsExists_Xiangji_Xiangce(file_path: String) {
        Log.e("yyy", "相机拍照后的照片发送到了CheckFileIsExists_Xiangji_Xiangce")
        Log.e("yyy", "传过来的路径：" + file_path)
        val destfile = File(file_path)   //把传过来的字符串进行转换为文件类型
        val destfile_length = destfile.length() * 1.0 / (1024 * 1024) //判断文件的长度
        Log.e("ok", "当前文件大小: " + destfile_length)  //打印文件大小
        if (destfile_length >= (4 * 1024)) {
            Toastinfo("该文件超过4G，不支持app传输")
            return
        }
        if (!CheckFreeSpace(destfile.length() * 1.5.toLong())) {
            Toastinfo("存储空间不足，无法完成上传操作")
            return
        }
        val apply = HashMap<String, Any>().apply {
            put("user_id", USER_ID)
            put("file_size", destfile.length())
        }
//要想上传成功必须要走NetRequest 这个方法
//------------------------------------------------------------------------------------------
        NetRequest(URL_FILE_UPLOADABLE, NET_POST, apply, this, object : OnNetCallback {
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                Log.e("yy", "我是OnNetPostSucces")
                if (VerifyUtils.VerifyResponseData(data)) {
                    var mainActivity = activity as MainActivity
                    if (!(File_path_yy == null)) {
                        Toastinfo("即将进行上传")
                        (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()
                            ?.AddFile(file_path, File_path_yy)//root  替换成了 文件夹的file_id
                        //将从MydirsFragment 获取的file_id  进行上传
                        Log.e(
                            "yyy",
                            "要进行分享的文件夹id1（File_path_yy）:" + File_path_yy + "--" + File_path_yy_name
                        )
                        //  Log.e("yyy","要进行分享的文件夹id1（id-name）:" + id + "--" +name)
                    } else {
                        (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()
                            ?.AddFile(file_path, pid)//文件夹的file_id  替换成了 root
                        Log.e("yyy", "要进行分享的文件夹id1（pid）:" + pid)
                    }
                    Log.e("yyy", "判断走没走相机上传方法1")
                } else {
                    Log.e("yy", "VerifyUtils.VerifyResponseData(data)  不符合需求")
                    Toastinfo(JSONObject(data).getString("message"))
                }
            }
        })
        var Memory = getMemory()
        Log.e("ok", "当前内存: $Memory")
    }

    //单个文件调用
    fun CheckFileIsExists(file_path: String) {
        Log.e("yyy", "相机拍照后的照片发送到了ChekFileISEXists")
        Log.e("yyy", "传过来的路径：" + file_path)
        val destfile = File(file_path)   //把传过来的字符串进行转换为文件类型
        val destfile_length = destfile.length() * 1.0 / (1024 * 1024) //判断文件的长度
        Log.e("ok", "当前文件大小: " + destfile_length)  //打印文件大小
        if (destfile_length >= (4 * 1024)) {
            Toastinfo("该文件超过4G，不支持app传输")
            return
        }
        if (!CheckFreeSpace(destfile.length() * 1.5.toLong())) {
            Toastinfo("存储空间不足，无法完成上传操作")
            return
        }
        val apply = HashMap<String, Any>().apply {
            put("user_id", USER_ID)
            put("file_size", destfile.length())
        }
//要想上传成功必须要走NetRequest 这个方法
//------------------------------------------------------------------------------------------
        NetRequest(URL_FILE_UPLOADABLE, NET_POST, apply, this, object : OnNetCallback {
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                Log.e("yy", "我是OnNetPostSucces")
                if (VerifyUtils.VerifyResponseData(data)) {
                    var mainActivity = activity as MainActivity
                    //目前指定目录的行为时  获取的pid 依旧是root  只要把路径替换掉（文件夹的路径）
                    //目前效果是 分享图片还是只能分享到根目录，但是可以创建文件夹 或者是指定某个文件夹，然后图片
                    //是因为没有给他指定文件夹位置  默认的还是Root
                    //当前效果 因为会点击多次-item_file_id的值会变很多次每次数据都不一样，而用来上传的路径只有一次.
                    /*       if (!item_file_id.equals("root")) {
                               (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()
                               ?.AddFile(file_path, item_file_id)//root  替换成了 文件夹的file_id
                           Log.e("yangyu", "item_path2:" + item_file_id)
                       } else {}*/
                    //pid= item_file_id.toString()
                    if (!(File_path_yy == null)) {
                        (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()
                            ?.AddFile(file_path, File_path_yy)//root  替换成了 文件夹的file_id
                        //将从MydirsFragment 获取的file_id  进行上传
                        Log.e(
                            "yyy",
                            "要进行分享的文件夹id1（File_path_yy）:" + File_path_yy + "--" + File_path_yy_name
                        )
                        //  Log.e("yyy","要进行分享的文件夹id1（id-name）:" + id + "--" +name)
                    } else {
                        (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()
                            ?.AddFile(file_path, pid)//文件夹的file_id  替换成了 root
                        Log.e("yyy", "要进行分享的文件夹id1（pid）:" + pid)
                    }
                    Log.e("yyy", "File_path_yy:" + File_path_yy)
                    Log.e("yyy", "pid:" + pid)
                    //Log.e("yy", "获取文件夹路径：" + chooseDestDirActivity?.tv_path?.text)
                } else {
                    Log.e("yy", "VerifyUtils.VerifyResponseData(data)  不符合需求")
                    Toastinfo(JSONObject(data).getString("message"))
                }
            }
        })
        var Memory = getMemory()
        Log.e("ok", "当前内存: $Memory")
    }

    //多个文件调用
    fun CheckFileIsExists_list(file_path: ArrayList<String>) {
        for (i in 0 until file_path.size) {
            Log.e("aaa", "Main传到Files的集合数量:" + file_path.size)
            get_path = file_path[i]
            Log.e("ayy", "传过来第" + i + "张图片:" + get_path)
        }
        //file_path.clear()
        Log.e("aaa", "Files中的集合数量:" + file_path.size)
        //  Toastinfo("传过来的路径" + file_path)
        Log.e("yy", "1传过来的路径：" + file_path)
        val destfile = File(get_path)   //把传过来的字符串进行转换为文件类型
        val destfile_length = destfile.length() * 1.0 / (1024 * 1024) //判断文件的长度
        Log.e("yy", "1当前文件大小: " + destfile_length)  //打印文件大小
        if (destfile_length >= (4 * 1024)) {
            Toastinfo("该文件超过4G，不支持app传输")
            return
        }
        if (!CheckFreeSpace(destfile.length() * 1.5.toLong())) {
            Toastinfo("存储空间不足，无法完成上传操作")
            return
        }
        val apply = HashMap<String, Any>().apply {
            put("user_id", USER_ID)
            put("file_size", destfile.length())
        }
//要想上传成功必须要走NetRequest 这个方法
//------------------------------------------------------------------------------------------
        NetRequest(URL_FILE_UPLOADABLE, NET_POST, apply, this, object : OnNetCallback {
            override fun OnNetPostSucces(
                request: Request<String, out Request<Any, Request<*, *>>>?,
                data: String
            ) {
                Log.e("yy", "1我是OnNetPostSucces")
                if (VerifyUtils.VerifyResponseData(data)) {
                    var mainActivity = activity as MainActivity
                    //成功的时候就会走AddFile
                    //目前是分享几个 可以上传几个  因为上面有一个循环  这里又加了一个循环
                    for (i in 0 until file_path.size) {
                        val get_path = file_path[i]
                        Log.e("aaa", "第" + i + "个路径:" + get_path)
                        if (!(File_path_yy == null)) {
                            (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()
                                ?.AddFile(file_path[i], File_path_yy)//root  替换成了 文件夹的file_id
                            //将从MydirsFragment 获取的file_id  进行上传
                            Log.e(
                                "yyy",
                                "要进行分享的文件夹id1（File_path_yy）:" + File_path_yy + "--" + File_path_yy_name
                            )
                            Log.e("aaa", "看看是否上传(File_path_yy):" + file_path[i])
                            //  Log.e("yyy","要进行分享的文件夹id1（id-name）:" + id + "--" +name)
                        }else {
                            (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()
                                ?.AddFile(file_path[i], pid)//文件夹的file_id  替换成了 root
                            //Log.e("aaa", "集合清空前:"+file_path.size)
                            //  file_path.clear()
                            // Log.e("aaa", "集合清空后:"+file_path.size)
                            Log.e("yyy", "要进行分享的文件夹id1（pid）:" + pid)
                            Log.e("aaa", "看看是否上传(pid):" + file_path[i])
                        }
                        Log.e("aaa","")
                    }
                    /*  file_path.clear()
                      Log.e("aaa","Files的集合数量（清空后）:"+file_path.size)*/
                    Log.e("yy", "1我通过服务Service进行把拿过来的路径存入AddFile")
                } else {
                    Log.e("yy", "1VerifyUtils.VerifyResponseData(data)  不符合需求")
                    Toastinfo(JSONObject(data).getString("message"))
                }
            }
        })
        var Memory = getMemory()
        Log.e("ok", "当前内存: $Memory")
        Log.e("aaa", "MyFiles传过来的集合数量：" + file_path.size)
        /* Log.e("aaa", "MyFiles的集合数量：" + file_path.size)*/
    }

    //</editor-fold>
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        GetFileList()
/* Do something */
    };
}



