package com.ucas.cloudenterprise.core

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_MIN
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.os.Build
import android.os.Build.CPU_ABI
import android.util.Log
import androidx.core.app.NotificationCompat
import  com.ucas.cloudenterprise.utils.*
import com.ucas.cloudenterprise.MainActivity
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.BOOTSTRAPS

class DaemonService : Service() {

    override fun onBind(intent: Intent) = null
    companion object {
        var daemon: Process? = null
        var logs: MutableList<String> = mutableListOf()
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationChannel("sweetipfs", "Sweet IPFS", IMPORTANCE_MIN).apply {
                description = "Sweet IPFS"
                getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(this)
            }

        install()
        start()
        startForeground(1, notification.build())
    }

    fun install() {

        val type = CPU_ABI.let {
            when {
                it.startsWith("arm") -> "arm"
                it.startsWith("x86") -> "386"
                else -> throw Exception("Unsupported ABI")
            }
        }

        bin.apply {
            delete()
            createNewFile()
        }

        val input = assets.open(type)
        val output = bin.outputStream()
        try {
            input.copyTo(output)
        } finally {
            input.close()
            output.close()

        }

        bin.setExecutable(true)
        println("Installed binary")
    }

    fun start() {
        logs.clear()

        exec("init").apply {
            read { logs.add(it) }
            waitFor()
        }

        store["swarm.key"].apply {
            delete()
            createNewFile()
        }
        val input_swarm_key = assets.open("swarm.key")
        val output_swarm_key =store["swarm.key"].outputStream()
        try {

            input_swarm_key.copyTo(output_swarm_key)

        }finally {

            input_swarm_key.close();
            output_swarm_key.close()


        }


        config {
            obj("API").obj("HTTPHeaders").apply {

                array("Access-Control-Allow-Origin").also { origins ->

                    origins.removeAll { true }
                    origins.add(json("*"))
                }

                array("Access-Control-Allow-Methods").also { methods ->
                    val put = json("PUT")
                    val get = json("GET")
                    val post = json("POST")
                    if (put !in methods) methods.add(put)
                    if (get !in methods) methods.add(get)
                    if (post !in methods) methods.add(post)
                }
                this.add("Access-Control-Allow-Credentials", json(arrayListOf<String>("true")))


            }
            array("Bootstrap").also {boots ->
                boots.removeAll {true }
                BOOTSTRAPS.forEach {
                    boots.add(json(it))
                }
               }
        }

        exec("daemon").apply {
            daemon = this
            read { logs.add(it) }
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
            setContentTitle("Sweet IPFS")

            val open = pendingActivity<MainActivity>()
            setContentIntent(open)
            addAction(R.drawable.ic_launcher, "Open", open)

            if (daemon == null) {
                setContentText("IPFS is not running")

                val start = pendingService(intent<DaemonService>().action("start"))
                addAction(R.drawable.ic_launcher, "start", start)
            } else {
                setContentText("IPFS is running")

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
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification.build())
    }


}
