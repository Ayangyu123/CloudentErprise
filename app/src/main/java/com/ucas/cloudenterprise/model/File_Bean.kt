package com.ucas.cloudenterprise.model

import java.io.Serializable

/**
@author simpler
@create 2020年01月08日  09:07
 */
data class File_Bean(
    var file_id :String,
    var file_name :String,
    var is_dir :Int,
    var user_id :String,
    var team_id :String,
    var fidhash :String,
    var filehash :String,
    var pid :String,
    var compet_team :String,
    var compet_user :String,
    var status :Int,
    var share :Int, //链接分享
    var pshare :Int,//共享
    var store :Int, //收藏
    var size :Long,
    var created_at :String,
    var updated_at :String,
    var is_show_checked_view:Boolean =false,
    var is_checked:Boolean =false,
    var weight:Int=4
    ):Serializable