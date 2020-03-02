package com.ucas.cloudenterprise.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.COMP_ID
import com.ucas.cloudenterprise.app.MD5encode
import com.ucas.cloudenterprise.app.URL_TEAM
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.Resource
import com.ucas.cloudenterprise.model.Team
import com.ucas.cloudenterprise.utils.AppUtils
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity
import kotlinx.android.synthetic.main.activity_team.*
import kotlinx.android.synthetic.main.activity_team.tv_result
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.security.MessageDigest

/**
@author simpler
@create 2020年01月06日  16:32
 */
class TeamAcitvity : AppCompatActivity() {

    val TAG ="TeamAcitvity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team)
    }

    fun To_AddMemberActiviy(view: View) = startActivity<AddMembersActivity>()

    fun GetTeams(view: View) {
        OkGo.get<String>(URL_TEAM+"/${COMP_ID}").//获取列表
            execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG,response?.body().toString())

                        response?.let {
                            val json = JSONObject(it.body().toString())
                            val code = json.getInt("code")
                            if(code== Resource.SUCCESS){
                                Toastinfo("获取团队成功")
                                tv_result.text = it.body().toString()
                                var team_list = Gson().fromJson<List<Team>>(json.getJSONArray("data").toString(),
                                    object :TypeToken<List<Team>>(){}.type)
//                                Array<Team>::class.java).toMutableList())
                                Log.e(TAG,team_list.toString())



                            }else{
                                //TODO
                            }
                    }

                }

            })
    }
    fun DeleteTeams(view: View) {
//        val team_id = "1578366786532301000-6078024148888105170" //企业ID
        val team_id = et_delete_team.text.toString() //企业ID
        if(team_id.isEmpty( )){
            Toastinfo("要删除的团队ID不能空")
            return
        }

        val params = HashMap<String,Any>()
        params["team_id"] = "$team_id" //企业ID
        val json = JSONObject(params as Map<String, Any>)
        OkGo.put<String>(URL_TEAM).//获取列表
            upJson(json).
            execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG,response?.body().toString())
                    response?.let {



                        val json = JSONObject(it.body().toString())
                        val code = json.getInt("code")
                        if(code== Resource.SUCCESS){
                            Toastinfo("删除团队成功")
//                            tv_result.text = it.body().toString()



                        }else{
                            //TODO
                        }
                    }

                }

            })

    }
    fun AddTeams(view: View) {
        val params = HashMap<String,Any>()
        params["company_id"] = "$COMP_ID" //企业ID
        params["team_name"] = getResources().getString(R.string.team_test) //企业ID
        params["team_level"] = "1578366198548880000-3045533124602177801" //企业ID
        val json = JSONObject(params as Map<String, Any>)
        OkGo.post<String>(URL_TEAM).
            upJson(json).
            execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG,response?.body().toString())
                    response?.let {



                        val json = JSONObject(it.body().toString())
                        val code = json.getInt("code")
                        if(code== Resource.SUCCESS){
                            Toastinfo("添加团队成功")
                            tv_result.text = it.body().toString()
                            var team = Gson().fromJson(json.getJSONObject("data").toString(),
                                Team::class.java)
                            Log.e(TAG,team.toString())



                        }else{
                            //TODO
                        }
                    }


                }



            })

    }

    fun testimp(view: View) {


        var nonce = "${AppUtils.getRandomString(32)}"
        var s="file_id=borsd7ir3udq387hksvg&nonce=${nonce}&user_id=225212075364847616"

        val SecretKey="nD8ZMAqRGsXyUTYmGq2ZRw00LGMWsMof"
        var stringSignTemp ="${s}&key=${SecretKey}"
        Log.e("ok","${stringSignTemp}")

        var sign = MD5encode(stringSignTemp,false).toUpperCase()

        var jsonsrc ="{\n" +
                " \"user_id\":\"225212075364847616\",\n" +
                " \"file_id\":\"borsd7ir3udq387hksvg\",\n" +
                " \"nonce\":\"${nonce}\"\n" +
                "}"
            OkGo.post<String>(" http://39.106.216.189:6016/api/cloud/v1/enterprise/share/create").
                headers("app_id","250528").
                headers("logid","MTU4MjAxNjAyODE2NDAuODkyMjA2MDk1MDk2OTc3OQ").
                headers("clienttype","1").
                headers("nonce","${nonce}").
                headers("sign","${sign}").
                upJson(JSONObject(jsonsrc)).
                execute(object : StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        Log.e("okgo","${response?.body().toString()}")
                    }
                })


    }


}

fun main(args: Array<String>) = try {


var file = File("/Users/simple/Desktop/test.txt")
//获取md5加密对象
val instance: MessageDigest = MessageDigest.getInstance("MD5")
val digest:ByteArray = instance.digest(file.readBytes())
var sb : StringBuffer = StringBuffer()
for (b in digest) {
    //获取低八位有效值
    var i :Int = b.toInt() and 0xff
    //将整数转化为16进制
    var hexString = Integer.toHexString(i)
    if (hexString.length < 2) {
        //如果是一位的话，补0
        hexString = "0" + hexString
    }
    sb.append(hexString)
}
    print(sb.toString())
}catch (e:Exception){
    print(e.message)
}