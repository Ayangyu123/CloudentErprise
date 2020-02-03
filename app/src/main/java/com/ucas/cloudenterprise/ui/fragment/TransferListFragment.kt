package com.ucas.cloudenterprise.ui.fragment

import android.os.Bundle
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseFragment
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.android.synthetic.main.transfer_list_fragment.*

/**
@author simpler
@create 2020年01月10日  14:31
 */
class TransferListFragment: BaseFragment() {


    override fun initView() {
        iv_back.visibility = View.GONE
        tv_title.text = "传输列表"
        tv_download.setOnClickListener {
            view_select_bar.animate().translationX(view_select_bar.width.toFloat()*0f)
        }
        tv_upload.setOnClickListener {
            view_select_bar.animate().translationX(view_select_bar.width.toFloat()*2f)
        }

    }

    override fun initData() {}

    override fun GetRootViewID()= R.layout.transfer_list_fragment
    companion object{
         private var instance: TransferListFragment? = null

        fun getInstance( param1:Boolean,  param2:String?): TransferListFragment {
            if (instance == null) {
                synchronized(TransferListFragment::class.java) {
                    if (instance == null) {
                        instance = TransferListFragment().apply {
                            this.arguments =Bundle().apply {
                                putBoolean("param1",param1)
                                putString("param2",param2)
                            }
                        }
                    }
                }
            }
            return instance!!
        }
    }

}