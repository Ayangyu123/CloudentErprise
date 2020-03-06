package com.ucas.cloudenterprise.model

import java.io.Serializable

/**
@author simpler
@create 2020年03月04日  11:05
 */
data class MemberAuditInfo(
    val member_id:String,
    val comp_id:String,
    val acc_name:String,
    val email:String,
    val telphone:String,
    val capacity:Int,
    val belong_team:List<BelongTeam>,
    val create_time:String,
    val status:Int,
    val remark:String
):Serializable