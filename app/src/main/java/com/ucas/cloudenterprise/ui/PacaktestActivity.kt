package com.ucas.cloudenterprise.ui

import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.MyApplication
import com.ucas.cloudenterprise.app.ROOT_DIR_PATH
import com.ucas.cloudenterprise.app.USER_ID
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.model.CompletedFile
import com.ucas.cloudenterprise.utils.StatisticCodeLines
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.startService
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
@author simpler
@create 2020年04月15日  16:17
 */
class PacaktestActivity: BaseActivity() {

    val file_path="/storage/emulated/0/年兽大作战BD1280高清国语中英双字.MP4"
    val file_md5="36e57dad718bc03c0aeaaa5310ee7e76"
    val Aeskey="z77yeyLAeyKyPaGktxeVwioqH35UL2HQ1a9bX3THEMMiU"
    override fun GetContentViewId()= R.layout.activity_packtest


    override fun InitView() {


    }

    override fun InitData() {





    }
    var aes =false
    fun pack(view: View) {
        pack(!aes)
    }


    fun pack( aes:Boolean) {
        com.ucas.cloudenterprise.app.checkPermission(this)
//        Executors.newFixedThreadPool(3).execute {
            Thread{
            var destfile = File(file_path)
            val pack_start_time=java.lang.System.currentTimeMillis()
            Log.e("WebSocketClient","${if(aes) "加密" else "不加密"}")
            Log.e("WebSocketClient","start pacak ${pack_start_time}")
            OkGo.post<String>("http://127.0.0.1:9984/api/v0/pack")
                .params("flag", JSONObject().apply {
                    put("MetaHash",file_md5)
                    put("MetaSize",destfile.length())
                    put("Gzip",false)
                    put("Aes",aes)
                    put("AesKey",Aeskey)
                    put("RS",false)
                    put("Feed",true)
                }.toString())
                .params("file", destfile)
                .isMultipart(true)
                .tag(file_md5)
                .execute(object :StringCallback(){
                    override fun uploadProgress(progress: Progress?) {
                        super.uploadProgress(progress)
//                    Log.e("WebSocketClient","uploadProgress $progress ")
                    }
                    override fun onSuccess(response: Response<String>?) {
                        val Multihash=   JSONObject(response?.body().toString()).getJSONObject("Encrypt").getString("Multihash")
                        val pack_end_time=java.lang.System.currentTimeMillis()
                        Log.e("WebSocketClient","end pacak $pack_end_time ")
                        Log.e("WebSocketClient"," pacak 耗时 ${(pack_end_time-pack_start_time)/1000 }")
                            if(aes) {
                                pack(false)
                            }
                    }
//
                })
            }.start()
//        }











    }
    fun unpack(Multihash: String) {
        if(ROOT_DIR_PATH.equals("")){
            ROOT_DIR_PATH= Environment.getExternalStorageDirectory().absolutePath+"/ucas.cloudentErprise.down/$USER_ID"
        }
        val pack_start_time=java.lang.System.currentTimeMillis()
        Log.e("WebSocketClient","start unpacak ${pack_start_time}")
        OkGo.post<File>("http://127.0.0.1:9984/api/v0/unpack")
            .params("hash","Multihash")
            .isMultipart(true)
            .execute(object :
                FileCallback(ROOT_DIR_PATH,"年兽大作战BD1280高清国语中英双字.MP4"){

                override fun downloadProgress(progress: Progress?) {
                    super.downloadProgress(progress)
                    Log.e("ok","文件进度：${progress}")
                }
                override fun onSuccess(response: Response<File>?) {
                    val umpack_end_time=java.lang.System.currentTimeMillis()
                    Log.e("WebSocketClient","end unpacak ${umpack_end_time}")
                    Log.e("WebSocketClient","end unpacak ${(umpack_end_time-pack_start_time)/1000}")

                    Log.e("it","文件路径 ${response?.body()?.absolutePath}")
                    Toastinfo("${"年兽大作战BD1280高清国语中英双字.MP4"} 下载完成")
                }

            })

    }

    fun md5(view: View) {
        Thread{
            var destfile = File(file_path)
            StatisticCodeLines.getFileMD5s(destfile)

            //            val md5_start_time=java.lang.System.currentTimeMillis()
//            Log.e("WebSocketClient","start pacak ${md5_start_time}")

//             val file_md5s=
            StatisticCodeLines.getFileMD5s(destfile,16)


//            val md5_end_time=java.lang.System.currentTimeMillis()
//            Log.e("WebSocketClient","end pacak $md5_end_time ")
//            Log.e("WebSocketClient"," pacak 耗时 ${(md5_end_time-md5_start_time)/1000 }")
//            Log.e("WebSocketClient"," pacak 耗时 ${(md5_end_time-md5_start_time) }")
////            Log.e("WebSocketClient"," file_md5s is ${file_md5s }")
//

        }.start()

    }

    fun getrepo(view: View) {
        myBinder!!.mDaemonService.getrepostat()

    }
    fun getpinlist(view: View) {
        myBinder!!.mDaemonService.getpinlist()

    }
    fun repogc(view: View) {
        myBinder!!.mDaemonService.coregc()
}

    fun peers(view: View) {
        myBinder!!.mDaemonService.peers()
    }

    fun addfile(view: View) {
        val pack_start_time=java.lang.System.currentTimeMillis()
        Log.e("WebSocketClient","start add ${pack_start_time}")
        myBinder!!.mDaemonService.addfile(file_path)

    }
}