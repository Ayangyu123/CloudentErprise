package com.ucas.cloudenterprise.utils

import android.content.Context
import android.content.pm.PackageManager
import com.ucas.cloudenterprise.BuildConfig
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object AppUtils {
    //    1、生成的字符串每个位置都有可能是str中的一个字母或数字，需要导入的包是import java.util.Random;

//length用户要求产生字符串的长度

    fun  getRandomString( length:Int):String
    {
        var str ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ039.106.216.189789";
        var random =  Random();
        var sb =  StringBuffer();
        for (i in 0 until str.length){
            var number = random . nextInt (62);
            sb.append(str[number]);
        }
        return sb.toString();
    }

    fun timestamptoString(time:Long):String{
        var sd = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        return  sd.format(Date(time*1000L))
    }

    fun Stringtotimestamp(time:String):Long{
        var sd = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        return  sd.parse(time).time.toString().substring(0,10).toLong()
    }


    fun GetVerSionCode():Int{
        return  BuildConfig.VERSION_CODE

    }
}

fun main() {
//   var time:Long = 3166199506 2070/05/02
   var time:Long = 1583481749

    var sd = SimpleDateFormat("yyyy/MM/dd")

    var stringdate =sd.format(Date(time*1000L))
    println(stringdate)


   var timestampsrc = sd.parse(stringdate)

    println(timestampsrc.time.toString().substring(0,10))
    println(sd.format(Date(timestampsrc.time.toString().substring(0,10).toLong()*1000L)))


}