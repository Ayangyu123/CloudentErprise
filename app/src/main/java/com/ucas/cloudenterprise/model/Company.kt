package com.ucas.cloudenterprise.model

import java.io.Serializable

/**
@author simpler
@create 2020年01月06日  16:19
 */
    //  Company 企业类
 data class Company(
 val comp_id:String,
 var comp_name:String,
 var comp_con_email :String,
 var comp_con_tel:String,
 var vip_type :Int,
 var  vip_exp :Int,
 var  total_cap :Int,
 var used_cap :Int,
 var limit_user :Int,
 val create_time:String,
 var update_time:String,
 var status :Int):Serializable