package com.ucas.cloudenterprise.`interface`

import androidx.recyclerview.widget.RecyclerView

/**
@author simpler
@create 2020年01月19日  17:11
 */
interface OnRecyclerItemClickListener {
    fun onItemClick(holder: RecyclerView.ViewHolder, position:Int)
}
