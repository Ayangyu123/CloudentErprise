package com.ucas.cloudenterprise.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.model.File_Bean
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.app.IS_DIR
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.FileInfoActivity
import com.ucas.cloudenterprise.ui.MainActivity
import com.ucas.cloudenterprise.utils.Toastinfo


/**
@author simpler
@create 2020年01月14日  08:51
 */
class BottomFilesOperateAdapter(    //底部四个按钮的监听适配器我的文件、内部共享、传输列表、个人中心
    val context: Context?,
    val item: File_Bean,
    val isfile: Boolean,
    val Permisssion: Int = 4,
    val isroot_file: Boolean = false,
    var ispshare_file: Boolean
) : RecyclerView.Adapter<BottomFilesOperateAdapter.ViewHolder>() {
    val TAG = "BottomFilesAdapter"
    var mOnRecyclerItemClickListener: OnRecyclerItemClickListener? = null

    //点击条目操作时的一些小功能图片  连接分享、下载、复制到、移动到、重命名、删除、详情信息
    var DrawableList = arrayListOf<Int>(    //存入图片集合
        R.drawable.operate_share_dir_normal,
        R.drawable.operate_link_share_normal,
        R.drawable.operate_download_normal,
        R.drawable.operate_copy_to_normal,
        R.drawable.operate_move_to_normal,
        R.drawable.operate_rename_normal,
        R.drawable.operate_delete_normal,
        R.drawable.operate_detail_normal
    )
    var InfoList = arrayListOf<String>( //存入对应功能名字的集合
        "设置共享",
        "链接分享",
        "下载",
        "复制到",
        "移动到",
        "重命名",
        "删除",
        "详细信息"
    )

    init {
        //123
        /**
         *   1.判断是否为共享文件
         *   2.是否为文件加
         *   3.是否为根目录
         * */
        if (isfile) { //isfile定义为布尔类型  默认为true
            InfoList.remove("设置共享")
            DrawableList.remove(R.drawable.operate_share_dir_normal)
        } else {
            InfoList.remove("链接分享")
            DrawableList.remove(R.drawable.operate_link_share_normal)
            InfoList.remove("下载")
            DrawableList.remove(R.drawable.operate_download_normal)
            InfoList.remove("复制到")
            InfoList.remove("移动到")
            DrawableList.remove(R.drawable.operate_copy_to_normal)
            DrawableList.remove(R.drawable.operate_move_to_normal)
        }
//        when(Permisssion){
//            1->{
//
//                InfoList.remove(  "设置共享")
//                DrawableList.remove( R.drawable.operate_share_dir_normal)
//                InfoList.remove("移动到")
//                DrawableList.remove( R.drawable.operate_move_to_normal)
//                InfoList.remove("重命名")
//                DrawableList.remove(R.drawable.operate_rename_normal)
//                InfoList.remove("删除")
//                DrawableList.remove(R.drawable.operate_delete_normal)
//
//            }
//            2->{
//
//
//                InfoList.remove(  "设置共享")
//                DrawableList.remove( R.drawable.operate_share_dir_normal)
//                InfoList.remove("移动到")
//                DrawableList.remove( R.drawable.operate_move_to_normal)
//                InfoList.remove("重命名")
//                DrawableList.remove(R.drawable.operate_rename_normal)
//                //TODO
//                InfoList.remove("删除")
//                DrawableList.remove(R.drawable.operate_delete_normal)
//            }
//        }

        if (!isroot_file) { //  强制性在注册的时候将isroot_file设置为false   在判断的时候又强制性设置为True
            InfoList.remove("设置共享")
            DrawableList.remove(R.drawable.operate_share_dir_normal)
        }
        if (ispshare_file) {

            InfoList.clear()
            DrawableList.clear()

            if (isroot_file && !isfile) {
                if (Permisssion == 4) {
                    InfoList.add("取消共享")
                    DrawableList.add(R.drawable.operate_delete_normal)
                }
                InfoList.add("详细信息")
                DrawableList.add(R.drawable.operate_detail_normal)
            } else {
                if (!isfile) {
                    InfoList.add("详细信息")
                    DrawableList.add(R.drawable.operate_detail_normal)
                } else {
                    InfoList.add("下载")
                    DrawableList.add(R.drawable.operate_download_normal)
                    InfoList.add("详细信息")
                    DrawableList.add(R.drawable.operate_detail_normal)
                }
            }
        }
    }

    fun SetOnRecyclerItemClickListener(OnRecyclerItemClickListener: OnRecyclerItemClickListener) {
        this.mOnRecyclerItemClickListener = OnRecyclerItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = LayoutInflater.from(context).inflate(
            if (getItemCount() != 1) R.layout.item_bottom_myfiles else R.layout.item_bottom_del,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return InfoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mOnRecyclerItemClickListener != null) {
            mOnRecyclerItemClickListener!!.onItemClick(holder, position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}


