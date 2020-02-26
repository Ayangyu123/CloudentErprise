package com.ucas.cloudenterprise.utils

import java.util.*

object AppUtils {
    //    1、生成的字符串每个位置都有可能是str中的一个字母或数字，需要导入的包是import java.util.Random;

//length用户要求产生字符串的长度

    fun  getRandomString( length:Int):String
    {
        var str ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        var random =  Random();
        var sb =  StringBuffer();
        for (i in 0 until str.length){
            var number = random . nextInt (62);
            sb.append(str[number]);
        }
        return sb.toString();
    }
}