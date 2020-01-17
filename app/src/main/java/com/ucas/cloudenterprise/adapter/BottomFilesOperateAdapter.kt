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
import com.ucas.cloudenterprise.ui.FileInfoActivity
import com.ucas.cloudenterprise.utils.Toastinfo


/**
@author simpler
@create 2020年01月14日  08:51
 */
class BottomFilesOperateAdapter(
    val context: Context?,
    val item: File_Bean,
    val isfile: Boolean,
    val sheetDialog: BottomSheetDialog
) :RecyclerView.Adapter<BottomFilesOperateAdapter.ViewHolder>(){
    val TAG ="BottomFilesAdapter"
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
            InfoList.removeAt(0)
            DrawableList.removeAt(0)
            Log.e(TAG,"isfile is ${isfile}")
            Log.e(TAG,"InfoList${InfoList.size}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(com.ucas.cloudenterprise.R.layout.item_bottom_myfiles, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return  InfoList.size
       }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var iteminfo =InfoList[position]
        val topdrawable = context!!.resources.getDrawable(DrawableList[position])


        topdrawable.setBounds(0, 0, topdrawable.minimumWidth, topdrawable.minimumHeight)

            var tv_text =holder.itemView as TextView
            tv_text.text=iteminfo
            tv_text.setCompoundDrawablesWithIntrinsicBounds(null,topdrawable,null,null)

        tv_text.setOnClickListener{
            var tv=it as TextView
            when(tv.text.toString()){
                "设置共享"->{ Toastinfo("设置共享")

                }
                "链接分享"->{Toastinfo("链接分享")}
                "下载"->{Toastinfo("下载")}
                "复制到"->{Toastinfo("复制到")}
                "移动到"->{Toastinfo("移动到")}
                "重命名"->{Toastinfo("重命名")}
                "删除"->{Toastinfo("删除")}
                "详细信息"->{Toastinfo("详细信息")
                    context.startActivity(Intent(context,FileInfoActivity::class.java).apply {
                        putExtra("file",item)
                    })
                }
            }
            sheetDialog.dismiss()
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
