package com.ucas.cloudenterprise.model

import java.io.Serializable

data class DownLoadIngFile(
    var file_id:String,
    var isDir:Int,
    var file_name:String,
    var file_size:String,
    var down_size:String,
    var state :DownLoadIngFileState//1正在现在 2 暂停  3等待
):Serializable

public enum class DownLoadIngFileState {

    DOWNLOADING,//1正在现在
    PAUS,
    COMPLETED,//已完成

}