package com.ucas.cloudenterprise.core

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_MIN
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Build.CPU_ABI
import android.os.Build.SUPPORTED_ABIS
import android.os.Environment
import android.os.IBinder
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import  com.ucas.cloudenterprise.utils.*
import com.ucas.cloudenterprise.ui.MainActivity
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.loadclient.DownWebSocketClient
import com.ucas.cloudenterprise.model.*
import com.ucas.cloudenterprise.task.LoadFiileTask
import io.ipfs.api.IPFS
import io.ipfs.multiaddr.MultiAddress
import io.ipfs.multihash.Multihash
import okhttp3.Request
import okhttp3.WebSocketListener
import okio.ByteString
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import kotlin.collections.HashMap

class DaemonService : Service() {
        val TAG="DaemonService"
    var mMyBinder :  MyBinder ? =null
    val  downexecutor = Executors.newFixedThreadPool(3)
    val  upexecutor = Executors.newFixedThreadPool(3)
    var  uptaskmap =HashMap<String,Runnable>()
    var  downtaskmap =HashMap<String,Runnable>()

    val notificationBuilder = NotificationCompat.Builder(this, "sweetipfs")

    val notification
        @SuppressLint("RestrictedApi")
        get() = notificationBuilder.apply {
            mActions.clear()
            setOngoing(true)
            setOnlyAlertOnce(true)
            color = parseColor("#69c4cd")
            setSmallIcon(R.drawable.ic_launcher)
            setShowWhen(false)
            setContentTitle("Sweet Core")

            val open = pendingActivity<MainActivity>()
            setContentIntent(open)
            addAction(R.drawable.ic_launcher, "Open", open)

            if (daemon == null) {
                setContentText("CORE is not running")

                val start = pendingService(intent<DaemonService>().action("start"))
                addAction(R.drawable.ic_launcher, "start", start)
            } else {
                setContentText("CORE is running")

                val restart = pendingService(intent<DaemonService>().action("restart"))
                addAction(R.drawable.ic_launcher, "restart", restart)

                val stop = pendingService(intent<DaemonService>().action("stop"))
                addAction(R.drawable.ic_launcher, "stop", stop)

                val add = pendingService(intent<DaemonService>().action("add"))
                addAction(R.drawable.ic_launcher, "add", add)
            }

            val exit = pendingService(intent<DaemonService>().action("exit"))
            addAction(R.drawable.ic_launcher, "exit", exit)

        }

    override fun onBind(intent: Intent): IBinder? = mMyBinder
    companion object {
        var daemon: Process? = null
        var plugindaemon: Process? = null
        var logs: MutableList<String> = mutableListOf()
    }

    override fun onCreate() {
        super.onCreate()
        mMyBinder = MyBinder(this)
        SUPPORTED_ABIS.forEach {
            Log.e("ok","${it}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel("sweetipfs", "Sweet IPFS", IMPORTANCE_MIN).apply {
                description = "Sweet IPFS"
                getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(this)
            }
            startForeground(1, notification.build())
        }

        if(IS_NOT_INSTALLED){
            install()
        }

        if(!IS_NOT_INSTALLED){
            start()
            startForeground(CORE_SERVICE_ID, notification.build())
        }

    }
    //<editor-fold desc="安装">
    fun install() {

        val type = CPU_ABI.let {
            Log.e("Daemo","cpu is ${it}")
            when {
                it.startsWith("arm") -> "arm"
                it.startsWith("x86") -> "386"  //移除386支持
                else ->throw Exception("Unsupported ABI")

            }
        }
        Log.e("Daemo","type is ${type}")

        if(!assets.list("")!!.contains(type)){
            Toastinfo("不支持该cpu类型")
            stopSelf()
            return
        }


        AssetFileCP(this,type,bin)
        AssetFileCP(this,"arm-plugin",pluginbin)
        bin.setExecutable(true)
        pluginbin.setExecutable(true)
            /*********core bin  复制 完成*******/
        logs.clear()
        exec("init --profile=lowpower").apply { // 低功耗模式
            Log.e(TAG,"init 开始执行")
            read {
                Log.e("it","it="+it)
                logs.add(it) }
            waitFor()
        }



        /*********core init   完成*******/


        AssetFileCP(this,CORE_WORK_PRIVATE_KEY)
        /*********key 复制   完成*******/

        config {
            obj("API").obj("HTTPHeaders").apply {

                array("Access-Control-Allow-Origin").also { origins ->

                    origins.removeAll { true }
                    origins.add(json("*"))
                    origins.add(json("https://sweetipfswebui.netlify.com"))
                    origins.add(json("http://127.0.0.1:5001"))
                }

                array("Access-Control-Allow-Methods").also { methods ->
                    methods.removeAll { true }
                    methods.add(json("PUT"))
                    methods.add(json("GET"))
                    methods.add(json("POST"))
                }
                this.add("Access-Control-Allow-Credentials", json(arrayListOf<String>("true")))


            }
            array("Bootstrap").also {boots ->
                boots.removeAll {true }
                BOOTSTRAPS_ARRAY.forEach {
                    boots.add(json(it))
                }
            }
        }

            /*********core config  复制 完成*******/

        IS_NOT_INSTALLED = false
        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE,Context.MODE_PRIVATE).apply {
            edit().putBoolean(NOT_INSTALLEDE_FOR_PREFERENCE,IS_NOT_INSTALLED).commit()
        }
        /*********core install   完成  写入flag 下次直接执行 star*******/

    }
    //</editor-fold>

    //<editor-fold desc="开启底层服务">
    fun start() {
        if(daemon!=null){
          stop()
        }
        logs.clear()
        exec("daemon").apply {
            daemon = this
            read {
                Log.e("daemonit","it="+it)
                logs.add(it) }
        }
        Runtime.getRuntime().exec(
            "${pluginbin.absolutePath}"

        ).apply {
            plugindaemon = this
            read {
                                Log.e("it"," plugindaemon it="+it)
                logs.add(it) }
        }
           getpluginVersion()


    }
    //</editor-fold>


    //<editor-fold desc="获取插件版本">
    fun getpluginVersion(){
        OkGo.get<String>("http://127.0.0.1:9984/api/v0/version").execute(object :StringCallback(){
            override fun onSuccess(response: Response<String>?) {
                Log.e("it","${response?.body().toString()}")
            }
        })
    }
    //</editor-fold>


    //<editor-fold desc="停止底层服务">
    fun stop() {
        plugindaemon?.destroy()
        daemon?.destroy()
        plugindaemon = null
        daemon = null
    }
    //</editor-fold>

    //<editor-fold desc="停止文件加载">
    fun LoadFileStop(loadfile:LoadingFile) {
        Log.e("ok","LoadFileStop")
        loadfile?.apply{
            when(loadfile.load_type_falg){
                0->{// 上传
                    OkGo.getInstance().cancelTag(loadfile.file_MD5)
                    uptaskmap[loadfile.file_MD5]?.apply {
                        (upexecutor as ThreadPoolExecutor).apply {
                            remove(uptaskmap[loadfile.file_MD5])
                            purge()

                        }

                    }
                    }
                1->{// 下载
                    OkGo.getInstance().cancelTag(loadfile.file_MD5)
                    downtaskmap[loadfile.file_MD5]?.apply {
                        (downexecutor as ThreadPoolExecutor).apply {
                        remove(downtaskmap[loadfile.file_MD5])
                        purge()
                    }
                    }
                }
            }
        }
    }
    //</editor-fold>
    //<editor-fold desc="文件重新加载">
     fun  ReLoadFile(loadfile:LoadingFile){
            when(loadfile.load_type_falg){
                0->{// 上传
                    MyApplication.upLoad_Ing.remove(loadfile)
                      AddFile(loadfile.dest_file!!.absolutePath,loadfile.pid!!)


                }
                1->{ // 下载
                    MyApplication.downLoad_Ing.remove(loadfile)
                        GetFile(loadfile.src_file_info!!)
                }
            }
    }
    //</editor-fold>




    override fun onStartCommand(i: Intent?, f: Int, id: Int) = START_STICKY.also {
        super.onStartCommand(i, f, id)
        Log.e("action=",i?.action+"")
        when (i?.action) {
            "start" -> start()
            "stop" -> stop()
            "restart" -> {
                stop(); start()
            }
            "exit" -> System.exit(0)
            "downFiles" ->{
                Log.e(TAG,"收到files")
                var file = i.getSerializableExtra("file") as File_Bean
                if(file != null){
                    GetFile(file)
                }else{
                    Toastinfo("该文件信息不规范")
                }


            }
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification.build())
    }


    //<editor-fold desc="下载文件">
    fun GetFile(item:File_Bean) {


        daemon?.let {
            if(plugindaemon ==null){
                Toastinfo("pulgDaemon 未启动")
                return
            }
            val task = LoadingFile(1,item.file_name,null,item.fidhash,item.size,src_file_info = item)

            MyApplication.downLoad_Ing.add(task)
            task. Ingstatus =LoadIngStatus.TRANSFERING
            Log.e("ok","item.fidhash=${task.file_hash}")
            object :Runnable{
                override fun run() {
                    var downclient:WebSocketClient? =null

                    downclient=object :WebSocketClient(URI.create("ws://127.0.0.1:9984/api/v0/ws/down"),
                        Draft_6455()
                        ,HashMap<String,String>().apply {put("Origin", "http://www.bejson.com/") }){
                        override fun onOpen(handshakedata: ServerHandshake?) {
                            Log.e("WebSocketClient","onOpen")

                            send(JSONObject(HashMap<String,String>().apply {
                                put("Hash",task.file_hash.toString())
                            }).toString())
                        }

                        override fun onClose(code: Int, reason: String?, remote: Boolean) {
                            Log.e("WebSocketClient","onClose")
                            Log.e("WebSocketClient","reason is ${reason}")
                            Log.e("WebSocketClient","code is ${code}")
                            Log.e("WebSocketClient","remote is ${remote}")



                        }

                        override fun onMessage(message: String?) {
                            Log.e("WebSocketClient","onMessage ${message}")
                            if(task.Ingstatus!=LoadIngStatus.TRANSFERING){
                                close()
                            }
                            if(TextUtils.isEmpty(message)){
                                return
                            }

                            JSONObject(message).apply {
                                task.progress= (getDouble("Percent")*100).toInt()
                                //TODO 速度显示
                                getString("Speed")?.apply {
                                    if(this.toLong()!=0L)
                                        task.Speed=(this.toLong()/1024).toString()+"KB/s"
                                }
                                if(task.progress==100){ //下载完成
                                    if(ROOT_DIR_PATH.equals("")){
                                        ROOT_DIR_PATH= Environment.getExternalStorageDirectory().absolutePath+"/ucas.cloudentErprise.down/${USER_ID}"
                                    }

                                    val root =  File(ROOT_DIR_PATH)
                                    if(!root.exists()){
                                        root.mkdirs()
                                    }
                                    Log.e("ok","destroot="+ ROOT_DIR_PATH.substring(0, ROOT_DIR_PATH.length-2))
                                    task. Ingstatus =LoadIngStatus.UNPACK
                                    OkGo.post<File>("http://127.0.0.1:9984/api/v0/unpack")
                                        .params("hash","${task.file_hash}")
                                        .isMultipart(true)
                                        .execute(object :
                                            FileCallback(ROOT_DIR_PATH,task.file_name){
                                            override fun onSuccess(response: Response<File>?) {

                                                MyApplication.downLoad_completed.add(CompletedFile(task.file_name,SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                                    Date()
                                                )
                                                    ,task.file_size.toString(),
                                                    false))
                                                MyApplication.downLoad_Ing.remove(task)
                                                Log.e("it","文件路径 ${response?.body()?.absolutePath}")
                                                Toastinfo("${task.file_name} 下载完成")
                                            }

                                        })
                                }

                            }

                        }

                        override fun onError(ex: java.lang.Exception?) {
                            Log.e("WebSocketClient","onError")
                        }

                    }
                    downclient.connect()
                }

            }.apply {
                downtaskmap.put("${item.fidhash}",this)
                downexecutor.execute(this)
            }


            }




    }
        //</editor-fold>


    // <editor-fold desc=" 上传文件  ">
    fun AddFile(
        filepath: String,
        pid: String
    ) {
        if (daemon == null ){
            Toastinfo("coredaemon服务未启动")
            Log.e("ok","daemon服务未启动")
            return
        }
        if (plugindaemon == null ){
            Toastinfo("插件服务未启动")
            Log.e("ok","plugindaemon服务未启动")
            return
        }
        val destfile =File(filepath)
        val filemd5= MD5encode(destfile.readBytes())


        val uptask = LoadingFile(0,destfile.name,filemd5,null,destfile.length(),destfile,pid)
        val uploadtask =LoadFiileTask(0,destfile.name,filemd5,null,destfile.length(),destfile,pid)

        upexecutor.execute(uploadtask)
        return

         MyApplication.upLoad_Ing.add(uptask)
        Log.e("ok","准备上传")


        object :Runnable{
            override fun run() {
                println("开始执行")
                Log.e("uptask","开始执行")
                uptask.Ingstatus = LoadIngStatus.CONFIG
                try {

                    var AesKey_adapt = OkGo.get<String>("http://127.0.0.1:9984/api/v0/aes/key/random").tag(filemd5).converter(StringConvert()).adapt()
                    val AesKey_response: Response<String> = AesKey_adapt.execute()
                    val AesKey =  "${AesKey_response?.body()}"
                    Log.e("ok","aeskay = ${AesKey}")

                    var Uploadfile_adapt= OkGo.post<String>("http://127.0.0.1:9984/api/v0/pack")
                        .params("flag",JSONObject().apply {
                            put("MetaHash",uptask.file_MD5)
                            put("MetaSize",uptask.file_size)
                            put("Gzip",true)
                            put("Aes",true)
                            put("AesKey",AesKey)
                            put("RS",true)
                            put("Feed",true)
                        }.toString())
                        .params("file", uptask.dest_file)
                        .isMultipart(true)
                        .tag(filemd5).converter(StringConvert()).adapt()
                    val pack_start_time=java.lang.System.currentTimeMillis()
                    Log.e("WebSocketClient","start pacak ${pack_start_time}")
                    val Uploadfile_response:Response<String> =  Uploadfile_adapt.execute()
                    val Multihash= JSONObject(Uploadfile_response.body().toString()).getJSONObject("Encrypt").getString("Multihash")
                    val pack_end_time=java.lang.System.currentTimeMillis()
                    Log.e("WebSocketClient","end pacak $pack_end_time ")
                    Log.e("WebSocketClient"," pacak 耗时 ${(pack_end_time-pack_start_time)/1000 }")
                    uptask.progress = 50

                    var downclient:WebSocketClient? =null

                    downclient=object :WebSocketClient(URI.create("ws://127.0.0.1:9984/api/v0/ws/up"),
                        Draft_6455()
                        ,HashMap<String,String>().apply {put("Origin", "http://www.bejson.com/") }){
                        override fun onOpen(handshakedata: ServerHandshake?) {
                            Log.e("WebSocketClient","onOpen")
                            send(JSONObject(HashMap<String,String>().apply {
                                put("Hash",Multihash)
                            }).toString())
                        }

                        override fun onClose(code: Int, reason: String?, remote: Boolean) {
                            Log.e("WebSocketClient","onClose")
                            Log.e("WebSocketClient","reason is ${reason}")
                            Log.e("WebSocketClient","code is ${code}")
                            Log.e("WebSocketClient","remote is ${remote}")

                        }

                        override fun onMessage(message: String?) {
                            Log.e("WebSocketClient","onMessage ${message}")
                            Log.e("WebSocketClient","uptask.Ingstatus is  ${uptask.Ingstatus}")
                            if(uptask.Ingstatus!=LoadIngStatus.TRANSFERING){
                                close()
                            }
                            if(TextUtils.isEmpty(message)){
                                return
                            }

                            JSONObject(message).apply {
                                uptask.progress= (getDouble("Percent")*100).toInt()
                                //TODO 速度显示
                                getString("Speed")?.apply {
                                    if(this.toLong()!=0L)
                                        uptask.Speed=(this.toLong()/1024).toString()+"KB/s"
                                }
                                if( uptask.progress==100){
                                    val params = HashMap<String, Any>()
                                    params["file_name"] = uptask.file_name + ""
                                    params["is_dir"] = IS_FILE
                                    params["user_id"] = "$USER_ID" //TODO
                                    params["fidhash"] = "${Multihash}"
                                    params["filehash"] = "${filemd5}"
                                    params["pid"] = pid
                                    params["size"] = destfile.length()
                                    UploadFileMetaInfo(params,filemd5,destfile.name,destfile.length().toString(),1)
                                }

                            }

                        }

                        override fun onError(ex: java.lang.Exception?) {
                            Log.e("WebSocketClient","onError")
                        }

                    }
                    downclient.setConnectionLostTimeout(0)
                    downclient.connect()
                    uptask.Ingstatus =LoadIngStatus.TRANSFERING




                } catch (e:Exception) {
                    e.printStackTrace()
                }

            }

        }.apply {
            uptaskmap.put("${uptaskmap}",this)
            upexecutor.execute(this)
        }

                         //1.获取文件md5值
                         //2.aes加密key
                         //3.pcak
                         //4.transfer
                         //5.提交metainfo




                    }

    //</editor-fold>


    //<editor-fold desc="META信息上传 JSON map">
     fun  GetUploadFileJsonMap(file_name:String,fidhash:String,filemd5:String,pid: String,size:Long):HashMap<String,Any>{
        val params = HashMap<String, Any>()
        params["file_name"] = file_name + ""
        params["is_dir"] = IS_FILE
        params["user_id"] = "${USER_ID}" //TODO
        params["fidhash"] = "${fidhash.apply {
            Log.e("ok","fidhash=${this}")
        }}"
        params["filehash"] = "${filemd5.apply {
            Log.e("ok","fidhash=${this}")
        }}"
        params["pid"] = pid
        params["size"] = size
        return  params
    }

    //</editor-fold>
    //<editor-fold desc="META信息上传">
   fun  UploadFileMetaInfo(params:HashMap<String,Any>,tag:Any,displayName:String,size:String,type:Int){ //type 0 没有调用up  1 从up接口


        OkGo.post<String>(URL_ADD_File).upJson(JSONObject(params))
            .tag(tag)
            .execute(object:StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Toastinfo("${displayName} 上传完成")
//
                    MyApplication.upLoad_completed.add(0,
                        CompletedFile("${displayName}"
                            ,SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                Date()
                            )
                            ,size,
                            false)
                    )

                    for (loadingFile in MyApplication.upLoad_Ing) {
                        if(loadingFile.dest_file?.name.equals(displayName)){
                            MyApplication.upLoad_Ing.remove(loadingFile)
                            break
                        }
                    }

                }

            })
    }

    //</editor-fold>


    class MyBinder(var mDaemonService:DaemonService) : Binder() {
        val TAG ="DaemonService.MyBinder"
        fun GetDaemonService():DaemonService{
            return  mDaemonService
        }

    }


    override fun onDestroy() {
        Log.e("ok"," daemon onDestroy")
        for (loadingFile in MyApplication.downLoad_Ing) {
            loadingFile.Ingstatus = LoadIngStatus.WAITING
        }
        for (loadingFile in MyApplication.upLoad_Ing) {
            loadingFile.Ingstatus = LoadIngStatus.WAITING
        }
        MyApplication.getInstance().GetSP().edit().apply {
         putString("downLoad_Ing",Gson().toJson(MyApplication.downLoad_Ing))
         putString("downLoad_completed",Gson().toJson(MyApplication.downLoad_completed))
         putString("upLoad_Ing",Gson().toJson(MyApplication.upLoad_Ing))
         putString("upLoad_completed",Gson().toJson(MyApplication.upLoad_completed))
            commit()
        }
        Log.e("ok"," daemon onDestroy w sp ok")
        if(downexecutor!=null&&!downexecutor.isShutdown){
            downexecutor.shutdown()
        }
        if(upexecutor!=null&&!upexecutor.isShutdown){
            upexecutor.shutdown()
        }



        super.onDestroy()
    }
}

fun main() {

   val downclient=object :WebSocketClient(URI.create("ws://echo.websocket.org")){
        override fun onOpen(handshakedata: ServerHandshake?) {
            println("onOpen")


            send("test")
            println("onOpen")
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("onClose")
            println("reason is ${reason}")
            println("code is ${code}")
            println("remote is ${remote}")
        }

        override fun onMessage(message: String?) {
            println("onMessage")
            println("onMessage $message")
        }

        override fun onError(ex: java.lang.Exception?) {
            println("onError")
        }

    }.apply {

   }
    downclient.connectBlocking()

}


