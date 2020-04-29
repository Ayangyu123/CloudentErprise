package com.ucas.cloudenterprise.model

import org.java_websocket.client.WebSocketClient
import java.io.File
import java.io.InputStream
import java.io.Serializable

/**
@author simpler
@create 2020年03月13日  14:55
 */
data class LoadingFile(
    val load_type_falg:Int,  //1 up  0 down
    val file_name:String,
    var file_MD5:String?=null,
    var file_hash:String?=null,
    val file_size:Long,
    val dest_file: File?=null,
    var Aes_key: String?=null,
    val pid :String?=null,
    var hasPacked:Boolean=false,
    var hasTransfer:Boolean=false,
    var Ingstatus:Int = LoadIngStatus.WAITING,
    var progress:Int=0,
    var Speed:String="",
    var src_file_info:File_Bean?=null,
    var webSocketClient: WebSocketClient?=null
    ):Serializable
object LoadIngStatus{
    val WAITING =0 //等待
   val CONFIG =1 //配置
   val TRANSFERING =2 //传输
   val PACK =3  //加密压缩
   val UNPACK =4 //解密解压
}