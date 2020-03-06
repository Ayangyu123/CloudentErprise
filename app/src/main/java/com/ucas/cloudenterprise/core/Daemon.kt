package com.ucas.cloudenterprise.core

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_MIN
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color.parseColor
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Build.CPU_ABI
import android.os.IBinder
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ThemedSpinnerAdapter
import androidx.core.app.NotificationCompat
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import  com.ucas.cloudenterprise.utils.*
import com.ucas.cloudenterprise.ui.MainActivity
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.model.DownLoadIngFile
import com.ucas.cloudenterprise.model.DownLoadIngFileState
import com.ucas.cloudenterprise.model.File_Bean
import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multiaddr.MultiAddress
import io.ipfs.multihash.Multihash
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.collections.HashMap
import kotlin.concurrent.timer

class DaemonService : Service() {
        val TAG="DaemonService"
    var mMyBinder :  MyBinder ? =null
    val  diskIO = Executors.newSingleThreadExecutor()
    override fun onBind(intent: Intent): IBinder? = mMyBinder
    companion object {
        var daemon: Process? = null
        var plugindaemon: Process? = null
        var logs: MutableList<String> = mutableListOf()
    }

    override fun onCreate() {
        super.onCreate()
        mMyBinder = MyBinder(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationChannel("sweetipfs", "Sweet IPFS", IMPORTANCE_MIN).apply {
                description = "Sweet IPFS"
                getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(this)
            }


        if(IS_NOT_INSTALLED){
            install()

        }

        if(!IS_NOT_INSTALLED){
            start()
            startForeground(CORE_SERVICE_ID, notification.build())
        }

    }

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
        exec("init").apply {
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


        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE,Context.MODE_PRIVATE).apply {
            edit().putBoolean(NOT_INSTALLEDE_FOR_PREFERENCE,false).commit()
        }
        /*********core install   完成  写入flag 下次直接执行 star*******/
    }

    fun start() {
        logs.clear()
        exec("daemon").apply {
            daemon = this
            read {
                Log.e("daemonit","it="+it)
                logs.add(it) }
        }
        Runtime.getRuntime().exec(
            "${pluginbin.absolutePath}"
//            ,
//            arrayOf(String(Base64.getDecoder().decode(CORE_PATH), StandardCharsets.UTF_8 )+"=${store.absolutePath}")//此处字符串为环境变量

        ).apply {
            plugindaemon = this
            read {
                                Log.e("it"," plugindaemon it="+it)
                logs.add(it) }
        }
            OkGo.get<String>("http://127.0.0.1:9984/api/v0/version").execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e("it","${response?.body().toString()}")
                }
            })


    }

    fun stop() {
        plugindaemon?.destroy()
        daemon?.destroy()
        plugindaemon = null
        daemon = null
    }

    fun add(){
      if(daemon!=null){
          exec("add "+store["cid.txt"].absolutePath).apply{
              daemon =this
              read {
                  Log.e("it","it="+it)
                  logs.add(it) }

          }

      }
    }

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

    override fun onStartCommand(i: Intent?, f: Int, id: Int) = START_STICKY.also {
        super.onStartCommand(i, f, id)
        Log.e("action=",i?.action+"")
        when (i?.action) {
            "start" -> start()
            "stop" -> stop()
            "add" -> add()
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

    fun GetFile(item:File_Bean) {
//        diskIO.execute {  }

        daemon?.let {
            Thread(object:Runnable{
                override fun run() {


                    var ipfs = IPFS( MultiAddress(CORE_CLIENT_ADDRESS))
                    var filePointer = Multihash.fromBase58(item.fidhash)
//                    var fileContents = ipfs.cat(filePointer)
                    var fileInputStream = ipfs.catStream(filePointer)
                    val root =  File(ROOT_DIR_PATH)
                    if(!root.exists()){
                        root.mkdirs()
                    }
//                    val dest  = File(ROOT_DIR_PATH+System.currentTimeMillis())
                    var item_downing =DownLoadIngFile(
                        file_id = System.currentTimeMillis().toString(),
                        isDir = IS_FILE,
                        file_name = item.file_name,
                        file_size =  item.size.toString(),
                        down_size = "0",
                        state = DownLoadIngFileState.DOWNLOADING
                    )
                    MyApplication.downLoad_Ing.add(item_downing)
                    val dest  = File(ROOT_DIR_PATH+item.file_name)
                    val fileOutputStream = dest.outputStream()
                    val buffer=ByteArray(1024*4)
                    var sum:Long =0
                    var len =0
                    val off =0

                    try {
                        while (fileInputStream.read(buffer).apply { len =this }>0){
//                            fileOutputStream.write(buffer,off,len)
                            sum+=len.toLong()
                            val progression = (sum * 1.0f / item.size * 100 ).toInt()
                        }
                    }finally {
                        fileOutputStream.close()
                    }



                    runOnUiThread(){
                        Log.e("ok","文件写入完毕")
                        OkGo.post<File>("http://127.0.0.1:9984/api/v0/down")
                            .params("hash","${item.fidhash}")
                            .isMultipart(true)
                            .execute(object :FileCallback(){
                            override fun onSuccess(response: Response<File>?) {
                                Log.e("it","文件路径 ${response?.body()?.absolutePath}")
                            }

                        })
                    Toastinfo("${item.file_name} 下载完成")
                    }

                }

            }).start()
        }
    }


    //<editor-fold desc=" 添加文件  ">
    fun AddFile(
        uri: String,
        pid: String,
        tag: Any,
        onNetCallback: BaseActivity.OnNetCallback
    ) {
        val uri = Uri.parse(uri) // 获取用户选择文件的URI
        Log.e("uri)", "data.getData()=" + uri)
        Log.e("uri)", "uri.getScheme()=" + uri.getScheme())
        Log.e("uri)", "uri.authority=" + uri.authority)
        val inputsteam= contentResolver.openInputStream(uri)
        val filemd5= MD5encode(inputsteam.readBytes())
       OkGo.get<String>("${URL_ADD_File_CHECK}filemd5")
           .execute(object:StringCallback(){
               override fun onSuccess(response: Response<String>?) {
                   Log.e("ok",response?.body().toString())

                    if(JSONObject(response?.body().toString()).getInt("code")== REQUEST_SUCCESS_CODE_NODATA){
                        val cursor = contentResolver.query(uri, null, null, null, null, null)
                        var displayName: String? = null
                        var size =0
                        cursor?.use {
                            if (it.moveToFirst()) {
                                displayName =
                                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                size =
                                    it.getInt(it.getColumnIndex(OpenableColumns.SIZE))


                            }
                        }
                        if (daemon != null ) {//&& daemon!!.isAlive
                            if(plugindaemon!=null){//&& plugindaemon!!.isAlive
                                OkGo.get<String>("http://127.0.0.1:9984/api/v0/aes/key/random")
                                    .execute(object:StringCallback(){
                                        override fun onSuccess(response: Response<String>?) {
                                            Log.e("ok","aeskey =${response?.body()}")


                                            var AesKey =  "${response?.body()}"

                                             File(displayName).apply {
                                                writeBytes(inputsteam.readBytes()
                                                )}

                                            OkGo.post<String>("http://127.0.0.1:9984/api/v0/up")
                                                .params("flag",JSONObject().apply {
                                                    put("MetaHash",filemd5)
                                                    put("MetaSize",size)
                                                    put("Gzip",true)
                                                    put("Aes",true)
                                                    put("AesKey",AesKey)
                                                    put("RS",true)
                                                    put("Feed",true)
                                                }.toString())
                                                .params("file",File(""))
                                                .isMultipart(true)
                                                .execute(object :StringCallback(){
                                                    override fun onSuccess(response: Response<String>?) {
                                                        Log.e("it","文件路径 ${response?.body()?.toString()}")
                                                    }

                                                })
                                        }
                                    })
                                return


                            }else{
                                Log.e("ok","plugindaemon服务未启动")
                            }



                            return
                            Thread(Runnable {
                                val ipfs = IPFS(CORE_CLIENT_ADDRESS)
                                val file = NamedStreamable.InputStreamWrapper(inputsteam)
                                val addResult = ipfs.add(file)[0]
                                Log.e("ifileContents", "addResult=" + addResult)

                                runOnUiThread() {
                                    addResult.apply {
                                        val params = HashMap<String, Any>()
                                        params["file_name"] = displayName + ""
                                        params["is_dir"] = IS_FILE
                                        params["user_id"] = "${USER_ID}" //TODO
                                        params["fidhash"] = "${hash}"
                                        params["pid"] = pid
                                        params["size"] = size
                                        OkGo.post<String>(URL_ADD_File).upJson(JSONObject(params))
                                            .tag(tag)
                                            .execute(object:StringCallback(){
                                                override fun onSuccess(response: Response<String>?) {
                                                    Toastinfo("${displayName} 上传完成")
                                                }

                                            })


                                    }
                                }
                            }).start()
                        } else {
                            Log.e("ok","daemon服务未启动")
                            Toastinfo("coredaemon服务未启动")
                        }


                    }



               }

           })
        //TODO

    }
    //</editor-fold>

    class MyBinder(var mDaemonService:DaemonService) : Binder() {
        val TAG ="DaemonService.MyBinder"
        fun GetDaemonService():DaemonService{
            return  mDaemonService
        }

    }
}

fun main() {

    Thread{
        run {
            val srcfile=File("/Users/simple/Desktop/7.zip")
            var destfile = File("/Users/simple/Desktop/testzip")
            var  total:Long=srcfile.length()
            println("srcfile size =${total}")

            var counttimer:Timer ?= null
            if(destfile.exists()){
                destfile.delete()
            }
            destfile.createNewFile()
//            var fileOutputStream = destfile.outputStream()
            var fileInputStream = srcfile.inputStream()
            var buffer=ByteArray(1024*4)
            var last_size:Long =0
            var sum:Long =0
            var len =0
            val off =0
            var last_progression =0
            var progression =0

            try {
                while(fileInputStream.read(buffer).apply { len =this }>0){
//                        Thread.sleep(10)
//                    fileOutputStream.write(buffer,off,len)
                    sum+=len.toLong()
                     progression = (sum * 1.0 / total * 100 ).toInt()
                    if(counttimer==null){
                        counttimer= Timer()

                        counttimer.scheduleAtFixedRate(object :TimerTask(){
                            override fun run() {

                                println("当前时间${SimpleDateFormat("HH:mm:ss").format(Date())}")
                                println("当前size${sum}")
                                println("last_size 前一秒size ${last_size}")
                                println("当前速度 ${sum-last_size}")
                                last_size =sum
                                if(progression!=last_progression){
                                println("当前进度为${progression}")
                                last_progression =progression
                                }
                                if(sum==total){
                                    print("下载完成")
                                    this.cancel()
                                    counttimer =null

                                }
                            }
                        },0,1000)
                    }
//                    println("当前进度为${progression} ")

                }
//
            }finally {
                counttimer=null
//                fileOutputStream.close()
            }

//            while(curr!=total){
//                if(counttimer==null){
//                    counttimer= Timer()
//
//                    counttimer.scheduleAtFixedRate(object :TimerTask(){
//                        override fun run() {
//                            if(curr!=total){
//
//                                println("${curr}")
//                            }else{
//                                print("")
//                                this.cancel()
//                            }
//                        }
//                    },0,10)
//                }
//                curr += 1
//                if(curr==total){
//                println(curr)
//                counttimer=null
//                }
//
//            }
        }
    }.start()

}


