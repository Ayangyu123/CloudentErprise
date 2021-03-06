package com.ucas.cloudenterprise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.model.Team

class SelectTeamAdapter(var context: Context?, var list: ArrayList<Team>) :
    RecyclerView.Adapter<SelectTeamAdapter.ViewHolder>() {

    val TAG = "FilesAdapter"
    var mOnRecyclerItemClickListener: OnRecyclerItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(com.ucas.cloudenterprise.R.layout.item_members, parent, false)
        return ViewHolder(view)
    }

    fun SetOnRecyclerItemClickListener(OnRecyclerItemClickListener: OnRecyclerItemClickListener) {
        this.mOnRecyclerItemClickListener = OnRecyclerItemClickListener
    }

    override fun getItemCount(): Int {
        return list?.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (mOnRecyclerItemClickListener != null) {
            mOnRecyclerItemClickListener!!.onItemClick(holder, position)
        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ll_root = itemView.findViewById<LinearLayout>(com.ucas.cloudenterprise.R.id.ll_root)
        var check_box_select_team_all =
            itemView.findViewById<CheckBox>(com.ucas.cloudenterprise.R.id.check_box_select_team_all)
        var tv_team_name =
            itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_team_name)
        var tv_members_count =
            itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_members_count)
        var iv_team_falg =
            itemView.findViewById<ImageView>(com.ucas.cloudenterprise.R.id.iv_team_falg)


    }
}
