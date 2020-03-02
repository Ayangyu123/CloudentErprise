package com.ucas.cloudenterprise.ui.fragment

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_transfer_list_item.*

/**
@author simpler
@create 2020年02月29日  13:30
 */
class TransferlistItemFragment(var type:Int) :BaseFragment(){
    companion object{
        val DOWNLOAD =0
        val UPLOAD =1
    }
    override fun initView() {
          when(type){
              DOWNLOAD->{
                  tv_ing_title.text = "正在下载${1}"
                  tv_completed_title.text ="下载完成"


              }
              UPLOAD->{
                  tv_ing_title.text = "正在上传${1}"
                  tv_completed_title.text ="上传完成"
              }
          }
        var layoutmanager = object :LinearLayoutManager(mContext){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rc_ing.apply {
            layoutmanager =layoutmanager
            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        }
        rc_completed.apply {
            layoutmanager =layoutmanager
            addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))

        }

    }

    override fun initData(){

    }

    override fun GetRootViewID()= R.layout.fragment_transfer_list_item
}