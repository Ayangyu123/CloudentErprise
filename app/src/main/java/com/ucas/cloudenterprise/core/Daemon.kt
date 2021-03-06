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
import android.os.Build.SUPPORTED_ABIS
import android.os.IBinder
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.google.gson.Gson
import com.lzy.okgo.BuildConfig

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request

import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.event.MessageEvent
import com.ucas.cloudenterprise.model.*
import com.ucas.cloudenterprise.ui.ChooseDestDirActivity
import com.ucas.cloudenterprise.ui.MainActivity
import com.ucas.cloudenterprise.ui.fragment.TransferlistItemFragment
import com.ucas.cloudenterprise.utils.*
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import kotlin.collections.HashMap
import kotlin.math.log
import com.ucas.cloudenterprise.model.UnionNodes
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import kotlin.collections.ArrayList


private const val URL =
    "http://47.95.145.45:6015/storemeta/v1/union_file_meta/5fc452df73caa4244000004c"

fun stratDaemonService(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(Intent(context, DaemonService::class.java))
    } else {
        context.startService<DaemonService>()
    }
}

class DaemonService : Service() {
    val TAG = "DaemonService"
    var mMyBinder: MyBinder? = null
    val downexecutor = Executors.newFixedThreadPool(3)
    val upexecutor = Executors.newFixedThreadPool(3)
    var uptaskmap = HashMap<String, Runnable>()
    var downtaskmap = HashMap<String, Runnable>()
    var arraycompbean: ArrayList<CompositeFilebean>? = null
    var DaemonClosed = false
    var DaemonStartsed = false


    val notificationBuilder = NotificationCompat.Builder(this, "tuxingyun")
    // private lateinit var viewModel: DemoViewModel

    val notification
        @SuppressLint("RestrictedApi")
        get() = notificationBuilder.apply {
            mActions.clear()
            setOngoing(true)
            setOnlyAlertOnce(true)
            color = parseColor("#69c4cd")
            setSmallIcon(R.drawable.ic_launcher)
            setShowWhen(false)
            setContentTitle("")
            //TODO splah页面
            val open = pendingActivity<MainActivity>()
            setContentIntent(open)
            //            addAction(R.drawable.ic_launcher, "Open", open)


            if (daemon == null) {
                setContentText("土星云企业网盘正在运行")
                //
                //                val start = pendingService(intent<DaemonService>().action("start"))
                //                addAction(R.drawable.ic_launcher, "start", start)
            } else {
                setContentText("土星云企业网盘正在运行")


                //
                //                val restart = pendingService(intent<DaemonService>().action("restart"))
                //                addAction(R.drawable.ic_launcher, "restart", restart)

                //                val stop = pendingService(intent<DaemonService>().action("repo stat"))
                //                addAction(R.drawable.ic_launcher, "repo stat", stop)
                //
                //                val stop = pendingService(intent<DaemonService>().action("repo stat"))
                //                addAction(R.drawable.ic_launcher, "repo stat", stop)
                //

                //                val stop = pendingService(intent<DaemonService>().action("stop"))
                //                addAction(R.drawable.ic_launcher, "stop", stop)
                //
                //                val add = pendingService(intent<DaemonService>().action("add"))
                //                addAction(R.drawable.ic_launcher, "add", add)
            }

            //            val exit = pendingService(intent<DaemonService>().action("exit"))
            //            addAction(R.drawable.ic_launcher, "exit", exit)

        }

    override fun onBind(intent: Intent): IBinder? = mMyBinder

    companion object {
        var daemon: Process? = null
        var plugindaemon: Process? = null
        var logs: MutableList<String> = mutableListOf()
        val DOWNLOADING = 1   //向下
        val UPLOADING = 2     //向上
        val DOWNLOADCOMPLETED = 3
        val UPLOADCOMPLETED = 4
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        mMyBinder = MyBinder(this)
        SUPPORTED_ABIS.forEach {
            Log.e("ok", "${it}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel("tuxingyun", "tuxingyun", IMPORTANCE_MIN).apply {
                description = "tuxingyun"
                getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(this)
            }
            startForeground(1, notification.build())
        }


        if (IS_NOT_INSTALLED || (BuildConfig.VERSION_CODE != APP_LAST_VERSION_CODE)) {
            install()
        }

        if (!IS_NOT_INSTALLED) {
            start()
            startForeground(CORE_SERVICE_ID, notification.build())
        }

    }

    //<editor-fold desc="安装">
    fun install() {
        Log.e("ok", "开始安装")
        val start_install_time = System.currentTimeMillis()
        val type = CPU_ABI.let {
            Log.e("Daemo", "cpu is ${it}")
            when {
                it.startsWith("arm") -> "arm"
                it.startsWith("x86") -> "386"  //移除386支持
                else -> throw Exception("Unsupported ABI")

            }
        }
        Log.e("Daemo", "type is ${type}")

        if (!assets.list("").contains(type)) {
            Toastinfo("不支持该cpu类型")
            stopSelf()
            return
        }


        AssetFileCP(this, type, bin)
        AssetFileCP(this, "arm-plugin", pluginbin)
        bin.setExecutable(true)
        pluginbin.setExecutable(true)
        /*********core bin  复制 完成*******/
        logs.clear()
        //        init --profile=
        exec("init --profile=badgerds,lowpower").apply { // 低功耗模式
            //        exec("init --profile=lowpower").apply { // 低功耗模式
            Log.e(TAG, "init 开始执行")
            read {
                Log.e("it", "it=" + it)
                logs.add(it)
            }
            waitFor()
        }
        /*********core init   完成*******/
        AssetFileCP(this, CORE_WORK_PRIVATE_KEY)//复制私钥

        //<editor-fold desc="pligin 配置文件写入">

        store["plugin.config"].apply {  //写入plugin配置

            if (!exists()) {
                createNewFile()
                writeBytes(JSONObject().apply {
                    //                    var  HOST = "39.106.216.189"
                    put("WEB_SERVER", "${HOST}:6015")
                    put("NODE_SERVER", "${HOST}:6016")
                    put("MAX_ROUTINE_NUM", 20)
                    put("MAX_ROUTINE_NUM_2", 10)
                    put("LOG_ENABLE", false)
                    put("MAGIC_PLANT", true) //加速模式
                    put("MAGIC_PLANT_PRE", false) //预下载

                }.toString().toByteArray())
            }

        }
        //</editor-fold >
        /*********key 复制   完成*******/
        // ipfs config Addresses.API /ip4/0.0.0.0/tcp/5001
        //ipfs config Addresses.Gateway /ip4/0.0.0.0/tcp/8080

        config {
            obj("Datastore").add("StorageMax", json("10GB"))
            //            obj("Addresses").add("","")
            obj("API").obj("HTTPHeaders").apply {
                array("Access-Control-Allow-Origin").also { origins ->
                    origins.removeAll { true }
                    origins.add(json("*"))
                    //                    origins.add(json("https://sweetipfswebui.netlify.com"))
                    //                    origins.add(json("http://127.0.0.1:5001"))
                }
                array("Access-Control-Allow-Methods").also { methods ->
                    methods.removeAll { true }
                    methods.add(json("PUT"))
                    methods.add(json("GET"))
                    methods.add(json("POST"))
                }
                this.add("Access-Control-Allow-Credentials", json(arrayListOf<String>("true")))
            }
            array("Bootstrap").also { boots ->
                boots.removeAll { true }
                BOOTSTRAPS_ARRAY.forEach {
                    boots.add(json(it))
                }
            }
        }

        Log.e("ok", "结束安装")
        val end_install_time = System.currentTimeMillis()
        Log.e("ok", "安装耗时${(end_install_time - start_install_time) / 1000.0}秒")

        /*********core config  复制 完成*******/

        IS_NOT_INSTALLED = false
        getSharedPreferences(PREFERENCE__NAME__FOR_PREFERENCE, Context.MODE_PRIVATE).apply {
            edit()
                .putBoolean(NOT_INSTALLEDE_FOR_PREFERENCE, IS_NOT_INSTALLED)
                .putInt("APP_LAST_VERSION_CODE", BuildConfig.VERSION_CODE)
                .commit()
        }
        /*********core install   完成  写入flag 下次直接执行 star*******/
        //</editor-fold>
    }

    //<editor-fold desc="开启底层服务">
    @RequiresApi(Build.VERSION_CODES.O)
    fun start() {
        if (daemon != null) {
            stop()
        }
        logs.clear()
        exec("daemon").apply {
            daemon = this
            read {

                Log.e("daemonit", it)
                if (it.equals("Daemon is ready")) {
                    DaemonStartsed = true
                    DaemonClosed = false
                }
                logs.add(it)
            }
        }
        Runtime.getRuntime().exec(
            "${pluginbin.absolutePath}",
            arrayOf(
                String(
                    Base64.getDecoder().decode(CORE_PATH),
                    StandardCharsets.UTF_8
                ) + "=${store.absolutePath}"
            )

        ).apply {
            plugindaemon = this
            read {
                Log.e("plugindaemon", it)
                logs.add(it)
            }
        }


        getpluginVersion()


    }
    //</editor-fold>


    //<editor-fold desc="获取插件版本">
    fun getpluginVersion() {
        OkGo.get<String>("http://127.0.0.1:9984/api/v0/version").execute(object : StringCallback() {
            override fun onSuccess(response: Response<String>?) {
                Log.e("it", "${response?.body().toString()}")
            }
        })
    }

    //</editor-fold>


    //<editor-fold desc="停止底层服务">

    fun stop() {
        for (it in MyApplication.downLoad_Ing) {
            it.Ingstatus = LoadIngStatus.WAITING
        }
        for (it in MyApplication.upLoad_Ing) {
            it.Ingstatus = LoadIngStatus.WAITING
        }
        plugindaemon?.destroy()
        daemon?.destroy()
        plugindaemon = null
        daemon = null
        DaemonStartsed = false
        DaemonClosed = true
    }

    //</editor-fold>
    //---------------------------------------------
    //<editor-fold desc="停止文件加载">
    fun LoadFileStop(loadfile: LoadingFile) {
        Log.e("ok", "LoadFileStop")
        loadfile?.apply {
            OkGo.getInstance().cancelTag(loadfile.file_MD5)
            if (loadfile.webSocketClient != null && loadfile.webSocketClient!!.isOpen) {
                loadfile.webSocketClient!!.close()
            }
            when (loadfile.load_type_falg) {
                //上传（停止文件）
                TransferlistItemFragment.UPLOAD -> {// 上传
                    //                    OkGo.getInstance().cancelTag(loadfile.file_MD5)
                    uptaskmap[loadfile.file_MD5]?.apply {
                        (upexecutor as ThreadPoolExecutor).apply {
                            remove(uptaskmap[loadfile.file_MD5])
                            purge()

                        }
                    }
                }
                //下载（停止文件）
                TransferlistItemFragment.DOWNLOAD -> {// 下载
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

    //---------------------------------------------
    //</editor-fold>
    //<editor-fold desc="文件重新加载">
    fun ReLoadFile(uptask: LoadingFile) {
        when (uptask.load_type_falg) {
            TransferlistItemFragment.UPLOAD -> {// 上传
                //                    MyApplication.upLoad_Ing.remove(loadfile)
                AddFile(uptask)
            }
            TransferlistItemFragment.DOWNLOAD -> { // 下载
                MyApplication.downLoad_Ing.remove(uptask)
                GetFile(uptask.src_file_info!!) //重新下载的时候走的还是  普通下载
            }
        }
    }

    //</editor-fold>
    fun clearcache() {
        stop()
        store.get("badgerds")
        start()
    }

    override fun onStartCommand(i: Intent?, f: Int, id: Int) = START_STICKY.also {
        super.onStartCommand(i, f, id)
        Log.e("action=", i?.action + "")
        when (i?.action) {
            "start" -> start()
            "stop" -> stop()
            "repo stat" -> getrepostat()
            "restart" -> {
                stop()
                start()
            }
            "exit" -> {
                stopSelf()
                System.exit(0)
            }
            "downFiles" -> {
                Log.e(TAG, "收到files")
                var file = i.getSerializableExtra("file") as File_Bean
                if (file != null) {
                    GetFile(file)
                } else {
                    Toastinfo("该文件信息不规范")
                }
            }
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification.build())
    }

    //<editor-fold desc="下载文件">
    fun GetFile(item: File_Bean) {
        //获取下载文件时需要的条件  task
        var task = LoadingFile(
            TransferlistItemFragment.DOWNLOAD,
            //需要下载的条目name
            item.file_name,
            //需要下载的条目MD5
            null,
            //需要下载的条目hash 值（文件只有一个hash值,复合文件有多个hash值 到时候做具体的判断-）
            item.fidhash,
            //需要下载的条目的大小 （复合文件一般都特别大）
            item.size,
            //File_Bean 类型的对象
            src_file_info = item
        )
        //因为文件夹不支持下载所以 能下载的只有文件和复合文件
        if (item.is_dir != -1) { //文件对应的dir值为-1  文件夹对应的dir值为1(文件夹没有下载功能) 复合文件对应的dir值为2
            //如果这里的dir值不等于-1  只能等于2将进行对复合文件的下载
            GetFile_xiazai_Compositefile(task, item.file_id)
            Log.e("ceshi", "文件类型-1是文件，-1以外是复合文件:" + item.is_dir)
        } else {
            //如果说等于dir值为-1 将进行对文件的下载(普通下载)
            GetFile_xiazai(task)
            Log.e("ceshi", "文件类型-1是文件，-1以外是复合文件:" + item.is_dir)
        }
    }

    //普通文件下载1
    /*  fun GetFile_xiazai(task: LoadingFile) {
          //打印实体类
          Log.e("ceshi", "（普通）LoadingFile:" + task)
          //不知道daemon和plugindaemon是什么但是正常下载后没有不良反应
          if (daemon == null) {
              Toastinfo("daemon 未启动")
              return
          }
          if (plugindaemon == null) {
              Toastinfo("pulgDaemon 未启动")
              return
          }
          //如果说downLoad_Ing返回的类型 不包含task返回的类型将进行添加task类型(downLoad_Ing和task 都是LoadingFile类型)
          if (!MyApplication.downLoad_Ing.contains(task)) {
              //如果说不等于 LoadingFile类型 将task添加到LoadingFile类型当中
              MyApplication.downLoad_Ing.add(task)
              Log.e("ceshi", "普通文件下载")
          }
          //LoadIngStatus.TRANSFERING(传输)  赋值给-task中的Ingstatus（等待）
          //好比如把一个值传给一空个值 那么这个空值将有了新的值  便不是空值
          //把2 赋值 给了 0  task.Ingstatus等于2
          task.Ingstatus = LoadIngStatus.TRANSFERING
          //调用sp(SharedPreferences)进行存储  DOWNLOADING == 1
          savaspbyfalag(DOWNLOADING)
          //打印普通文件的一个hash值
          Log.e("ok", "item.fidhash=${task.file_hash}")
          Log.e("ceshi", "(普通)item.fidhash=${task.file_hash}")
          object : Runnable {  //创建Runnable
              override fun run() { //实现他的方法 run函数
                  //定义一个websock  et 的一个对象可以为空
                  var downclient: WebSocketClient? = null
                  //进行对websocket进行赋值
                  //websocket 走的三个方法  1.Uri 2. Draft方法 3.一个HashMap
                  downclient =
                      object : WebSocketClient(URI.create("ws://127.0.0.1:9984/api/v0/ws/down"),
                          Draft_6455(),
                          HashMap<String, String>().apply {
                              put("Origin", "http://www.bejs on.com/")
                          }) {
                          //握手
                          override fun onOpen(handshakedata: ServerHandshake?) {
                              Log.e("WebSocketClient", "onOpen")
                              send(JSONObject(HashMap<String, String>().apply {
                                  put("Hash", task.file_hash!!)
                              }).toString())

                              //把下载目录转换成file类型的变量root
                              val root = File(getRootPath())
                              Log.e("ceshi", "root（普通）:" + root)
                              if (!root.exists()) {
                                  root.mkdirs()
                              }
                              var destfile = File("${root}/${task.file_name}")
                              Log.e("ceshi", "destfile（普通）:" + destfile)
                              if (destfile.exists()) {
                                  destfile.delete()
                              }
                              //OkGo.getInstance().okHttpClient=10000
                              //使用post 请求进行对下载的文件进行操作
                              OkGo.post<File>("http://127.0.0.1:9984/api/v0/unpack")
                                  .params("hash", "${task.file_hash}")
                                  .retryCount(3)
                                  .isMultipart(true)
                                  .execute(object : FileCallback(ROOT_DIR_PATH, task.file_name) {
                                      override fun downloadProgress(progress: Progress?) {
                                          super.downloadProgress(progress)
                                          Log.e("ceshi", "（普通文件）progress:" + progress)
                                          //获取下载进度
                                          var downloadprogress =
                                              ((progress!!.currentSize * 1.0f / task.file_size) * 100).toInt()
                                          Log.e("ceshi", "下载进度:" + downloadprogress)
                                          //判断下载进度
                                          if (downloadprogress > task.file_progress) {
                                              task.file_progress = downloadprogress
                                          }
                                          val fl =
                                              (progress!!.currentSize * 1.0f / task.file_size) * 100
                                          Log.e("ok123", "${fl}")
                                          Log.e("ceshi", "下载进度:" + fl)
                                          //TODO 速度显示
                                          progress!!.speed.apply {
                                              if (this != 0L) {
                                                  task.Speed = Formatter.formatFileSize(
                                                      applicationContext,
                                                      this
                                                  ).toUpperCase() + "/s"
                                              }
                                          }
                                          Log.e("ok", "文件下载进度：${downloadprogress}")
                                          Log.e("ceshi", "文件下载进度：${downloadprogress}")
                                      }

                                      //错误
                                      override fun onError(response: Response<File>?) {
                                          super.onError(response)
                                          Toastinfo("服务器未获取该文件到相关信息")
                                          Log.e("123", "服务器未获取该文件到相关信息")
                                          Log.e("ok", "code=" + response?.code())
                                          Log.e("ceshi", "(普通文件)code=" + response?.code())
                                      }

                                      //结束
                                      override fun onFinish() {
                                          super.onFinish()
                                          task.Ingstatus = LoadIngStatus.WAITING
                                          LoadFileStop(task)
                                          MyApplication.downLoad_Ing.remove(task)
                                          savaspbyfalag(DOWNLOADING)
                                          Log.e("ceshi", "普通文件onFinish")
                                      }

                                      //成功
                                      override fun onSuccess(response: Response<File>?) {
                                          Log.e("ceshi", "OnSuccess")
                                          MyApplication.downLoad_completed.add(
                                              0, CompletedFile(
                                                  task.file_name,
                                                  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                                      Date()
                                                  ), task.file_size.toString(), false
                                              )
                                          )
                                          savaspbyfalag(DOWNLOADCOMPLETED)
                                          Log.e("it", "文件路径 ${response?.body()?.absolutePath}")
                                          Log.e("ceshi",
                                              "（文件）文件路径: ${response?.body()?.absolutePath}"
                                          )
                                          Log.e("ceshi", "文件下载完成!!")
                                          Toastinfo("${task.file_name} 下载完成!!")
                                      }
                                  })
                          }

                          //关闭
                          override fun onClose(code: Int, reason: String?, remote: Boolean) {
                              Log.e("WebSocketClient", "onClose")
                              Log.e("WebSocketClient", "reason is ${reason}")
                              Log.e("WebSocketClient", "code is ${code}")
                              Log.e("WebSocketClient", "remote is ${remote}")

                              if (!remote) { //remote  默认为true   当它不等于true的时候进行关闭
                                  Log.e("AAA", "我是Daemon里面的onClose1")
                              }
                              Log.e("AAA", "我是Daemon里面的onClose2")
                              Log.e("123", "onSuccess")
                          }

                          //接收消息
                          override fun onMessage(message: String?) {
                              Log.e("WebSocketClient", "onMessage:${message}")
                              if (task.Ingstatus != LoadIngStatus.TRANSFERING) {
                                  close()
                              }
                              if (TextUtils.isEmpty(message)) {
                                  return
                              }
                              JSONObject(message).apply {
                                  var downloadprogress = (getDouble("Percent") * 100).toInt()
                                  Log.e("ceshi", "(普通文件)downloadprogress:" + downloadprogress)
                                  if (downloadprogress > task.progress) {
                                      task.progress = downloadprogress
                                      Log.e("ceshi", "(普通文件)task.progress:" + task.progress)
                                  }
                                  //Log.e("ok","${ (progress!!.currentSize * 1.0f / task.file_size)*100}")
                                  //TODO 速度显示
                                  getLong("Speed").apply {
                                      if (this != 0L)
                                          task.Speed = Formatter.formatFileSize(
                                              applicationContext,
                                              this
                                          ).toUpperCase() + "/s"
                                  }
                              }
                          }

                          //异常错误
                          override fun onError(ex: java.lang.Exception?) {
                              Log.e("WebSocketClient", "onError")
                              Log.e("WebSocketClient", "ex is ${ex.toString()}")
                          }
                      }
                  Log.e("ceshi", "(普通)downclient:" + downclient)
                  downclient.setConnectionLostTimeout(0)
                  downclient.connect()
              }
          }.apply {
              downtaskmap.put("${task.file_hash}", this)
              downexecutor.execute(this)
          }
      }*/
    //普通文件下载2
    fun GetFile_xiazai(task: LoadingFile) {
        //打印实体类
        Log.e("ceshi", "（普通）LoadingFile:" + task)
        if (daemon == null) {
            Toastinfo("daemon 未启动")
            return
        }
        if (plugindaemon == null) {
            Toastinfo("pulgDaemon 未启动")
            return
        }
        if (!MyApplication.downLoad_Ing.contains(task)) {
            MyApplication.downLoad_Ing.add(task)
            Log.e("ceshi", "普通文件下载")
        }
        task.Ingstatus = LoadIngStatus.TRANSFERING
        savaspbyfalag(DOWNLOADING)
        //打印普通文件的一个hash值
        Log.e("ok", "item.fidhash=${task.file_hash}")
        Log.e("ceshi", "(普通)item.fidhash=${task.file_hash}")
        object : Runnable {
            override fun run() {
                var downclient: WebSocketClient? = null
                downclient =
                    object : WebSocketClient(URI.create("ws://127.0.0.1:9984/api/v0/ws/down"),
                        Draft_6455(),
                        HashMap<String, String>().apply {
                            put("Origin", "http://www.bejson.com/")
                        }) {
                        //握手
                        override fun onOpen(handshakedata: ServerHandshake?) {
                            Log.e("WebSocketClient", "onOpen")
                            send(JSONObject(HashMap<String, String>().apply {
                                put("Hash", task.file_hash!!)
                            }).toString())

                            val root = File(getRootPath())
                            if (!root.exists()) {
                                root.mkdirs()
                            }
                            var destfile = File("${root}/${task.file_name}")
                            if (destfile.exists()) {
                                destfile.delete()
                            }
                            Log.e("ceshi", "猜一猜:" + task.file_hash)
                            OkGo.post<File>("http://127.0.0.1:9984/api/v0/unpack")
                                .params("hash", "${task.file_hash}")
                                .retryCount(3)
                                .isMultipart(true)
                                .execute(object : FileCallback(ROOT_DIR_PATH, task.file_name) {
                                    override fun downloadProgress(progress: Progress?) {
                                        super.downloadProgress(progress)
                                        Log.e("ceshi", "（普通文件）progress:" + progress)
                                        var downloadprogress =
                                            ((progress!!.currentSize * 1.0f / task.file_size) * 100).toInt()
                                        Log.e("ceshi", "（普通文件）downloadprogress:" + downloadprogress)
                                        if (downloadprogress > task.file_progress) {
                                            task.file_progress = downloadprogress
                                        }
                                        val fl =
                                            (progress!!.currentSize * 1.0f / task.file_size) * 100
                                        Log.e("ceshi", "（普通文件）fl:" + fl)
                                        Log.e("ceshi", "${fl}")
                                        //TODO 速度显示
                                        progress!!.speed.apply {
                                            if (this != 0L)
                                                task.Speed = Formatter.formatFileSize(
                                                    applicationContext,
                                                    this
                                                ).toUpperCase() + "/s"
                                        }
                                        Log.e("ceshi", "文件下载进度：${downloadprogress}")
                                    }

                                    override fun onError(response: Response<File>?) {
                                        super.onError(response)
                                        Toastinfo("服务器未获取该文件到相关信息")
                                        Log.e("ceshi", "code=" + response?.code())
                                        Log.e("ceshi", "服务器未获取该文件到相关信息")
                                    }

                                    override fun onFinish() {
                                        super.onFinish()
                                        task.Ingstatus = LoadIngStatus.WAITING
                                        LoadFileStop(task)
                                        MyApplication.downLoad_Ing.remove(task)
                                        savaspbyfalag(DOWNLOADING)
                                        Log.e("ceshi", "onFinish")
                                    }

                                    override fun onSuccess(response: Response<File>?) {
                                        Log.e("ceshi", "onSuccess:" + response.toString())
                                        //把下载的文件添加到列表中进行展示
                                        MyApplication.downLoad_completed.add(
                                            0, CompletedFile(
                                                task.file_name,
                                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                                                    Date()
                                                ), task.file_size.toString(), false
                                            )
                                        )
                                        savaspbyfalag(DOWNLOADCOMPLETED)
                                        Log.e("ceshi", "文件路径 ${response?.body()?.absolutePath}")
                                        Toastinfo("${task.file_name} 下载完成!!")
                                        Log.e("kk", "onSuccess执行了")
                                        Log.e("ceshi", "下载完成!!")
                                        Log.e("kk", "onSuccess关闭了")
                                    }
                                })
                        }

                        //关闭
                        override fun onClose(code: Int, reason: String?, remote: Boolean) {
                            Log.e("WebSocketClient", "onClose")
                            Log.e("WebSocketClient", "reason is ${reason}")
                            Log.e("WebSocketClient", "code is ${code}")
                            Log.e("WebSocketClient", "remote is ${remote}")
                            if (!remote) { //remote  默认为true   当它不等于true的时候进行关闭
                                Log.e("AAA", "我是Daemon里面的onClose1")
                            }
                            Log.e("AAA", "我是Daemon里面的onClose2")
                        }

                        //接收消息
                        override fun onMessage(message: String?) {
                            Log.e("ceshi", "onMessage:" + message)
                            Log.e("WebSocketClient", "onMessage ${message}")
                            if (task.Ingstatus != LoadIngStatus.TRANSFERING) {
                                close()
                            }
                            if (TextUtils.isEmpty(message)) {
                                return
                            }
                            JSONObject(message).apply {
                                //下载进度
                                var downloadprogress = (getDouble("Percent") * 100).toInt()
                                if (downloadprogress > task.progress) {
                                    task.progress = downloadprogress
                                }
//                           Log.e("ok","${ (progress!!.currentSize * 1.0f / task.file_size)*100}")
                                //TODO 速度显示
                                getLong("Speed").apply {
                                    if (this != 0L) {
                                        task.Speed = Formatter.formatFileSize(
                                            applicationContext,
                                            this
                                        ).toUpperCase() + "/s"
                                    }
                                }
                            }
                        }

                        //异常错误
                        override fun onError(ex: java.lang.Exception?) {
                            Log.e("WebSocketClient", "onError")
                            Log.e("WebSocketClient", "ex is ${ex.toString()}")
                        }
                    }
                downclient.setConnectionLostTimeout(0)
                downclient.connect()
            }

        }.apply {
            downtaskmap.put("${task.file_hash}", this)
            downexecutor.execute(this)
        }
    }

    //复合文件下载
    //（目前状态时   获取好了单个的一个hash值  现在要用每个hash值去走websocket 当所有值返回1的时候走merge请求）
    fun GetFile_xiazai_Compositefile(task: LoadingFile, compid: String) {
        if (daemon == null) {
            Toastinfo("daemon 未启动")
            return
        }
        if (plugindaemon == null) {
            Toastinfo("pulgDaemon 未启动")
            return
        }
        //如果说downLoad_Ing返回的类型 不包含task返回的类型将进行添加task类型(downLoad_Ing和task 都是LoadingFile类型)
        if (!MyApplication.downLoad_Ing.contains(task)) {
            //如果说不等于 LoadingFile类型 将task添加到LoadingFile类型当中
            MyApplication.downLoad_Ing.add(task)
            Log.e("ceshi", "复合文件下载")
            //提示打印
            Log.e("ceshi", "task 和 downLoad_file 是同一个类型")
        }
        //默认是等待  然后LoadIngStatus.TRANSFERING是传输
        task.Ingstatus = LoadIngStatus.TRANSFERING
        //调用sp(SharedPreferences)进行存储  DOWNLOADING == 1
        //调用sp存储
        savaspbyfalag(DOWNLOADING)
        //打印网址
        Log.e("ceshi", "打印网址1:" + Composite_File_URLb)
        Log.e("ceshi", "打印网址2:$Composite_File_URLa$compid")
        //使用get 请求进行对下载的复合文件进行操作(获取HAsh值)
        OkGo.get<String>(Composite_File_URLa + compid)
            .execute(object : StringCallback(),
                com.lzy.okgo.callback.Callback<String> {
                override fun onSuccess(response: Response<String>?) {
                    var toString = response!!.body().toString()
                    Log.e("ceshi", "response：" + toString)
                    var gson = Gson()
                    var fromJson = gson.fromJson(toString, CompositeFile::class.java)
                    Log.e("ceshi", "看一下1:" + fromJson)
                    var dataNodes = fromJson.data.UnionNodes.DataNodes
                    var array =CompositeFilebean(task.file_name, task.file_size.toString(), dataNodes)
                    Log.e("ceshi", "复合文件Hash个数:" + dataNodes!!.size)
                    Log.e("ceshi", "赋值后的name:" + task.file_name)
                    Log.e("ceshi", "赋值后的totalSize:" + task.file_size.toString())
                    Log.e("ceshi", "赋值后的Bean:" + array)
                    Log.e("ceshi", "复合文件大小:" + task.file_size.toString())
                    Log.e("ceshi", "赋值后的对象:" + dataNodes)
                    //循环打印获取hash值
                    var count = 0// 用于给进度进行计数
                    var Countresults = 0// 结果
                    for (i in 0 until dataNodes.size) {
                        //获取的文件名字  文件大小   文件的多个Hash 拼接到CompositeFileBean实体类中
                        Log.e("ceshi", "第" + i + "个Hash:" + dataNodes[i])
                        Log.e("ceshi", "---------------------------------------------")
                        object : Runnable {
                            override fun run() {
                                //定义一个websocket 的一个对象可以为空
                                var downclient: WebSocketClient? = null
                                //进行对websocket进行赋值
                                //websocket 走的三个方法  1.Uri 2. Draft方法 3.一个HashMap
                                downclient = object :
                                    WebSocketClient(URI.create("ws://127.0.0.1:9984/api/v0/ws/down"),
                                        Draft_6455(),
                                        HashMap<String, String>().apply {
                                            put("Origin", "http://www.bejson.com/")
                                        }) {
                                    //握手
                                    override fun onOpen(handshakedata: ServerHandshake?) {
                                        Log.e("WebSocketClient", "onOpen")
                                        send(JSONObject(HashMap<String, String>().apply {
                                            put("Hash", dataNodes[i]!!)
                                        }).toString())
                                        Log.e("ceshi",
                                            "send:" + JSONObject(HashMap<String, String>().apply {
                                                put("Hash", dataNodes[i])
                                            }).toString()
                                        )
                                        //把下载目录转换成file类型的变量root
                                        val root = File(getRootPath())
                                        Log.e("ceshi", "root（复合）:" + root)
                                        if (!root.exists()) {
                                            root.mkdirs()
                                        }
                                        var destfile = File("${root}/${task.file_name}")
                                        Log.e("ceshi", "destfile（复合下载目录）:" + destfile)
                                        if (destfile.exists()) {
                                            destfile.delete()
                                        }
                                        //OkGo.getInstance().okHttpClient=10000
                                        /*
                                        * 复合文件流程:
                                        * 1.首先复合文件和普通文件的区别在于普通文件只有一个Hash，复合文件有多个Hash
                                        * 2.下载文件同时采用webSocket进行下载
                                        * 3.复合文件和普通文件都采用Post请求（单:hash->哈希值，多:data->哈希值数组，返回的参数都是json串对象）
                                        * 4.获取多个Hash值对应的进度  然后获取进度的平均值进行判断    当等于100的时候进行显示下载完的数据，并退出下载
                                        * 5.目前卡在了无法获取进度  无法获取平均值   downloadProgress 方法不执行
                                        * * */
                                        //使用post 请求进行对下载的复合文件进行操作
                                        Log.e("ceshi", "Yy1:${JSONObject().apply { array }}")
                                        Log.e("ceshi",
                                            "Yy2:" + JSONObject().apply { array }.toString()
                                        )
                                        Log.e("ceshi", "Yy3:" + array)
                                        Log.e("ceshi", "Yy4:" + array.Files)
                                        var toJson = Gson().toJson(array)
                                        Log.e("ceshi", "Yy5:" + toJson)
                                        Log.e("ceshi","Yy6:" + JSONObject().apply { toJson }.toString())
                                        Log.e("ceshi", "Yy7:" + "${toJson}")
                                        OkGo.post<File>("http://127.0.0.1:9984/api/v0/merge")
                                            .tag(this)
                                            .params("data",JSONObject().apply { array }.toString())
                                            .isMultipart(true)
                                            .execute(object :FileCallback(ROOT_DIR_PATH, task.file_name) {
                                                //获取进度并 取平均值
                                                override fun downloadProgress(progress: Progress?) {
                                                    super.downloadProgress(progress)
                                                    Log.e("ceshi", "downloadProgress")
                                                    Log.e("ceshi", "（复合文件）progress:" + progress)
                                                    var downloadprogress =
                                                        ((progress!!.currentSize * 1.0f / task.file_size) * 100).toInt()
                                                    Log.e("ceshi","（复合文件）downloadprogress:" + downloadprogress)
                                                    count += downloadprogress
                                                    Log.e("ceshi", "复合count:" + count)
                                                    Countresults = count / dataNodes.size
                                                    Log.e("ceshi", "复合文件平均值:" + Countresults)
                                                    if (Countresults > task.file_progress) {
                                                        task.file_progress = Countresults
                                                    }
                                                    val fl =
                                                        (progress!!.currentSize * 1.0f / task.file_size) * 100
                                                    Log.e("ceshi", "（复合文件）fl:" + fl)
                                                    //TODO 速度显示
                                                    progress!!.speed.apply {
                                                        if (this != 0L)
                                                            task.Speed = Formatter.formatFileSize(
                                                                applicationContext,
                                                                this
                                                            ).toUpperCase() + "/s"
                                                    }
                                                    Log.e("ceshi", "复合文件下载进度：${Countresults}")
                                                }

                                                //网络延迟不好的时候会走  并展示code值
                                                override fun onError(response: Response<File>?) {
                                                    super.onError(response)
                                                    Toastinfo("复合服务器未获取该文件到相关信息")
                                                    Log.e("ceshi", "复合code=" + response?.code())
                                                    Log.e("ceshi", "复合服务器未获取该文件到相关信息")
                                                }

                                                //当平均值等于100的时候进行终止并销毁进度
                                                override fun onFinish() {
                                                    super.onFinish()
                                                    if (Countresults == 100) {
                                                        task.Ingstatus = LoadIngStatus.WAITING
                                                        LoadFileStop(task)
                                                        MyApplication.downLoad_Ing.remove(task)
                                                        savaspbyfalag(DOWNLOADING)
                                                        Log.e("ceshi", "onFinish")
                                                    } else {
                                                        Log.e(
                                                            "ceshi",
                                                            "onFinish(Countresults):" + Countresults
                                                        )
                                                    }
                                                }

                                                //当平均值等于100的时候进行把下载好的数据展示到列表上
                                                override fun onSuccess(response: Response<File>?) {
                                                    Log.e("ceshi","复合onSuccess:" + response.toString())
                                                    Log.e("ceshi",
                                                        "onSuccessJSONObject:" + JSONObject().apply { array }
                                                            .toString())
                                                    //把下载的文件添加到列表中进行展示
                                                    if (Countresults == 100) {
                                                        MyApplication.downLoad_completed.add(
                                                            0, CompletedFile(
                                                                task.file_name,
                                                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                                    .format(Date()),
                                                                task.file_size.toString(),
                                                                false
                                                            )
                                                        )
                                                        savaspbyfalag(DOWNLOADCOMPLETED)
                                                        Log.e("ceshi","复合文件路径 ${response?.body()?.absolutePath}"
                                                        )
                                                        Toastinfo("${task.file_name} 下载完成!!")
                                                        Log.e("kk", "onSuccess执行了")
                                                        Log.e("ceshi", "复合文件下载完成!!")
                                                        Log.e("kk", "onSuccess关闭了")
                                                    } else {
                                                        Log.e(
                                                            "ceshi",
                                                            "onSuccess进度没有到100没有添加到展示列表:" + Countresults
                                                        )
                                                    }
                                                }
                                            })
                                    }

                                    //关闭
                                    override fun onClose(
                                        code: Int,
                                        reason: String?,
                                        remote: Boolean
                                    ) {
                                        Log.e("WebSocketClient", "onClose")
                                        Log.e("WebSocketClient", "reason is ${reason}")
                                        Log.e("WebSocketClient", "code is ${code}")
                                        Log.e("WebSocketClient", "remote is ${remote}")
                                        if (!remote) { //remote  默认为true   当它不等于true的时候进行关闭
                                            Log.e("AAA", "我是Daemon里面的onClose1")
                                        }
                                        Log.e("AAA", "我是Daemon里面的onClose2")
                                        Log.e("ceshi", "onSuccess")
                                    }

                                    //接收
                                    override fun onMessage(message: String?) {
                                        Log.e("ceshi", "onMessage:${message}")
                                        if (task.Ingstatus != LoadIngStatus.TRANSFERING) {
                                            close()
                                        }
                                        if (TextUtils.isEmpty(message)) {
                                            return
                                        }
                                        JSONObject(message).apply {
                                            var downloadprogress =
                                                (getDouble("Percent") * 100).toInt()
                                            /*   Log.e("ceshi", "前downloadprogress:" + downloadprogress)
                                               Log.e("ceshi",
                                                   "(复合文件)downloadprogress:" + dataNodes[i] + ":" + downloadprogress
                                               )
                                               Log.e(
                                                   "yangyua",
                                                   "(复合文件)downloadprogress:" + dataNodes[i] + ":" + downloadprogress
                                               )*/
                                            count += downloadprogress
                                            var size = dataNodes.size
                                            Countresults = count / size
                                            Log.e("ceshi", "后webSocker的平均值:" + Countresults)
                                            if (Countresults == 100) {
                                                Log.e("ceshi",
                                                    "(onMessage)(count / size):" + (count / size)
                                                )
                                                Log.e("ceshi",
                                                    "(onMessage)Countresults:" + Countresults
                                                )
                                                Log.e("ceshi", "task.progress等于100:" + Countresults)
                                                if (Countresults > task.progress) {
                                                    task.progress = Countresults
                                                    Log.e(
                                                        "ceshi",
                                                        "(复合文件)task.progress:" + task.progress
                                                    )
                                                }
                                                //TODO 速度显示
                                                getLong("Speed").apply {
                                                    if (this != 0L)
                                                        task.Speed = Formatter.formatFileSize(
                                                            applicationContext,
                                                            this
                                                        ).toUpperCase() + "/s"
                                                }
                                            } else {
                                                Log.e("ceshi","(onMessage)Countresults不等于100:" + task.progress
                                               )
                                            }
                                        }
                                    }

                                    //异常错误
                                    override fun onError(ex: java.lang.Exception?) {
                                        Log.e("WebSocketClient", "onError")
                                        Log.e("WebSocketClient", "ex is ${ex.toString()}")
                                    }
                                }
                                Log.e("ceshi", "(复合文件)downclient:" + downclient)
                                downclient.setConnectionLostTimeout(0)
                                downclient.connect()
                            }
                        }.apply {
                            downtaskmap.put("${dataNodes[i]}", this)
                            downexecutor.execute(this)
                        }
                    }
                }
            })
    }

    //</editor-fold>
    // <editor-fold desc=" 上传文件  ">
    fun AddFile(filePath: String, pid: String?) {
        var destfile = File(filePath)
        var uptask = LoadingFile(
            TransferlistItemFragment.UPLOAD,
            destfile.name,
            null,
            null,
            destfile.length(),
            destfile,
            pid = pid,
            Ingstatus = LoadIngStatus.WAITING
        )
        AddFile(uptask)
    }

    //拍照上传部分   多加了一个更改名字后的参数
    fun AddFile(filePath: String, pid: String?, recomposename: String) {
        var destfile = File(filePath)
        var uptask = LoadingFile(
            TransferlistItemFragment.UPLOAD,
            recomposename,  //名字更改成 随机输入的名字
            null,
            null,
            destfile.length(),
            destfile,
            pid = pid,
            Ingstatus = LoadIngStatus.WAITING
        )
        AddFile(uptask)
    }

    fun AddFile(uptask: LoadingFile) {
        if (daemon == null) {
            Toastinfo("coredaemon服务未启动")
            Log.e("ok", "daemon服务未启动")
            return
        }
        if (plugindaemon == null) {
            Toastinfo("插件服务未启动")
            Log.e("ok", "plugindaemon服务未启动")
            return
        }

        MyApplication.upLoad_Ing.apply {
            if (!contains(uptask)) {
                add(uptask)
                savaspbyfalag(UPLOADING)
                Log.e("ok", "准备上传")
            }
        }

        object : Runnable {
            override fun run() {
                println("开始执行")
                Log.e("uptask", "开始执行")
                uptask.Ingstatus = LoadIngStatus.CONFIG
                try {
                    if (TextUtils.isEmpty(uptask.Aes_key)) {
                        //判断AES_KEY 如果为空就获取
                        // if(TextUtils.isEmpty(uptask.file_MD5)){
                        // Log.e("ok","上传文件 file_MD5 为空")
                        // return
                        //}

                        var AesKey_adapt =
                            OkGo.get<String>("http://127.0.0.1:9984/api/v0/aes/key/random")
                                .tag(uptask.file_MD5).converter(StringConvert()).adapt()
                        val AesKey_response: Response<String> = AesKey_adapt.execute()
                        val AesKey = "${AesKey_response?.body()}"
                        Log.e("ok", "aeskay = ${AesKey}")
                        uptask.Aes_key = AesKey
                        savaspbyfalag(UPLOADING)
                    }

                    if (TextUtils.isEmpty(uptask.file_MD5)) {  //判断MD5 如果为空就进行md5加密
                        Log.e(" uptask.Aes_key", " uptask.Aes_key")
                        // if(uptask.Aes_key.length<33){
                        // uptask.Aes_key
                        // }
                        uptask.file_MD5 = uptask.Aes_key!!.substring(0, 32)

                        // Log.e("ok","start file md5")
                        // val  start_time =System.currentTimeMillis()
                        // var file_md5=""
                        // val destfile_length =uptask.file_size
                        // val Memory = getMemory()
                        //if(destfile_length>(Memory)){
                        //
                        //file_md5= StatisticCodeLines.getFileMD5s(uptask.dest_file)
                        // }else{
                        //   file_md5  =MD5encode(uptask.dest_file!!.readBytes())
                        //  }
                        // uptask.file_MD5 =file_md5
                        savaspbyfalag(UPLOADING)
                        //                        val  end_time =System.currentTimeMillis()
                        //                        Log.e("ok","file_md5 is ${file_md5}")
                        //                        Log.e("ok","md5 time  is ${(end_time-start_time).toDouble()
                        //                                /1000
                        //                        }")
                    }

                    if (!uptask.hasPacked) {
                        if (TextUtils.isEmpty(uptask.Aes_key)) {

                            Log.e("ok", "上传文件 AesKey 为空")
                            return
                        }
                        //                        OkGo.post<String>("http://127.0.0.1:9984/api/v0/pack")
                        //                            .params("flag", JSONObject().apply {
                        //                                put("MetaHash", uptask.file_MD5)
                        //                                put("MetaSize", uptask.file_size)
                        //                                put("Gzip", false)
                        //                                put("Aes", true)
                        //                                put("AesKey", uptask.Aes_key)
                        //                                put("RS", false)
                        //                                put("Feed", false)
                        //                            }.toString())
                        //                            .params("file", uptask.dest_file)
                        //                            .isMultipart(true)
                        //                            .tag(uptask.file_MD5).execute(object : StringCallback() {
                        //                                var pack_start_time: Long = 0
                        //                                override fun onStart(request: Request<String, out Request<Any, Request<*, *>>>?) {
                        //                                    super.onStart(request)
                        //                                    pack_start_time = java.lang.System.currentTimeMillis()
                        //                                    Log.e("WebSocketClient", "start pacak ${pack_start_time}")
                        //                                }
                        //
                        //                                override fun uploadProgress(progress: Progress?) {
                        //                                    super.uploadProgress(progress)
                        //                                    Log.e("ok", "文件上传进度：${progress}")
                        //                                }
                        //
                        //                                override fun onSuccess(response: Response<String>?) {
                        //
                        //                                    Log.e(
                        //                                        "ok",
                        //                                        "Uploadfile_response =" + response?.body().toString()
                        //                                    )
                        //                                    val Multihash = JSONObject(
                        //                                        response?.body().toString()
                        //                                    ).getJSONObject("Encrypt").getString("Multihash")
                        //                                    val pack_end_time = java.lang.System.currentTimeMillis()
                        //                                    Log.e("WebSocketClient", "end pacak $pack_end_time ")
                        //                                    Log.e(
                        //                                        "WebSocketClient",
                        //                                        " pacak 耗时 ${(pack_end_time - pack_start_time) / 1000}"
                        //                                    )
                        ////                    addpinlist(Multihash) //固定切片
                        //                                    uptask.file_hash = Multihash
                        //                                    uptask.hasPacked = true
                        //                                    savaspbyfalag(UPLOADING)
                        //                                    AddFile(uptask)
                        //                                }
                        //
                        //                            })
                        //
                        //                        return


                        var Uploadfile_adapt =
                            OkGo.post<String>("http://127.0.0.1:9984/api/v0/pack")
                                .params("flag", JSONObject().apply {
                                    put("MetaHash", uptask.file_MD5)
                                    put("MetaSize", uptask.file_size)
                                    put("Gzip", false)
                                    put("Aes", true)
                                    put("AesKey", uptask.Aes_key)
                                    put("RS", false)
                                    put("Feed", false)
                                }.toString())
                                .params("file", uptask.dest_file)
                                .isMultipart(true)
                                .tag(uptask.file_MD5).converter(StringConvert()).adapt()
                        val pack_start_time = java.lang.System.currentTimeMillis()
                        Log.e("WebSocketClient", "start pacak ${pack_start_time}")
                        val Uploadfile_response: Response<String> = Uploadfile_adapt.execute()
                        Log.e("ok", "Uploadfile_response =" + Uploadfile_response.toString())
                        val Multihash = JSONObject(
                            Uploadfile_response.body().toString()
                        ).getJSONObject("Encrypt").getString("Multihash")
                        val pack_end_time = java.lang.System.currentTimeMillis()
                        Log.e("WebSocketClient", "end pacak $pack_end_time ")
                        Log.e(
                            "WebSocketClient",
                            " pacak 耗时 ${(pack_end_time - pack_start_time) / 1000}"
                        )
                        //                    addpinlist(Multihash) //固定切片
                        uptask.file_hash = Multihash
                        uptask.hasPacked = true
                        savaspbyfalag(UPLOADING)
                    }


                    if (!uptask.hasTransfer) {
                        if (TextUtils.isEmpty(uptask.file_hash)) {

                            Log.e("ok", "上传文件 filehash 为空")
                            return
                        }
                        uptask.webSocketClient =
                            object :
                                WebSocketClient(URI.create("ws://127.0.0.1:9984/api/v0/ws/up"),
                                    Draft_6455()
                                    ,
                                    HashMap<String, String>().apply {
                                        put(
                                            "Origin",
                                            "http://www.bejson.com/"
                                        )
                                    }) {
                                override fun onOpen(handshakedata: ServerHandshake?) {
                                    Log.e("WebSocketClient", "onOpen")
                                    send(JSONObject(HashMap<String, String>().apply {
                                        put("Hash", uptask.file_hash!!)
                                    }).toString())
                                }

                                override fun onClose(
                                    code: Int,
                                    reason: String?,
                                    remote: Boolean
                                ) {
                                    Log.e("WebSocketClient", "onClose")
                                    Log.e("WebSocketClient", "reason is ${reason}")
                                    Log.e("WebSocketClient", "code is ${code}")
                                    Log.e("WebSocketClient", "remote is ${remote}")

                                }

                                override fun onMessage(message: String?) {
                                    Log.e("WebSocketClient", "onMessage ${message}")
                                    //                                    Log.e("WebSocketClient","uptask.Ingstatus is  ${uptask.Ingstatus}")

                                    if (uptask.Ingstatus != LoadIngStatus.TRANSFERING) {
                                        close()
                                    }
                                    if (TextUtils.isEmpty(message)) {
                                        return
                                    }

                                    JSONObject(message).apply {
                                        var uploadprogress =
                                            (getDouble("Percent") * 100).toInt()
                                        if (uploadprogress > uptask.progress) {
                                            uptask.progress = uploadprogress
                                        }
                                        //TODO 速度显示
                                        getString("Speed")?.apply {
                                            if (this.toLong() != 0L)
                                                uptask.Speed = Formatter.formatFileSize(
                                                    applicationContext,
                                                    this.toLong()
                                                ).toUpperCase() + "/s"

                                        }
                                        if (uptask.progress == 100 && uptask.hasTransfer == false) {
                                            uptask.hasTransfer = true
                                            savaspbyfalag(UPLOADING)
                                            val params = HashMap<String, Any>()
                                            params["file_name"] = uptask.file_name + ""
                                            params["is_dir"] = IS_FILE
                                            params["user_id"] = "$USER_ID" //TODO
                                            params["fidhash"] = "${uptask.file_hash}"
                                            params["file_hash"] = "${uptask.file_MD5}"
                                            params["pid"] = uptask.pid!!
                                            params["size"] = uptask.file_size
                                            UploadFileMetaInfo(
                                                params,
                                                uptask.file_MD5!!,
                                                uptask.file_name!!,
                                                uptask.file_size.toString(),
                                                uptask.file_hash!!
                                            )
                                        }

                                    }
                                }
                                override fun onError(e: java.lang.Exception?) {
                                    Log.e("WebSocketClient", "onError")
                                    Log.e("WebSocketClient", "onError ${e}")
                                }
                            }
                        uptask.webSocketClient!!.setConnectionLostTimeout(0)
                        uptask.webSocketClient!!.connect()
                        uptask.Ingstatus = LoadIngStatus.TRANSFERING
                        savaspbyfalag(UPLOADING)
                    } else {
                        val params = HashMap<String, Any>()
                        params["file_name"] = uptask.file_name + ""
                        params["is_dir"] = IS_FILE
                        params["user_id"] = "$USER_ID" //TODO
                        params["fidhash"] = "${uptask.file_hash}"
                        params["file_hash"] = "${uptask.file_MD5}"
                        params["pid"] = uptask.pid!!
                        params["size"] = uptask.file_size
                        UploadFileMetaInfo(
                            params,
                            uptask.file_MD5!!,
                            uptask.file_name!!,
                            uptask.file_size.toString(),
                            uptask.file_hash!!
                        )
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }.apply {
            uptaskmap.put("${uptaskmap}", this)
            upexecutor.execute(this)
        }
        //1.获取文件md5值
        //2.aes加密key
        //3.pcak
        //4.transfer
        //5.提交metainfo
    }

    //</editor-fold>
//<editor-fold desc="上传文件bytask">
    fun upfilebytask() {
        if (daemon == null) {
            Toastinfo("coredaemon服务未启动")
            Log.e("ok", "daemon服务未启动")
            return
        }
        if (plugindaemon == null) {
            Toastinfo("插件服务未启动")
            Log.e("ok", "plugindaemon服务未启动")
            return
        }
        //        val uploadtask = LoadFiileTask(0,destfile.name,filemd5,null,destfile.length(),destfile,pid)
        //        upexecutor.execute(uploadtask)
    }

    //</editor-fold>
//<editor-fold desc="META信息上传 JSON map">
    fun GetUploadFileJsonMap(
        file_name: String,
        fidhash: String,
        filemd5: String,
        pid: String,
        size: Long
    ): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        params["file_name"] = file_name + ""
        params["is_dir"] = IS_FILE
        params["user_id"] = "${USER_ID}" //TODO
        params["fidhash"] = "${fidhash.apply {
            Log.e("ok", "fidhash=${this}")
        }}"
        params["file_hash"] = "${filemd5.apply {
            Log.e("ok", "fidhash=${this}")
        }}"
        params["pid"] = pid
        params["size"] = size
        return params
    }

    //</editor-fold>
//<editor-fold desc="META信息上传">
    fun UploadFileMetaInfo(
        params: HashMap<String, Any>,
        tag: Any,
        displayName: String,
        size: String,
        filehash: String
    ) { //type 0 没有调用up  1 从up接口
        OkGo.post<String>(URL_ADD_File).upJson(JSONObject(params))
            .tag(tag)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.body()?.apply {
                        if (VerifyUtils.VerifyResponseData(this)) {
                            for (loadingFile in MyApplication.upLoad_Ing) {
                                if (loadingFile.file_hash.equals(filehash)) {
                                    MyApplication.upLoad_Ing.remove(loadingFile)
                                    savaspbyfalag(UPLOADING)
                                    break
                                }
                            }
                            Toastinfo("${displayName} 上传完成!!")

                            //把上传完成的文件进行添加到我的文件列表中进行显示
                            MyApplication.upLoad_completed.add(
                                0,
                                //CompletedFile("${displayName}",SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),size, false)
                                CompletedFile(
                                    "${displayName}",
                                    "${JSONObject(this).getJSONObject("data")
                                        .getString("created_at")}",
                                    size,
                                    false
                                )
                            )
                            savaspbyfalag(UPLOADCOMPLETED)
                            EventBus.getDefault().post(MessageEvent())
                        } else {
                            Toastinfo("${JSONObject(this).getString("message")}")
                        }
                    }
                }
            })
    }

    //</editor-fold>
//<editor-fold desc="获取仓库信息">
    fun getrepostat() {
        exec("repo stat").apply {
            daemon = this
            read {
                Log.e("daemonit", "repo stat=" + it)
                logs.add(it)
            }
        }
    }

    //</editor-fold>
//<editor-fold desc="清理仓库缓存">
    fun coregc() {
        exec("repo gc").apply {
            daemon = this
            read {
                Log.e("daemonit", "repo stat=" + it)
                logs.add(it)
            }
        }
    }

    //</editor-fold>
//<editor-fold desc="获取pin缓存"列表>
    fun getpinlist() {
        exec("pin ls").apply {
            daemon = this
            read {
                Log.e("daemonit", "repo stat=" + it)
                logs.add(it)
            }
        }
    }

    //</editor-fold>
//<editor-fold desc="添加pin缓存"列表>
    fun addpinlist(fidhash: String) {
        exec("pin add ${fidhash}").apply {
            daemon = this
            read {
                Log.e("daemonit", "pin add=" + it)
                logs.add(it)
            }
        }
    }

    //</editor-fold>
//<editor-fold desc="已连接节点"列表>
    fun peers() {
        exec("swarm peers").apply {
            daemon = this
            read {
                Log.e("daemonit", "pin add=" + it)
                logs.add(it)
            }
        }
    }

    //</editor-fold>
//<editor-fold desc="移除pin缓存"列表>
    fun rmpinlist(fidhash: String) {
        exec("pin rm ${fidhash}").apply {
            daemon = this
            read {
                Log.e("daemonit", "pin add=" + it)
                logs.add(it)
            }
        }
    }

    //</editor-fold>
//<editor-fold desc="添加pin缓存"列表>
    fun addfile(file_path: String) {
        exec("add --pin=false ${file_path}").apply {
            daemon = this
            read {
                Log.e("daemonit", "pin add=" + it)
                logs.add(it)
            }
        }
    }

    //</editor-fold>
    class MyBinder(var mDaemonService: DaemonService) : Binder() {
        val TAG = "DaemonService.MyBinder"
        fun GetDaemonService(): DaemonService {
            return mDaemonService
        }

    }

    //<editor-fold desc="保存数据到sp   SharedPreferences  善德儿 怕分贼死">
    fun savaspbyfalag(flag: Int) {
        MyApplication.getInstance().GetSP().edit().apply {
            when (flag) {
                DOWNLOADING -> {
                    putString("downLoad_Ing", Gson().toJson(MyApplication.downLoad_Ing))
                }
                UPLOADING -> {
                    putString("upLoad_Ing", Gson().toJson(MyApplication.upLoad_Ing))
                }
                UPLOADCOMPLETED -> {
                    putString("upLoad_completed", Gson().toJson(MyApplication.upLoad_completed))
                }
                DOWNLOADCOMPLETED -> {
                    putString(
                        "downLoad_completed",
                        Gson().toJson(MyApplication.downLoad_completed)
                    )
                }
            }
            apply()
        }
    }

    //</editor-fold>
//<editor-fold desc="保存数据到sp">
    fun savaspall() {
        MyApplication.getInstance().GetSP().edit().apply {
            putString("downLoad_Ing", Gson().toJson(MyApplication.downLoad_Ing))
            putString("downLoad_completed", Gson().toJson(MyApplication.downLoad_completed))
            putString("upLoad_Ing", Gson().toJson(MyApplication.upLoad_Ing))
            putString("upLoad_completed", Gson().toJson(MyApplication.upLoad_completed))
            apply()
        }
    }

    //</editor-fold>
    override fun onDestroy() {
        Log.e("ok", " daemon onDestroy")
        for (loadingFile in MyApplication.downLoad_Ing) {
            loadingFile.Ingstatus = LoadIngStatus.WAITING
        }
        for (loadingFile in MyApplication.upLoad_Ing) {
            loadingFile.Ingstatus = LoadIngStatus.WAITING
        }
        Log.e("ok", " daemon onDestroy w sp ok")
        if (downexecutor != null && !downexecutor.isShutdown) {
            downexecutor.shutdown()
        }
        if (upexecutor != null && !upexecutor.isShutdown) {
            upexecutor.shutdown()
        }
        super.onDestroy()
    }
}



