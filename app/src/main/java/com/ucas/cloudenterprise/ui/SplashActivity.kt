package com.ucas.cloudenterprise.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import com.hjq.permissions.XXPermissions
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.IS_FIRSTRUN
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity

/**
@author simpler
@create 2020年01月08日  13:32
 */
class SplashActivity:BaseActivity() {
    override fun InitView() {}

    override fun InitData() {
//      TODO 权限检查 不vcxmjxç  checkPermission()



        Thread(object : Runnable {
            override fun run() {
//                Thread.sleep(5*1000)
                Thread.sleep(2*1000)
                runOnUiThread(){
                    if(IS_FIRSTRUN){
                        //第一次运行app 条状引导页
                        startActivity<WelcomeActivity>() }
                    else{
                        //判断Token是否为空
                        startActivity<MainActivity>()
                    }
                    finish()
                }

            }

        }
        ).start()
    }

    override fun GetContentViewId() = R.layout.activity_splash
    fun checkPermission(): Boolean? {
        var isGranted = true
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
            }

            if (!isGranted) {
                this.requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    102
                )
            }
        }
        return isGranted
    }



}