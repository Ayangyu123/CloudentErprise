package com.ucas.cloudenterprise.app

import android.app.Application
import android.content.Context
import android.util.Log
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.startService

/**
@author simpler
@create 2019年12月27日  12:57
 */
class MyApplication:Application() {

    val TAG ="MyApplication"
    override fun onCreate() {
        super.onCreate()
        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE,Context.MODE_PRIVATE).apply {
            IS_FIRSTRUN = getBoolean(FIRSTRUN_NAME_FOR_PREFERENCE,true)
            IS_NOT_INSTALLED = getBoolean(NOT_INSTALLEDE_FOR_PREFERENCE,true)
        }


    }


}