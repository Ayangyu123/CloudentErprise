package com.ucas.cloudenterprise.model

import java.io.Serializable

/*
上传完成
* */
class UpLoadCompletedFile(
    var file_name:String,
    var up_time:String,
    var file_size:String,
    var selected: Boolean =false
):Serializable
