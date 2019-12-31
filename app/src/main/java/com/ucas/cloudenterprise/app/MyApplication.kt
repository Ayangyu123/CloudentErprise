package com.ucas.cloudenterprise.app

import android.app.Application
import android.content.Context

/**
@author simpler
@create 2019年12月27日  12:57
 */
class MyApplication:Application() {




    override fun onCreate() {
        super.onCreate()
        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE,Context.MODE_PRIVATE).apply {
            IS_FIRSTRUN = getBoolean(FIRSTRUN_NAME_FOR_PREFERENCE,true)
        }

    }


}