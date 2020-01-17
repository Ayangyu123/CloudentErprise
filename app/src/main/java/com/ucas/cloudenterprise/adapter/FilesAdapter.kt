package com.ucas.cloudenterprise.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.model.File_Bean
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ucas.cloudenterprise.R


/**
@author simpler
@create 2020年01月14日  08:51
 */
class FilesAdapter(var context: Context?, var list:ArrayList<File_Bean>) :RecyclerView.Adapter<FilesAdapter.ViewHolder>(){
    val TAG ="FilesAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(com.ucas.cloudenterprise.R.layout.item_myfiles, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list?.size
       }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item =list[position]

        holder.apply {
            tv_title.text = item.file_name
            tv_time.text = item.created_at
            val isfile = if(item.is_dir==-1)true  else false

            iv_right_icon.setOnClickListener{
                val sheetDialog = BottomSheetDialog(context!!)
                 val contentview = LayoutInflater.from(context!!).inflate(R.layout.dialog_bottom_files,null) as RecyclerView
                contentview.layoutManager =GridLayoutManager(context,4)
                Log.e(TAG,"isfile is ${isfile}")
                contentview.adapter =BottomFilesOperateAdapter(context,item,isfile,sheetDialog)

                sheetDialog.setContentView(contentview)
                sheetDialog.show()
            }



            if(!isfile){
                iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_folder)
            }else{
                var filetype = item.file_name.substringAfterLast(".")
                Log.e(TAG,"filetype is ${filetype}")
                if(filetype.equals("text")||filetype.equals("txt")){
                    iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_txtfile)
                }
                if(filetype.equals("doc")||filetype.equals("docx")){
                    iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_doc)
                }
                if(filetype.equals("pdf")){
                    iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_pdf)
                }
                if(filetype.equals("exe")){
                    iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_exe)
                }
                if(filetype.equals("apk")){
                    iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_apk)
                }
                if(filetype in arrayOf("jpg","png","jpge","psd","svg")){
                    iv_icon.setImageResource(com.ucas.cloudenterprise.R.drawable.icon_list_image)
                }
            }


        }



        }

    class ViewHolder(itemView :View) :RecyclerView.ViewHolder(itemView){
        var iv_icon =itemView.findViewById<ImageView>(com.ucas.cloudenterprise.R.id.iv_icon)
        var iv_right_icon =itemView.findViewById<ImageView>(com.ucas.cloudenterprise.R.id.iv_right_icon)
        var tv_title =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_title)
        var tv_time =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_time)

    }


}