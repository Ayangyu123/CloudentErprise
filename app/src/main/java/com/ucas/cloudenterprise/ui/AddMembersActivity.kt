package com.ucas.cloudenterprise.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.model.Resource
import com.ucas.cloudenterprise.utils.Toastinfo
import org.json.JSONObject
import java.util.ArrayList

/**
@author simpler
@create 2020年01月06日  13:22
 */
class AddMembersActivity : AppCompatActivity(){
    val  TAG ="AddMembersActivity"

    val  team_id = "1578371132474935000-7696043959137995161"
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addmembers)
//        220546049285033984
//        comp_id acc_name capacity Invit_mode
//                team_name belong_level telphone member_name pid(如果无上级则传空)
//        comp_id acc_name  team_name belong_level  member_name pid(如果无上级则传空)
    }

    fun AddMember(view: View) {
        val params = HashMap<String,Any>()
        params["comp_id"] = "${COMP_ID}" //企业ID
        params["acc_name"] = "test" //comp_name
        params["team_name"] = "team_test" // 用户输入
        params["belong_teamId"] = "${team_id}"  //用户输入 test
        params["belong_team"] = ArrayList<HashMap<String,String>>().apply {
            add(HashMap<String,String>().apply {
                put("belong_teamId", "${team_id}")
            })
        }
//        params["belong_team"] = HashMap<String,String>().apply {
//                put("belong_teamId", "${team_id}")
//            }

        params["capacity"] = 10


        val json = JSONObject(params as Map<String, Any>)

        OkGo.post<String>(URL_ADD_MEMBER).
            upJson(json).
            execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG,response?.body().toString())
                }

            })


    }

    fun GetMembers(view: View) {

        OkGo.get<String>(URL_LIST_MEMBER+"company/${COMP_ID}/mem/${team_id}").
            execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG, response?.body().toString())

                    response?.let {
                        val json = JSONObject(it.body().toString())
                        val code = json.getInt("code")
                        if (code == Resource.SUCCESS) {
                            Toastinfo("获取成员列表成功")
//                            tv_result.text = it.body().toString()
//                            var team_list = Gson().fromJson<List<Team>>(json.getJSONArray("data").toString(),
//                                object : TypeToken<List<Team>>(){}.type)
////                                Array<Team>::class.java).toMutableList())
//                            Log.e(TAG,team_list.toString())
                        } else {
                            //TODO
                        }
                    }
                }
            })


    }
    fun DeleteMember(view: View) {
        val params = HashMap<String,Any>()
        params["comp_id"] = "$COMP_ID" //企业ID
        params["member_id"] = "1W3jeXEBUzpbALEFB4h5slPqc4T" //企业ID
        val json = JSONObject(params as Map<String, Any>)

        OkGo.put<String>(URL_ADD_MEMBER).
             upJson(json).
             execute(object : StringCallback(){
                 override fun onSuccess(response: Response<String>?) {
                     Log.e(TAG, response?.body().toString())

                     response?.let {
                         val json = JSONObject(it.body().toString())
                         val code = json.getInt("code")
                         if (code == Resource.SUCCESS) {
                             Toastinfo("删除成员成功")
//                             tv_result.text = it.body().toString()
//                            var team_list = Gson().fromJson<List<Team>>(json.getJSONArray("data").toString(),
//                                object : TypeToken<List<Team>>(){}.type)
////                                Array<Team>::class.java).toMutableList())
//                            Log.e(TAG,team_list.toString())


                         } else {
                             //TODO
                         }
                     }
                 }
             })

    }


}