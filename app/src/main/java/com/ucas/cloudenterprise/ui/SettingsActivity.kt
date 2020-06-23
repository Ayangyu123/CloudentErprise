package com.ucas.cloudenterprise.ui

import android.app.Dialog
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.CORE_PATH
import com.ucas.cloudenterprise.app.MyApplication
import com.ucas.cloudenterprise.app.ROOT_DIR_PATH
import com.ucas.cloudenterprise.app.getRootPath
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

class SettingsActivity : BaseActivity() {

    var mClearCache: Dialog? = null

    override fun GetContentViewId() = R.layout.activity_settings

    override fun InitView() {
        tv_title.text = "设置"
        tv_edit.visibility = View.GONE
        iv_back.setOnClickListener { finish() }
        ll_clear_cahe.isEnabled = false
        ll_clear_cahe.setOnClickListener {
            showClearCaheDialog()
        }

    }

    private fun showClearCaheDialog() {
        if (!MyApplication.downLoad_Ing.isEmpty()) {
            Toastinfo("有下载任务进行中，不能清理")
            return
        }
        if (!MyApplication.upLoad_Ing.isEmpty()) {
            Toastinfo("有上传任务进行中，不能清理")
            return
        }

        if (mClearCache == null) {
            mClearCache = Dialog(this).apply {
                var contentview =
                    LayoutInflater.from(this@SettingsActivity)
                        .inflate(R.layout.dialog_clear_cahe, null)
                setContentView(contentview)
                setCancelable(false)
            }

        }
        mClearCache!!.show()
        ClearCache()


    }

    fun ClearCache() {
        thread {
            if(DaemonService.daemon!=null){
                if(DaemonService.plugindaemon!=null){
                    DaemonService.plugindaemon?.destroy()
                    DaemonService.plugindaemon=null
                }
                DaemonService.daemon?.destroy()
                DaemonService.daemon=null
            }
            DaemonService.logs.clear()
            store.get("badgerds").apply {
                if(exists()){
                    Log.e("ok"," file size = ${this.length()}")
                    var fileArrays= listFiles()
                  if(fileArrays.size>0){
                      for(i in 0 until  listFiles().size){
                          fileArrays[i].delete()
                      }
                  }

                    if (delete()){
                        Log.e("ok"," file is delete")
                        exec("daemon").apply {
                            DaemonService.daemon = this
                            read {

                                Log.e("daemonit",it)
                                if(it.equals("Daemon is ready")){
                                    getrepostat()
                                }
                                DaemonService.logs.add(it) }
                        }
                        Runtime.getRuntime().exec(
                            "${pluginbin.absolutePath}",
                            arrayOf(String(Base64.getDecoder().decode(CORE_PATH), StandardCharsets.UTF_8 )+"=${store.absolutePath}")

                        ).apply {
                            DaemonService.plugindaemon = this
                            read {   Log.e("plugindaemon",it)
                                DaemonService.logs.add(it) }
                        }

                    }else{
                        Log.e("ok"," file  delete is fail")
                    }

                }else{
                    Log.e("ok","文件不存在")
                }
            }




        }
//            .start()



    }


    override fun InitData() {
        tv_default_down_dest_dir.text = "默认下载位置：\n ${getRootPath()}"
        getrepostat()
    }

    private fun getrepostat() {
        exec("repo stat --encoding=json").apply {
            read {
                Log.e("daemonit", "repo stat=" + it)
                if (it.startsWith("{\"RepoSize\"")) {
                    runOnUiThread {

                        mClearCache?.apply {
                            if(this.isShowing){
                                dismiss()
                                Toastinfo("清理完成")
                            }
                        }
                        tv_repo_size.text = Formatter.formatFileSize(
                            this@SettingsActivity,
                            JSONObject(it).getLong("RepoSize")
                        )
                        ll_clear_cahe.isEnabled = true
                    }
                } else {
                    runOnUiThread {
                        Toastinfo("获取缓存失败")
                    }

                }
            }
        }
    }

    fun execmd(view: View) {
        if (et_exec.text.isEmpty()) {
            Toastinfo("请输入命令")
            return
        }
        exec("${et_exec.text}").apply {

            read {
                Log.e("daemonit", "${et_exec.text} info =" + it)
                runOnUiThread {
                    tv_exec_info.text = tv_exec_info.text.toString() + "\n" + it
                }
//
            }
        }


    }
}