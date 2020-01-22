package com.ucas.cloudenterprise.core

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_MIN
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.os.Binder
import android.os.Build
import android.os.Build.CPU_ABI
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import  com.ucas.cloudenterprise.utils.*
import com.ucas.cloudenterprise.ui.MainActivity
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.model.File_Bean
import io.ipfs.api.IPFS
import io.ipfs.multiaddr.MultiAddress
import io.ipfs.multihash.Multihash
import java.io.File

class DaemonService : Service() {
        val TAG="DaemonService"
    var mMyBinder :  MyBinder ? =null
    override fun onBind(intent: Intent): IBinder? = mMyBinder
    companion object {
        var daemon: Process? = null
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

        start()
        startForeground(CORE_SERVICE_ID, notification.build())
    }

    fun install() {

        val type = CPU_ABI.let {
            when {
                it.startsWith("arm") -> "arm"
                it.startsWith("x86") -> "386"
                else -> throw Exception("Unsupported ABI")
            }
        }


        AssetFileCP(this,type,bin)
        bin.setExecutable(true)
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
                Log.e("it","it="+it)
                logs.add(it) }
        }

    }

    fun stop() {
        daemon?.destroy()
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
        if(TextUtils.isEmpty(item.fidhash)||item.fidhash==null){
            Toastinfo("该文件hash为空")
            return
        }
            if(daemon!=null&& daemon!!.isAlive){
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
                        val dest  = File(ROOT_DIR_PATH+item.file_name)
                        val fileOutputStream = dest.outputStream()
                        val buffer=ByteArray(1024*4)
                        var sum:Long =0
                        var len =0
                        val off =0

                        try {
                            while (fileInputStream.read(buffer).apply { len =this }>0){
                                fileOutputStream.write(buffer,off,len)
                                sum+=len.toLong()
                                val progression = (sum * 1.0f / item.size * 100 ).toInt()
                                Log.e(TAG,"当前进度为${progression} ")

                                runOnUiThread(){
                                    Toastinfo("当前进度为${progression} ")
                                }
                            }
//                        fileOutputStream.write(fileContents)
                        }finally {
                            fileOutputStream.close()
                        }

                        Log.e("ok","文件写入完毕")
                        runOnUiThread(){
                            //                    tv_text.text =  tv_text.text.toString()+"\n 文件名称：${dest.name}"+"\n"+"filesize=${dest.length()}\nfilepath=${dest.absolutePath}"
                            Toastinfo("${item.file_name} 下载完成")
                        }

                    }

                }).start()
            }else{
                Toastinfo("core服务未启动")
            }



    }

    class MyBinder(var mDaemonService:DaemonService) : Binder() {
        val TAG ="DaemonService.MyBinder"
        fun GetDaemonService():DaemonService{
            return  mDaemonService
        }

    }
}


