package com.ucas.cloudenterprise.ui

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.view.Window
import android.view.WindowManager
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
//领导页Sp判断
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
        stratDaemonService(this@SplashActivity)
        lifecycleScope.launchWhenResumed {
            Log.e("ok","1")
            delay(1*1000)
            Log.e("ok","2")
            delay(1*1000)
            Log.e("ok","3")
                if(IS_FIRSTRUN){
                    //第一次运行app 》》》 引导页
                    startActivity<WelcomeActivity>() }
                else{
                    //判断Token是否为空
                    if(TextUtils.isEmpty(ACCESS_TOKEN)){
                        // 》》》登陆页面
                        startActivity<LoginActivity>()
                    }else{
                        // 》》》主页面
                        startActivity<MainActivity>()
                    }

                }
                finish()
            }





    }


    fun checkPermission(): Boolean  {

        var isGranted = true
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false
            }

            if (!isGranted) {
                this.requestPermissions(
                    arrayOf(
                       WRITE_EXTERNAL_STORAGE
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