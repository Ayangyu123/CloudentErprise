package com.ucas.cloudenterprise.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.FilesAdapter
import com.ucas.cloudenterprise.app.IS_DIR
import com.ucas.cloudenterprise.app.IS_UNCOMMON_DIR
import com.ucas.cloudenterprise.app.URL_LIST_FILES
import com.ucas.cloudenterprise.app.USER_ID
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.ui.ChooseDestDirActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.activity_choose_dest_dir.*
import kotlinx.android.synthetic.main.swiperefreshlayout.*
import org.json.JSONObject


/**
@author simpler
@create 2020年01月10日  14:31
 */
class MyDirsFragment(var pid: String) : BaseFragment(), BaseActivity.OnNetCallback {
    lateinit var adapter: FilesAdapter
    lateinit var mMyFilesFragment: MyFilesFragment
    lateinit var fileslist: ArrayList<File_Bean>
    var pid_src = "root"
    var pathname = ""
    var item_file_id_mydirsfragment: String? = null
    var item_file_name_mydirsfragment: String? = null
    lateinit var pid_stack: ArrayList<String>
    lateinit var pathyu_path: String
    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        var showtype = (activity as ChooseDestDirActivity).viewpager_content.currentItem
        fileslist.clear()
        if (JSONObject(data).isNull("data")) {
            ll_empty.visibility = View.VISIBLE
            swipeRefresh.visibility = View.INVISIBLE
            //Log.e("222", "ll_empty.visibility    1:"+ll_empty.visibility)
            // Log.e("222", "swipeRefresh.visibility1:"+swipeRefresh.visibility)
        } else {
            ll_empty.visibility = View.INVISIBLE
            swipeRefresh.visibility = View.VISIBLE
            //   Log.e("222", "ll_empty.visibility    2:"+ll_empty.visibility)
            //    Log.e("222", "swipeRefresh.visibility2:"+swipeRefresh.visibility)
            Toastinfo("获取文件列表成功")
            fileslist.addAll((Gson().fromJson<List<File_Bean>>(
                JSONObject(data).getJSONArray("data").toString(),
                object : TypeToken<List<File_Bean>>() {}.type
            ) as ArrayList<File_Bean>).filter { it.is_dir == IS_DIR })
            //   Log.e("222", "fileslist:"+fileslist)


            //移动文件到指定目录  的期中一部分
            if (!fileslist.isEmpty()) {  //判断集合不为空
                var destitem: File_Bean? = null
                for (item in fileslist) {
                    if (item.file_id.equals((activity as ChooseDestDirActivity).file_item!!.file_id)) {
                        destitem = item
                    }
                    //Log.e("222", "destitem:" +destitem)
                    // Log.e("222", "打印条目:" + item.file_id + "----" + item.file_name)
                }
                // Log.e("222", "点击了:" + destitem!!.file_id + "----" + destitem!!.file_name)
                if (destitem != null) {
                    fileslist.remove(destitem)
                }
                //判断路径
                if (!pid.equals("root")) {  //如果选择的不是默认的root  就直接把分享进来的文件进行存储到指定文件路径
                    var path = fileslist[0].path
                    //   Log.e("222", "fileslist[0]为:" + fileslist[0])
                    var last = path.indexOfLast { it.equals('/') }
                    pathyu_path = path.substring(0, last)
                    if (last != -1) {
                        (activity as ChooseDestDirActivity).tv_path.text = "已选：${pathyu_path}"
                    }
                    //    Log.e("222", "选中的路径为:" + path)
                    (activity as ChooseDestDirActivity).tv_path.apply {
                        text = text.toString() + "/" + pathname
                        //  Log.e("222", "选中的路径为:" + text.toString() + "/" + pathname)
                    }

                } else {
                    (activity as ChooseDestDirActivity).tv_path.text = "已选： 我的文件"
                }
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun initView() {
        //<editor-fold desc=" 设置files RecyclerView  ">
        fileslist = ArrayList()
        adapter = FilesAdapter(mContext, fileslist)
        rc_myfiles.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rc_myfiles.adapter = adapter
//        rc_myfiles.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        adapter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                var item = fileslist[position]
                Log.e("yangyu", "item:" + item)
                Log.e("yangyu", "MyDirsFragment获取到的pid值:" + pid)
                Log.e("yangyu", "MyDirsFragment获取到的file_id值:" + item.file_id)
                holder as FilesAdapter.ViewHolder
                holder.apply {
                    iv_icon.setImageResource(
                        if (item.is_dir == IS_DIR) (if (item.pshare == 0) R.drawable.icon_list_folder
                        else R.drawable.icon_list_share_folder) else R.drawable.icon_list_unknown
                    )
                    Log.e("222", "打印条目:" + item.file_id + "----" + item.file_name)
                    tv_file_name.text = item.file_name
                    tv_file_create_time.text = item.created_at
                    rl_file_item_root.setOnClickListener {
                        //准备两个字符串然后  把点击的id 和name 传过去 （传到MyFileFragment作为上传file_id）
                        item_file_id_mydirsfragment = item.file_id
                        item_file_name_mydirsfragment = item.file_name

                        Log.e(
                            "yyy",
                            "我点击的条目:" + item_file_id_mydirsfragment + "----" + item_file_name_mydirsfragment
                        )

                        Log.e("222", "整个数据列表:" + item)
                        pid_stack.add(0, item.file_id)
                        pid = item.file_id
                        Log.e("yangyu", "MyDirsFragment获取到Pid的值：" + pid)
                        if (!pid.equals("root")) {
                            (activity as ChooseDestDirActivity).tv_path.apply {
                                if (text.equals("已选： 我的文件")) {
                                    text = "已选： /${item.file_name}"
                                } else {
                                    text = "${text}/${item.file_name}"
                                }
                            }
                        }
//                            else{
//                                (activity as ChooseDestDirActivity).tv_path.text="已选： 我的文件"
//                            }
                        GetFileList()
                    }
                }
            }
        })
        //</editor-fold >


        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
//            fileslist.clear()
            GetFileList()
            swipeRefresh.isRefreshing = false
        }
    }

    fun GetFileList() {
        GetFilesListForNet(
            URL_LIST_FILES + "$USER_ID/status/${IS_UNCOMMON_DIR}/p/${pid}/dir/$IS_DIR",
            this,
            this
        )
        Log.e("yangyu1", "MydirsFragment新建文件的的pid：" + pid)
    }

    override fun initData() {
        pid_src = pid
        pid_stack = ArrayList<String>().apply {
            add(0, pid)
        }

        pid = pid_stack[0]
        Log.e("ok", "pid is  ${pid}")


    }

    var hasloaddata = false
    override fun onResume() {
        super.onResume()
        Log.e("ok", "mydir")
        if (!hasloaddata) {
            GetFileList()
            hasloaddata = true
        }
    }

    override fun GetRootViewID() = R.layout.mydirs_fragment
}
