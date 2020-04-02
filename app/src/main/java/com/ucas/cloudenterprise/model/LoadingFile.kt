package com.ucas.cloudenterprise.model

import java.io.File
import java.io.InputStream
import java.io.Serializable

/**
@author simpler
@create 2020年03月13日  14:55
 */
data class LoadingFile(
    val load_type_falg:Int,  //0  up  1 down
    val file_name:String,
    val file_MD5:String?=null,
    val file_hash:String?=null,
    val file_size:Long,
    val dest_file: File?=null,
    val pid :String?=null,
    var Ingstatus:Int = LoadIngStatus.WAITING,
    var progress:Int=0,
    var Speed:String="",
    var src_file_info:File_Bean?=null
    ):Serializable
object LoadIngStatus{
    val WAITING =0 //等待
   val CONFIG =1 //配置
   val TRANSFERING =2 //传输
   val PACK =3  //加密压缩
   val UNPACK =4 //解密解压
}