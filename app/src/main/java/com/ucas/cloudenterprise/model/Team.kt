package com.ucas.cloudenterprise.model

import java.io.Serializable

/**
@author simpler
@create 2020年01月06日  16:19
 */
    //  Team 团队类
 data class Team(val team_id:String, var team_name:String, var team_level :String, var status :Int,var created_at:String,var updated_at:String,var company_id:String ):Serializable