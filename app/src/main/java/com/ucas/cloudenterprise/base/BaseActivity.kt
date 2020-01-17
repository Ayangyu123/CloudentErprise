package com.ucas.cloudenterprise.base



import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.model.Resource
import com.ucas.cloudenterprise.utils.Toastinfo
import org.json.JSONObject

/**
@author simpler
@create 2020年01月08日  10:35
 */
  abstract class BaseActivity  : AppCompatActivity(){


     var onNetCallback: OnNetCallback?= null
     var mrequest: Request<String, out Request<Any, Request<*, *>>>?= null
     var mLastRequest: Request<String, out Request<Any, Request<*, *>>>?= null

     var dialog: Dialog?=null
    var NetTag: Any ? =null
   companion object val netcallback = object :StringCallback(){
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


        override fun onSuccess(response: Response<String>?) {
            Log.e("BaseActivity",response?.body().toString())
            response?.let {
                val json = JSONObject(it.body().toString())
                val code = json.getInt("code")
                when(code){
                    REQUEST_SUCCESS_CODE -> //请求成功
                    {
                        Log.e("BaseActivity","请求数据成功")
                        if (json.isNull("data")){
                            Toastinfo("data is null")
                            //TODO
                            return
                        }

                      try {
                          json.getJSONObject("data").let {

                              onNetCallback?.OnNetPostSucces(mrequest,it.toString())

                          }
                      }catch (e:org.json.JSONException){
//
                                  json.getJSONArray("data").let {
                                                                    onNetCallback?.OnNetPostSucces(mrequest,it.toString())}
                              }
                          }
                    REQUEST_NO_TOKEN_CODE ->//token为空
                    {
                        Toastinfo("Token  is null")
                    }
                    REQUEST_GET_TOKEN_FAIL_CODE ->//获取token失败
                                        {
                                            Toastinfo(" Get Token Fail")
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
                        Toastinfo("服务端找不到相应数据")
                    }

//




                    else ->{
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


    fun NetRequest(
        url: String,
        RequestMethod:Int,
        paramsjson: HashMap<String, Any>?,
        tag: Any,
        onNetCallback: OnNetCallback
    ) {
        this.onNetCallback  = onNetCallback
        this.NetTag =tag
        when(RequestMethod){
            NET_GET->{
                    OkGo.
                    get<String>(url).
                    tag(tag).
                    execute(netcallback)
            }
            NET_POST->{
                OkGo.post<String>(url).
                    upJson(JSONObject(paramsjson as Map<String, Any>)).
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
//            window.statusBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.parseColor(APP_COLOR)
        }
    }


    interface OnNetCallback{
         fun OnNetPostSucces(request:Request<String, out Request<Any, Request<*, *>>>?, data: String)
    }
}
