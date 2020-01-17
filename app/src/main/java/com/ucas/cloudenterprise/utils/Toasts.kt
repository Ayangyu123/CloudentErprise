package com.ucas.cloudenterprise.utils

import android.widget.Toast
import com.ucas.cloudenterprise.app.MyApplication

/**
@author simpler
@create 2020年01月07日  13:18
 */
 fun Toastinfo(info:String)=  Toast.makeText(MyApplication.context,info,Toast.LENGTH_SHORT).show()