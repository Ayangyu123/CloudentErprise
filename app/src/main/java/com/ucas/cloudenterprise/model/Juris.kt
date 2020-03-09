package com.ucas.cloudenterprise.model

import java.io.Serializable

/**
@author simpler
@create 2020年03月09日  13:40
 */
class Juris(
    var role_id:String,
    var weight:Int,
    var juris_item:ArrayList<JurisItem>
):Serializable

class JurisItem(
    var juris_user_id:String,
    var juris_flag:Int
):Serializable
