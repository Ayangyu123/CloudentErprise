package com.ucas.cloudenterprise.ui.fragment

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.adapter.CompletedAdapter
import com.ucas.cloudenterprise.adapter.LoadingFileAdapter
import com.ucas.cloudenterprise.app.MyApplication
import com.ucas.cloudenterprise.base.BaseFragment
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.model.LoadIngStatus
import com.ucas.cloudenterprise.model.LoadingFile
import com.ucas.cloudenterprise.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_transfer_list_item.*
import me.rosuh.filepicker.config.FilePickerManager

/**
@author simpler
@create 2020年02月29日  13:30
 */
class TransferlistItemFragment(var type:Int,mContext:Context) :BaseFragment(){
    companion object{
        val DOWNLOAD =0
        val UPLOAD =1
        val ING=2
        val COMPLETED=3
    }
    var mcountDownTimer =  object :CountDownTimer(24 * 60 * 60 * 1000, 3*1000){
        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            mIngAdapter?.apply {
                if(!list.isEmpty()){
                    notifyDataSetChanged()
                }

            }

            mCompletedAdapter?.apply {
                if(!list.isEmpty()){
                    notifyDataSetChanged()
                }
            }

            ll_ing.visibility = if(mIngAdapter.list.isEmpty()) View.GONE else View.VISIBLE
            ll_completed.visibility = if(mCompletedAdapter.list.isEmpty()) View.GONE else View.VISIBLE
            if(mIngAdapter.list.isEmpty()&&mCompletedAdapter.list.isEmpty()){
                //没有任务   TODO
                tv_no_task_info.visibility = View.VISIBLE
                when(type){
                    DOWNLOAD->{tv_no_task_info.text= "暂无下载任务"}
                    UPLOAD->{tv_no_task_info.text= "暂无上传任务"}
                }

            }else{
                tv_no_task_info.visibility = View.GONE

            }


        }

    }
    lateinit var  mIngAdapter :LoadingFileAdapter
    lateinit var mCompletedAdapter:CompletedAdapter

    override fun initView() {
          when(type){
              DOWNLOAD->{
                  tv_ing_title.text = "正在下载${1}"
                  tv_completed_title.text ="下载完成"
                  mIngAdapter   = LoadingFileAdapter(context,MyApplication.downLoad_Ing)
                  mCompletedAdapter = CompletedAdapter(context,MyApplication.downLoad_completed)
              }
              UPLOAD->{
                  tv_ing_title.text = "正在上传${1}"
                  tv_completed_title.text ="上传完成"
                  mIngAdapter   = LoadingFileAdapter(context,MyApplication.upLoad_Ing)
                  mCompletedAdapter = CompletedAdapter(context,MyApplication.upLoad_completed)
              }
          }


        var layoutmanager = object :LinearLayoutManager(mContext){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rc_ing.apply {
            layoutmanager =layoutmanager
            adapter=mIngAdapter
            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
            mIngAdapter.SetOnRecyclerItemClickListener(object :OnRecyclerItemClickListener{
                override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                    (holder as LoadingFileAdapter.ViewHolder).apply {
                        var item= mIngAdapter.list[position]
                        tv_file_name.text =item.file_name

                         progress_download.setProgress(item.progress,true)
                        iv_down_flag.setImageResource(if(item.load_type_falg==0) R.drawable.reupload else R.drawable.redownload) ////0  up  1 down
                        iv_down_flag.setOnClickListener { //点击按钮修改文件状态
                            var  mainActivity=activity as MainActivity
                            when(item.Ingstatus){
                                LoadIngStatus.WAITING->{
                                    item.Ingstatus = LoadIngStatus.TRANSFERING
                                    (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.ReLoadFile(item)
                                }
                                LoadIngStatus.TRANSFERING->{
                                    item.Ingstatus = LoadIngStatus.WAITING
                                    (mainActivity.myBinder as DaemonService.MyBinder)?.GetDaemonService()?.LoadFileStop(item)
                                }
                            }
                        iv_show_del.setOnClickListener {
                            showDelBootomDialog(item.load_type_falg,position,item.Ingstatus)
                        }


                            mIngAdapter.notifyDataSetChanged()
                        }


                        when(item.Ingstatus){
                            LoadIngStatus.WAITING->{
                                tv_curr_size.text="等待中"
                                iv_down_flag.setImageResource(R.drawable.pause)
                            }
                            LoadIngStatus.CONFIG->{
                                iv_down_flag.setImageResource(R.drawable.redownload)
                                tv_curr_size.text="压缩加密中"
                            }
                            LoadIngStatus.TRANSFERING->{
                                iv_down_flag.setImageResource(R.drawable.redownload)
                                tv_curr_size.text=item.Speed
                            }
                        }

                    }
                }

            })

        }
        rc_completed.apply {
            layoutmanager =layoutmanager
            adapter =mCompletedAdapter


            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
            mCompletedAdapter.SetOnRecyclerItemClickListener( object :OnRecyclerItemClickListener{
                override fun onItemClick(holder: RecyclerView.ViewHolder, position: Int) {
                    (holder as CompletedAdapter.ViewHolder).apply {
                        var  item = mCompletedAdapter.list[position]
                        tv_file_name.text = item.file_name
                        iv_right_icon.visibility = View.VISIBLE

                        iv_right_icon.setOnClickListener {
                            showDelBootomDialog(type,position, COMPLETED)
                        }
                    tv_file_create_time.text = item.up_time
                    }
                }

            })

        }

    }

    private fun showDelBootomDialog(type:Int,position:Int,status:Int) {
        val topdrawable = mContext!!.resources.getDrawable( R.drawable.operate_delete_normal)
        topdrawable.setBounds(0, 0, topdrawable.minimumWidth, topdrawable.minimumHeight)
        BottomSheetDialog(mContext!!).apply {
           setContentView(LayoutInflater.from(mContext).inflate(R.layout.item_bottom_myfiles, null).apply {
               (this as TextView).apply {
                   text="删除"
                  setCompoundDrawablesWithIntrinsicBounds(null,topdrawable,null,null)
                   setOnClickListener {
                        when(type){
                            DOWNLOAD->{
                                when(status){
                                    ING ->{

                                        MyApplication.downLoad_Ing.apply {
                                            remove(this[position])
                                        }
                                        mIngAdapter.notifyDataSetChanged()
                                    }
                                    COMPLETED->{
                                        MyApplication.upLoad_completed.apply {
                                            remove(this[position])
                                        }
                                        mCompletedAdapter.notifyDataSetChanged()
                                    }

                                }
                            }
                            UPLOAD->{
                                when(status){
                                    ING ->{

                                        MyApplication.upLoad_Ing.apply {
                                            ((activity as MainActivity).myBinder as DaemonService.MyBinder)?.GetDaemonService()?.LoadFileStop(this[position])
                                            remove(this[position])
                                        }
                                        mIngAdapter.notifyDataSetChanged()
                                    }
                                    COMPLETED->{
                                        MyApplication.upLoad_completed.apply {
                                            remove(this[position])
                                        }
                                        mCompletedAdapter.notifyDataSetChanged()
                                    }

                                }

                            }
                        }
                       dismiss()
                   }
               }
           })
        }.show()
    }

    override fun initData(){

    }

    override fun onResume() {
        super.onResume()
        mcountDownTimer.start()

    }

    override fun onStop() {
        super.onStop()
        mcountDownTimer.cancel()
    }

    override fun GetRootViewID()= R.layout.fragment_transfer_list_item
}


