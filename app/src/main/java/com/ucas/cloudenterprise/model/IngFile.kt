package com.ucas.cloudenterprise.model

import java.io.Serializable

data class IngFile(
//    var file_id:String,
//    var isDir:Int,
    var file_name:String,
    var file_size:String,
    var curr_size:String,
    var task :Runnable,
    var state :IngFileState//1正在现在 2 暂停  3等待
):Serializable

public enum class IngFileState {

    ING,//1正在现在
    PAUS,
    COMPLETED,//已完成
    WAITING,//等待

}