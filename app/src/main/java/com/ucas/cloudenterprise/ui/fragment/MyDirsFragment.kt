package com.ucas.cloudenterprise.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.FilesAdapter
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.ui.ChooseDestDirActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import kotlinx.android.synthetic.main.activity_choose_dest_dir.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.swiperefreshlayout.*
import org.json.JSONObject

/**
@author simpler
@create 2020年01月10日  14:31
 */
class MyDirsFragment(var pid:String) : BaseFragment(),BaseActivity.OnNetCallback {
    lateinit var adapter:FilesAdapter
    lateinit var fileslist:ArrayList<File_Bean>
    var pid_src ="root"
    var pathname=""
    lateinit var pid_stack : ArrayList<String>

    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {


             var showtype= (activity as ChooseDestDirActivity).viewpager_content.currentItem


        fileslist.clear()


        if(JSONObject(data).isNull("data")){
            ll_empty.visibility = View.VISIBLE
            swipeRefresh.visibility = View.INVISIBLE

        }else{
            ll_empty.visibility = View.INVISIBLE
            swipeRefresh.visibility = View.VISIBLE
            Toastinfo("获取文件列表成功")

            fileslist.addAll((Gson().fromJson<List<File_Bean>>(JSONObject(data).getJSONArray("data").toString(),object : TypeToken<List<File_Bean>>(){}.type) as ArrayList<File_Bean>).filter { it.is_dir== IS_DIR })
            if(!fileslist.isEmpty()){
                var destitem:File_Bean?=null
                for (item in fileslist){
                    if(item.file_id.equals((activity as ChooseDestDirActivity).file_item!!.file_id)){
                        destitem=item
                    }
                }
                if(destitem!=null){
                    fileslist.remove(destitem)
                }
                if(!pid.equals("root")){
//                    var path =fileslist[0].path
//                    var last= path.indexOfLast {  it.equals('/') }
//
//                       if(last!=-1){
//                           (activity as ChooseDestDirActivity).tv_path.text="已选：${path.substring(0,last)}"
//                       }
//                    (activity as ChooseDestDirActivity).tv_path.apply {
//                        text =text.toString()+"/"+pathname
//                    }

                }else{

                    (activity as ChooseDestDirActivity).tv_path.text="已选： 我的文件"
                }
                adapter?.notifyDataSetChanged()
            }

        }

        //获取我的文件列表

//        if(showtype==0){
//
//             (activity as ChooseDestDirActivity).tv_dest_dir_commit.apply {
//                 if(fileslist.isEmpty()){
//                     setBackgroundColor(Color.GRAY)
//                     isEnabled = false
//                 }else{
//                     setBackgroundColor(resources.getColor(R.color.app_color))
//                     isEnabled = true
//                 }
//             }
//
//
//
//        }


    }







    override fun initView() {
        //<editor-fold desc=" 设置files RecyclerView  ">
        fileslist =ArrayList()

        adapter = FilesAdapter(mContext,fileslist)
        rc_myfiles.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false)
        rc_myfiles.adapter = adapter
        rc_myfiles.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        adapter.SetOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                var item =fileslist[position]

                    holder as FilesAdapter.ViewHolder
                    holder.apply {
                        iv_icon.setImageResource( if(item.is_dir == IS_DIR) (if(item.pshare==0)R.drawable.icon_list_folder else R.drawable.icon_list_share_folder) else R.drawable.icon_list_unknown)
                        tv_file_name.text = item.file_name
                        tv_file_create_time.text = item.created_at
                        rl_file_item_root.setOnClickListener {
                            pid_stack.add(0,item.file_id)
                            pid = item.file_id
                            if(!pid.equals("root")){
                                (activity as ChooseDestDirActivity).tv_path.apply {
                                    if(text.equals("已选： 我的文件")){
                                        text="已选： /${item.file_name}"
                                    }else{
                                        text="${text}/${item.file_name}"
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

    }

    override fun initData() {
        pid_src = pid
        pid_stack = ArrayList<String>().apply {
            add(0,pid)
        }

        pid = pid_stack[0]
        Log.e("ok","pid is  ${pid}")


    }

    var hasloaddata=false
    override fun onResume() {
        super.onResume()
        Log.e("ok","mydir")
        if(!hasloaddata){
            GetFileList()
            hasloaddata = true
        }
    }

    override fun GetRootViewID()= R.layout.mydirs_fragment



}