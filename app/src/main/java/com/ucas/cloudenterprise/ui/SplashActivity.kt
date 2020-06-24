package com.ucas.cloudenterprise.ui

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.widget.Space
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.ACCESS_TOKEN
import com.ucas.cloudenterprise.app.IS_FIRSTRUN
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.core.stratDaemonService
import com.ucas.cloudenterprise.utils.StatusBarUtil
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity
import com.ucas.cloudenterprise.utils.startService
import kotlinx.coroutines.*
import me.rosuh.filepicker.FilePickerActivity
import kotlin.concurrent.thread

/**
@author simpler
@create 2020年01月08日  13:32
 */
class SplashActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        StatusBarUtil.setStatusDarkColor(window)
        if( checkPermission()){
        startnextstep()
        }


    }

    private fun startnextstep() {
        lifecycleScope.launchWhenResumed {

            delay(1*1000)
                Log.e("ok","1")
            delay(1*1000)
                Log.e("ok","2")
            delay(1*1000)
                Log.e("ok","3")
                if(IS_FIRSTRUN){
                    //第一次运行app 条状引导页
                    startActivity<WelcomeActivity>() }
                else{
                    //判断Token是否为空
                    if(TextUtils.isEmpty(ACCESS_TOKEN)){
                        startActivity<LoginActivity>()
                    }else{
                        startActivity<MainActivity>()
                    }

                }
                finish()
            }





    }


    fun checkPermission(): Boolean {



        var isGranted = true
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false
            }
//            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                isGranted = false
//            }

            if (!isGranted) {
                this.requestPermissions(
                    arrayOf(
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    102
                )
            }
        }
        return isGranted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            102 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                   Toastinfo("没有写入权限,应用将退出")
                    finish()
                } else {
                   startnextstep()
                }
            }
        }
    }
}