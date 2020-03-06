package com.ucas.cloudenterprise.model

import java.io.Serializable

/**
@author simpler
@create 2020年02月29日  08:51
 */
data class MemberInfo(

var member_id:String,//     "member_id": "sjhfjshkfhkhadk1333",(成员id)
var comp_id:String,//    "comp_id":"国科环宇xxxx",(公司id)
var comp_name:String,//    "comp_id":"国科环宇xxxx",(公司名称 // )
var acc_name:String,//    "acc_name": "wkeej20192",(账号名称)
var email:String,//    "email":"ysh@sina.com",(邮件)
var telphone:String,//    "telphone": "13789012375",(电话)
var capacity:Int,//    "capacity":10G,(容量)
var belong_team:ArrayList<BelongTeam>,//    "belong_team": [
//    "belong_teamId":"12345678754",(所属团队id)
//    ]
var create_time:String//    "create_time":"2020-02-27 11:09:10",(创建日期)
//}
): Serializable
data class BelongTeam(
    var belong_teamId: String,//(所属团队id)
    var team_name: String//(所属团队名称)
): Serializable