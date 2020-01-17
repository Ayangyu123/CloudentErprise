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
//    fun checkPermission(): Boolean? {
//        var isGranted = true
//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            XXPermissions.gotoPermissionSettings(this);
////        XXPermissions.with(this)
////                .permission(Permission.Group.STORAGE)
////                .request(new OnPermission() {
////
////                    @Override
////                    public void hasPermission(List<String> granted, boolean isAll) {
////                    if (isAll) {
////                            DialogUtil.showMessage("获取权限成功");
////                        } else {
////                            DialogUtil.showMessage("获取权限成功，部分权限未正常授予");
////                        }
////                    }
////
////                    @Override
////                    public void noPermission(List<String> denied, boolean quick) {
////                        if (quick) {
////                            DialogUtil.showMessage("被永久拒绝授权，请手动授予权限");
////                            //如果是被永久拒绝就跳转到应用权限系统设置页面
////                            XXPermissions.gotoPermissionSettings(SplashActivity.this);
////                        } else {
////                            DialogUtil.showMessage("获取权限失败");
////                        }
////                    }
////                });
//        }
//        return isGranted
//    }


}