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
    var pid :String,
    var compet_team :String,
    var compet_user :String,
    var status :Int,
    var size :Long,
    var created_at :String,
    var updated_at :String
    ):Serializable