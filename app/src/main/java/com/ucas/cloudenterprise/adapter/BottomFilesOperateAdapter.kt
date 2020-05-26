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
class BottomFilesOperateAdapter(
    val context: Context?,
    val item: File_Bean,
    val isfile: Boolean,
    val Permisssion:Int =4,
    val isroot_file:Boolean = false,
    var ispshare_file:Boolean=false

) :RecyclerView.Adapter<BottomFilesOperateAdapter.ViewHolder>(){
    val TAG ="BottomFilesAdapter"
    var mOnRecyclerItemClickListener : OnRecyclerItemClickListener? = null
    var DrawableList = arrayListOf<Int>(
        R.drawable.operate_share_dir_normal,
        R.drawable.operate_link_share_normal,
        R.drawable.operate_download_normal,
        R.drawable.operate_copy_to_normal,
        R.drawable.operate_move_to_normal,
        R.drawable.operate_rename_normal,
        R.drawable.operate_delete_normal,
        R.drawable.operate_detail_normal
       )
    var InfoList = arrayListOf<String>(
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
        if(isfile){
            InfoList.remove(  "设置共享")
            DrawableList.remove( R.drawable.operate_share_dir_normal)
        }else{
            InfoList.remove("链接分享")
            InfoList.remove("下载")
            DrawableList.remove( R.drawable.operate_link_share_normal)
            DrawableList.remove( R.drawable.operate_download_normal)
        }
        when(Permisssion){
            1->{
                InfoList.remove("移动到")

                InfoList.remove("重命名")
                InfoList.remove("删除")
                InfoList.remove(  "设置共享")
                DrawableList.remove( R.drawable.operate_share_dir_normal)
                DrawableList.remove( R.drawable.operate_move_to_normal)
                DrawableList.remove(R.drawable.operate_rename_normal)
                DrawableList.remove(R.drawable.operate_delete_normal)

            }
            2->{
                InfoList.remove("移动到")
                InfoList.remove("重命名")
                InfoList.remove(  "设置共享")
                DrawableList.remove( R.drawable.operate_share_dir_normal)
                DrawableList.remove( R.drawable.operate_move_to_normal)
                DrawableList.remove(R.drawable.operate_rename_normal)
                    //TODO
                InfoList.remove("删除")
                DrawableList.remove(R.drawable.operate_delete_normal)
            }
        }

        if(!isroot_file){
            InfoList.remove(  "设置共享")
            DrawableList.remove( R.drawable.operate_share_dir_normal)
        }
        if(ispshare_file){
            InfoList.remove("复制到")
            DrawableList.remove( R.drawable.operate_copy_to_normal)
            InfoList.remove("移动到")
            DrawableList.remove( R.drawable.operate_move_to_normal)
        }
    }

    fun SetOnRecyclerItemClickListener(OnRecyclerItemClickListener : OnRecyclerItemClickListener){
        this.mOnRecyclerItemClickListener =OnRecyclerItemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(com.ucas.cloudenterprise.R.layout.item_bottom_myfiles, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return  InfoList.size
       }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(mOnRecyclerItemClickListener!=null){
            mOnRecyclerItemClickListener!!.onItemClick(holder,position)
        }
        }

    class ViewHolder(itemView :View) :RecyclerView.ViewHolder(itemView){

    }


}

private fun TextView.setCompoundDrawablesWithIntrinsicBounds(
    nothing: Nothing?,
    topdrawable: Drawable?,
    nothing1: Nothing?,
    nothing2: Nothing?,
    nothing3: Nothing?
) {

}
