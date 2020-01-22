package com.ucas.cloudenterprise.ui.fragment

import android.os.Bundle
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseFragment

/**
@author simpler
@create 2020年01月10日  14:31
 */
class CommonDirsFragment: BaseFragment() {


    override fun initView() {

    }

    override fun initData() {}

    override fun GetRootViewID()= R.layout.personal_center_fragment
    companion object{
         private var instance: CommonDirsFragment? = null

        fun getInstance( param1:Boolean,  param2:String?): CommonDirsFragment {
            if (instance == null) {
                synchronized(CommonDirsFragment::class.java) {
                    if (instance == null) {
                        instance = CommonDirsFragment().apply {
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