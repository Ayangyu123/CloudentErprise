package me.rosuh.filepicker.adapter


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import me.rosuh.filepicker.bean.FileBean

abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract fun getItem(position: Int): FileBean?
    abstract fun getItemView(position: Int): View?
}