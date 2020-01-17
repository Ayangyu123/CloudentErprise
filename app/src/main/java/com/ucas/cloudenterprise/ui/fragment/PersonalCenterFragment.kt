package com.ucas.cloudenterprise.ui.fragment

import android.os.Bundle
import android.view.View
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseFragemnt

/**
@author simpler
@create 2020年01月10日  14:31
 */
class PersonalCenterFragment:BaseFragemnt() {


    override fun initView() {

    }

    override fun initData() {}

    override fun GetRootViewID()= R.layout.personal_center_fragment
    companion object{
         private var instance: PersonalCenterFragment? = null

        fun getInstance( param1:Boolean,  param2:String?): PersonalCenterFragment {
            if (instance == null) {
                synchronized(PersonalCenterFragment::class.java) {
                    if (instance == null) {
                        instance = PersonalCenterFragment().apply {
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