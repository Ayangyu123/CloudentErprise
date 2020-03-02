package com.ucas.cloudenterprise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.model.MemberInfo

class MemberInfoAdapter(var context: Context?, var list:ArrayList<MemberInfo>) : RecyclerView.Adapter<MemberInfoAdapter.ViewHolder>(){

    val TAG ="FilesAdapter"
    var mOnRecyclerItemClickListener : OnRecyclerItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberInfoAdapter.ViewHolder {
        var view = LayoutInflater.from(context).inflate(com.ucas.cloudenterprise.R.layout.item_members_manage, null)
        return MemberInfoAdapter.ViewHolder(view)
    }
    fun SetOnRecyclerItemClickListener(OnRecyclerItemClickListener : OnRecyclerItemClickListener){
        this.mOnRecyclerItemClickListener =OnRecyclerItemClickListener
    }
    override fun getItemCount(): Int {
        return  list?.size
    }

    override fun onBindViewHolder(holder: MemberInfoAdapter.ViewHolder, position: Int) {

        if(mOnRecyclerItemClickListener!=null){
            mOnRecyclerItemClickListener!!.onItemClick(holder,position)
        }





    }

    class ViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView){
        var ll_root=itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.ll_root)
        var tv_name =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_name)
        var tv_state =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_state)


    }
}
