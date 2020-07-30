package com.ucas.cloudenterprise.base



import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.LoginActivity
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.ConnectException

/**
@author simpler
@create 2020年01月08日  10:35
 */
  abstract class BaseActivity  : AppCompatActivity(){

    var  myBinder : DaemonService.MyBinder?=  null
     var onNetCallback: OnNetCallback?= null
     var mrequest: Request<String, out Request<Any, Request<*, *>>>?= null
     var mLastRequest: Request<String, out Request<Any, Request<*, *>>>?= null

     var dialog: Dialog?=null
    var NetTag: Any ? =null

    var mBound =false
    val con = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.e("BaseActivity","断开链接")
            mBound =false

        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.e("BaseActivity","链接成功")
            myBinder =p1 as DaemonService.MyBinder
            mBound =true

        }

    }
    override fun onResume() {
        super.onResume()
        bindService(Intent(this,DaemonService::class.java),con,BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if(mBound){
            unbindService(con)
            mBound = false
        }
    }
    var netcallback = object :StringCallback(){
        var  url:String=""
        override fun onFinish() {
            super.onFinish()
                     if (dialog != null && dialog!!.isShowing()) {
                         dialog!!.dismiss()
                     }
        }

        override fun onStart(request: Request<String, out Request<Any, Request<*, *>>>?) {
            super.onStart(request)
            url = request!!.url
            mrequest =request
            Log.e("BaseActivity","request_url=${url}")
                     if (dialog != null && !dialog!!.isShowing()) {
                         dialog!!.show()
                     }

        }


        override fun onError(response: Response<String>?) {
            super.onError(response)
            response?.let {
                Log.e("ok","onError")
                Log.e("ok","response :"+response.toString())
//               TODO网络异常情况   需要添加网络判断
                if(it.exception!=null&&it.exception is ConnectException){
                   Toastinfo("网络链接错误，请检查网络")
                    return
                }
                if(it.rawResponse==null){
                    return
                }
                if(TextUtils.isEmpty(it.rawResponse.toString())){
                    Toastinfo(" 返回 body 为空")
                    return@let
                }
                try {
                    val json = JSONObject(it.rawResponse.toString())
                    if(!json.isNull("code")){
                        when(json.getInt("code")){
                            500->{
                                Toastinfo("服务器错误")
                            }
                            404->{
                                Toastinfo("网络错误")
                            }
                        }

                    }
                }catch (e: JSONException){
                    Toastinfo("未找到路由")
                }



            }

        }

        override fun onSuccess(response: Response<String>?) {
//            Log.e("BaseActivity",response?.body().toString())
            response?.let {
                if(TextUtils.isEmpty(it.body())){
                    Toastinfo(" 返回 body 为空")
                    return@let
                }
                var json = JSONObject(it.body().toString())

                val code = json.getInt("code")
                when(code){
                    REQUEST_SUCCESS_CODE , 4004,  REQUEST_SUCCESS_CODE_NODATA-> //请求成功
                    {

                        if(url.equals(URL_POST_SEND_MESSAGE_CODE)){
                            Toastinfo("${json.getString("message")}")
                            return
                        }
                        Log.e("BaseActivity","请求数据成功")
                       onNetCallback?.OnNetPostSucces(mrequest,json.toString())

                    }

                    REQUEST_NO_TOKEN_CODE ->//token为空
                    {


                        Toastinfo("登陆超时")
                        MyApplication.getInstance().GetSP().edit().putString("access_token","")
                        startActivity<LoginActivity>()
                        finish()
                    }
                    REQUEST_GET_TOKEN_FAIL_CODE ->//获取token失败
                                        {
                                            if(url.equals(URL_LOGIN)){
                                                Toastinfo("${json.getString("message")}")
                                                return
                                            }
                                            Toastinfo(" Get Token Fail")
                                            OkGo.getInstance().commonHeaders.clear()
                                            startActivity<LoginActivity>()
                                            finish()
                                        }
                    REQUEST_ACCESS_TOKEN_FAIL_CODE ->//access_token 失效
                    {
                        Toastinfo(" access_token 失效")
                        mLastRequest =mrequest

                        NetRequest(GET_REFRESH_ACCESS_TOKEN+ REFRESH_TOKEN+ USER_ID, NET_GET,null,NetTag!!,
                            object :BaseActivity.OnNetCallback{
                                override fun OnNetPostSucces(
                                    request: Request<String, out Request<Any, Request<*, *>>>?,
                                    data: String
                                ) {
                                    ACCESS_TOKEN =JSONObject(data).getString("access_token")
                                    MyApplication.context.getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE, Context.MODE_PRIVATE).
                                        edit().
                                        putString("access_token",ACCESS_TOKEN).
                                        commit()

                                    AddToken(ACCESS_TOKEN)
                                    Log.e("base","token  is ${OkGo.getInstance().commonHeaders.get("Authorization")}")
                                    DoLASTREquest(mLastRequest as Request<String, Request<Any, Request<*, *>>>?)
                                }

                            })

                    }

                    REQUEST_NOT_FOUND_CODE ->{
                        Toastinfo("${REQUEST_NOT_FOUND_CODE}服务端找不到相应数据")
                    }

                    REQUEST_REFRESH_TOKEN_FAIL_CODE->{
                        Toastinfo("${REQUEST_REFRESH_TOKEN_FAIL_CODE}${json.getString("message")}  服务端找不到相应数据")
                    }
                    else ->{
                        Toastinfo("${json.getString("message")}")
                        Log.e("BaseActivity","请求数据异常")
                    }

                }

            }


        }



    }

    private fun DoLASTREquest(mLastRequest: Request<String, Request<Any, Request<*, *>>>?) {
        mLastRequest?.execute(netcallback)
    }

    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        StatusBarsettings()
        setContentView(GetContentViewId())
        dialog = Dialog(this).apply {
            setContentView(R.layout.dialog_net_request)
            setCancelable(true)
        }



        InitView()
        InitData()
    }

    abstract  fun  GetContentViewId(): kotlin.Int
    abstract  fun  InitView()
    abstract  fun  InitData()

    //<editor-fold desc=" 添加文件夹  ">
    fun CreateNewDir(dirname: String, iscommon: Boolean,
                     pid:String,
                     tag: Any,
                     onNetCallback:  BaseActivity.OnNetCallback) {
        //TODO 请求创建新的文件夹
        val params = HashMap<String,Any>()
        params["file_name"] = dirname
        params["is_dir"] = IS_DIR
        params["user_id"] = USER_ID //TODO
        params["fidhash"] = ""
        params["state"] = if (iscommon) IS_COMMON_DIR else IS_UNCOMMON_DIR
        params["pid"] = pid
        params["size"] = 0

        NetRequest(URL_ADD_File, NET_POST,params,tag,onNetCallback)

    }
    //</editor-fold>



     fun NetRequest(
        url: String,
        RequestMethod:Int,
        paramsjson: HashMap<String, Any>?,
        tag: Any,
        onNetCallback: OnNetCallback
    ) {

        this.onNetCallback  = onNetCallback
        this.NetTag =tag


        val SecretKey="nD8ZMAqRGsXyUTYmGq2ZRw00LGMWsMof"
        var json  =JSONObject()

//        var stringA  = StringBuffer()
        if(RequestMethod!= NET_GET){
            json  =JSONObject(paramsjson as Map<String, Any>)
            Log.e("ok",json.toString())
            Log.e("ok replace",json.toString().replace("\\",""))
//            if(!url.equals(URL_LOGIN)&&!url.equals(URL_REGISTER_COMPANY)){
//                paramsjson.put("app_id",APP_ID)
//                paramsjson.put("logid",LOGID)
//                paramsjson.put("clienttype",CLIENTTYPE)
            }
//        paramsjson?.toSortedMap()?.forEach { K,V  ->
//            stringA.append("${K}=${V}&")
//
//        }
//        }
//        var s=stringA.deleteCharAt(stringA.length-1)
//
//        var stringSignTemp ="${s}&key=${SecretKey}"
//
//        var sign = MD5encode(stringSignTemp,false).toUpperCase()
//        OkGo.getInstance().apply {
//            addCommonHeaders(HttpHeaders("sign","${sign}"))
//            addCommonHeaders(HttpHeaders("nonce","${AppUtils.getRandomString(16)}"))
//        }
        when(RequestMethod){

            NET_GET->{
                var stringA  = StringBuffer()
                    OkGo.
                    get<String>(url).
                    tag(tag).
                    execute(netcallback)
            }
            NET_POST->{

                OkGo.post<String>(url).
                    upJson(json
//                        .toString().replace("\\","")
                    ).
                    tag(tag).
                    execute(netcallback)
            }
            NET_PUT->{
                OkGo.put<String>(url).
                    upJson(JSONObject(paramsjson as Map<String, Any>)).
                    tag(tag).
                    execute(netcallback)
            }
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        NetTag?.let { OkGo.getInstance().cancelTag(NetTag) }
    }

    private fun StatusBarsettings() {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = ContextCompat.getColor(this,R.color.app_color)
        }
    }


    interface OnNetCallback{
         fun OnNetPostSucces(request:Request<String, out Request<Any, Request<*, *>>>?, data: String)
    }
}



