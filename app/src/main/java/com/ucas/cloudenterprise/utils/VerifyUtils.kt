package com.ucas.cloudenterprise.utils

import kotlinx.android.synthetic.main.activity_forget_password.*
import java.util.regex.Pattern

/**
@author simpler
@create 2020年03月03日  14:52
 */
object VerifyUtils {
    fun VerifyPhone (phone:String):Boolean{
        return Verify(phone,"^1[3-9][0-9]{9}$")
    }
    fun VerifyEmail (eamil:String):Boolean{
        return  Verify(eamil,"^[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?$")
    }
    fun Verify(s:String,regex :String):Boolean{
        return  Pattern.compile(regex).matcher(s).matches()
    }
}