package com.ucas.cloudenterprise.app

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.startService

/**
@author simpler
@create 2019年12月27日  12:57
 */
class MyApplication():Application() {

    override fun onCreate() {
        super.onCreate()
        startService<DaemonService>()

    }


}