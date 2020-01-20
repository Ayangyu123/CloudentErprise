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
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener


/**
@author simpler
@create 2020年01月14日  08:51
 */
class FilesAdapter(var context: Context?, var list:ArrayList<File_Bean>) :RecyclerView.Adapter<FilesAdapter.ViewHolder>(){
    val TAG ="FilesAdapter"
    var mOnRecyclerItemClickListener : OnRecyclerItemClickListener ? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(com.ucas.cloudenterprise.R.layout.item_myfiles, null)
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

    class ViewHolder(itemView :View) :RecyclerView.ViewHolder(itemView){

          var iv_icon =itemView.findViewById<ImageView>(com.ucas.cloudenterprise.R.id.iv_icon)
          var iv_right_icon =itemView.findViewById<ImageView>(com.ucas.cloudenterprise.R.id.iv_right_icon)
          var tv_title =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_title)
          var tv_time =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_time)




    }


}