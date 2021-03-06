package com.ucas.cloudenterprise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.model.CompletedFile
//上传文件的一个适配器   比如说 要上传个图片或者文本  都可以通过这个适配器 来进行配置
//实体类为CompletedFile
class CompletedAdapter(var context: Context?,
                       var list:ArrayList<CompletedFile>)
    : RecyclerView.Adapter<CompletedAdapter.ViewHolder>() {

    val TAG ="FilesAdapter"
    var mOnRecyclerItemClickListener : OnRecyclerItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(com.ucas.cloudenterprise.R.layout.item_myfiles, parent,false)
        return ViewHolder(view)
    }
    fun SetOnRecyclerItemClickListener(OnRecyclerItemClickListener : OnRecyclerItemClickListener){
        this.mOnRecyclerItemClickListener =OnRecyclerItemClickListener
    }
    override fun getItemCount(): Int {
        return  list?.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(mOnRecyclerItemClickListener!=null){
            mOnRecyclerItemClickListener!!.onItemClick(holder,position)
        }
    }

    class ViewHolder(itemView :View) :RecyclerView.ViewHolder(itemView)  {
        var rl_file_item_root = itemView.findViewById<RelativeLayout>(R.id.rl_file_item_root)
        var iv_icon =itemView.findViewById<ImageView>(R.id.iv_icon)
        var iv_right_icon =itemView.findViewById<ImageView>(R.id.iv_right_icon)
        var checkbox_is_checked =itemView.findViewById<CheckBox>(R.id.checkbox_is_checked)
        var tv_file_name =itemView.findViewById<TextView>(R.id.tv_file_name)
        var tv_file_create_time =itemView.findViewById<TextView>(R.id.tv_file_create_time)
        var end_line =itemView.findViewById<View>(R.id.end_line)
    }
}
