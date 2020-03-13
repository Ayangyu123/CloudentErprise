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
    val src_file_inputstream:InputStream ?=null,
    val dest_file: File?=null,
    val pid :String?=null,
    var Ingstatus:IngFileState = IngFileState.WAITING,
    var progress:Int=0):Serializable