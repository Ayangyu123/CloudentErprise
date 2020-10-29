package com.ucas.cloudenterprise.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.`interface`.OnRecyclerItemClickListener
import com.ucas.cloudenterprise.model.JurisItem

class JurisAdapter(var context: Context?, var list: ArrayList<JurisItem>) :
    RecyclerView.Adapter<JurisAdapter.ViewHolder>() {

    val TAG = "FilesAdapter"
    var mOnRecyclerItemClickListener: OnRecyclerItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(com.ucas.cloudenterprise.R.layout.item_team, parent, false)
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
        var fl_root = itemView.findViewById<FrameLayout>(com.ucas.cloudenterprise.R.id.fl_root)
        var tv_team_name =
            itemView.findViewById<TextView>(com.ucas.cloudenterprise.R.id.tv_team_name)
        var iv_remove = itemView.findViewById<ImageView>(com.ucas.cloudenterprise.R.id.iv_remove)
    }
}
