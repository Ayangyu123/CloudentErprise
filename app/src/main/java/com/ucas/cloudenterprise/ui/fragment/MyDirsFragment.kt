package com.ucas.cloudenterprise.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.swiperefreshlayout.*

/**
@author simpler
@create 2020年01月10日  14:31
 */
class MyDirsFragment: BaseFragment(),BaseActivity.OnNetCallback {
    override fun OnNetPostSucces(
        request: Request<String, out Request<Any, Request<*, *>>>?,
        data: String
    ) {
        when(request?.url){
         "$URL_LIST_FILES$USER_ID/status/$IS_UNCOMMON_DIR/p/${pid}/dir/$IS_DIR"->{

         }

        }
    }

    lateinit var adapter:FilesAdapter
       lateinit var fileslist:ArrayList<File_Bean>
    var  pid ="root"

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

                            }

                        }else{
                            iv_right_icon.visibility =View.VISIBLE
                            checkbox_is_checked.visibility = View.GONE
                            iv_right_icon.setOnClickListener{
//                                ShowBottomFilesOperateDialog(item,isfile)
                            }
                        }




                        if(!isfile){
                            iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_folder)
                        }

                    }



                }

            }
        })
        //</editor-fold >



        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            fileslist.clear()
            GetFileList()
            swipeRefresh.isRefreshing = false
        }

    }

    private fun GetFileList() {
        GetFilesListForNet(URL_LIST_FILES +"$USER_ID/status/$IS_UNCOMMON_DIR/p/${pid}/dir/$IS_DIR",this,this)
    }

    override fun initData() {


    }

    override fun GetRootViewID()= R.layout.mydirs_fragment
    companion object{
         private var instance: MyDirsFragment? = null

        fun getInstance( param1:Boolean,  param2:String?): MyDirsFragment {
            if (instance == null) {
                synchronized(MyDirsFragment::class.java) {
                    if (instance == null) {
                        instance = MyDirsFragment().apply {
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

}