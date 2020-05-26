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
class ShareDirsFragment( var pid:String) : BaseFragment(),BaseActivity.OnNetCallback {
    lateinit var adapter:FilesAdapter
    lateinit var fileslist:ArrayList<File_Bean>
    var pid_src ="root"
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
            //获取我的文件列表
            Toastinfo("获取文件列表成功")

            fileslist.addAll((Gson().fromJson<List<File_Bean>>(JSONObject(data).getJSONArray("data").toString(),object : TypeToken<List<File_Bean>>(){}.type) as ArrayList<File_Bean>).filter { it.is_dir== IS_DIR })
            adapter?.notifyDataSetChanged()

        }

//        if(showtype==1){
//            (activity as ChooseDestDirActivity).tv_dest_dir_commit.apply {
//                if(fileslist.isEmpty()){
//                    setBackgroundColor(Color.GRAY)
//                    isEnabled = false
//                }else{
//                    setBackgroundColor(resources.getColor(R.color.app_color))
//                    isEnabled = true
//                }
//            }
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
                        tv_file_name.text = item.file_name
                        tv_file_create_time.text = item.created_at
                        rl_file_item_root.setOnClickListener {
                            pid_stack.add(0,item.file_id)
                            pid = item.file_id
                            GetFileList()
                        }
                    }



                }


        })
        //</editor-fold >




        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            GetFileList()
            swipeRefresh.isRefreshing = false
        }


    }

     fun GetFileList() {

            GetFilesListForNet(URL_GET_FILE_JURIS_LIST +"${USER_ID}/p/${pid}",this,this)

    }

    override fun initData() {
        pid_src = pid
        pid_stack = ArrayList<String>().apply {
            add(0,pid)
        }

        pid = pid_stack[0]
        Log.e("ok","pid is  ${pid}")


    }

    override fun GetRootViewID()= R.layout.mydirs_fragment
    var hasloaddata=false
    override fun onResume() {
        super.onResume()
        Log.e("ok","sharedir")
        if(!hasloaddata){
            GetFileList()
            hasloaddata = true
        }
    }


}