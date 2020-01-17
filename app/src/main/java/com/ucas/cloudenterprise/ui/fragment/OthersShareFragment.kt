package com.ucas.cloudenterprise.ui.fragment

import android.os.Bundle
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseFragemnt

/**
@author simpler
@create 2020年01月10日  14:31
 */
class OthersShareFragment :BaseFragemnt() {


    override fun initView() {

    }

    override fun initData() {}

    override fun GetRootViewID()= R.layout.others_share_fragment
    companion object{
         private var instance: OthersShareFragment? = null

        fun getInstance( param1:Boolean?,  param2:String?): OthersShareFragment {
            if (instance == null) {
                synchronized(OthersShareFragment::class.java) {
                    if (instance == null) {
                        instance = OthersShareFragment()
//                            .apply {
//                            this.arguments =Bundle().apply {
//                                putBoolean("param1", param1)
//                                putString("param2",param2)
//                            }
//                        }
                    }
                }
            }
            return instance!!
        }
    }

}