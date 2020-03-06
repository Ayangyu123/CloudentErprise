package com.ucas.cloudenterprise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.model.MemberAuditInfo

class MemberAuditAdapter(var context: Context?, var list:ArrayList<MemberAuditInfo>) : RecyclerView.Adapter<MemberAuditAdapter.ViewHolder>(){

    val TAG ="FilesAdapter"
    var mOnRecyclerItemClickListener : OnRecyclerItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberAuditAdapter.ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_members_aduit, parent,false)
        return MemberAuditAdapter.ViewHolder(view)
    }
    fun SetOnRecyclerItemClickListener(OnRecyclerItemClickListener : OnRecyclerItemClickListener){
        this.mOnRecyclerItemClickListener =OnRecyclerItemClickListener
    }
    override fun getItemCount(): Int {
        return  list?.size
    }

    override fun onBindViewHolder(holder: MemberAuditAdapter.ViewHolder, position: Int) {

        if(mOnRecyclerItemClickListener!=null){
            mOnRecyclerItemClickListener!!.onItemClick(holder,position)
        }





    }

    class ViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView){
        var ll_root=itemView.findViewById<LinearLayout>(com.ucas.cloudenterprise.R.id.ll_root)
        var tv_member_name =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_member_name)
        var tv_reason_for_application =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_reason_for_application)
        var tv_state =itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_state)


    }
}
