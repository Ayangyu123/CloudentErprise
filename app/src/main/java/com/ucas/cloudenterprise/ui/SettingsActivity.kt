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
import com.ucas.cloudenterprise.app.MyApplication
import com.ucas.cloudenterprise.app.ROOT_DIR_PATH
import com.ucas.cloudenterprise.app.getRootPath
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.exec
import com.ucas.cloudenterprise.utils.read
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.common_head.*
import org.json.JSONObject

class SettingsActivity : BaseActivity() {

    var  mClearCache:Dialog ?=null

    override fun GetContentViewId()= R.layout.activity_settings

    override fun InitView() {
      tv_title.text ="设置"
        tv_edit.visibility = View.GONE
        iv_back.setOnClickListener { finish() }
        ll_clear_cahe.isEnabled =false
        ll_clear_cahe.setOnClickListener {
            showClearCaheDialog()
        }

    }

    private fun showClearCaheDialog() {
                if(!MyApplication.downLoad_Ing.isEmpty()){
                    Toastinfo("有下载任务，不能清理")
                    return
                }
        if(!MyApplication.upLoad_Ing.isEmpty()){
            Toastinfo("有上传任务，不能清理")
            return
        }

            if(mClearCache==null){
                mClearCache =Dialog(this).apply {
                    var contentview =
                        LayoutInflater.from(this@SettingsActivity).inflate(R.layout.dialog_clear_cahe, null)
                    setContentView(contentview)
                    setCancelable(false)
                }

            }
        mClearCache!!.show()
        ClearCache()


    }
    fun  ClearCache(){
        if(DaemonService.daemon!=null){
            myBinder?.mDaemonService?.stop()
//            OkGo.post<String>("http://127.0.0.1:5001/api/v0/repo/gc?stream-errors=true&quiet=true")
//                .tag(this)
//                .execute(object :StringCallback(){
//                    override fun onError(response: Response<String>?) {
//                        super.onError(response)
//                        Toastinfo("清理失败")
//                    }
//                    override fun onSuccess(response: Response<String>?) {
//                        Log.e("ok",response?.body().toString())
//                        Toastinfo("清理完成")
//                        getrepostat()
//                    }
//
//                    override fun onFinish() {
//                        mClearCache!!.dismiss()
//                        super.onFinish()
//
//
//
//                    }
//                })

        }
    }



    override fun InitData() {
        tv_default_down_dest_dir.text= "默认下载位置：\n ${getRootPath()}"
            getrepostat()
    }

    private fun getrepostat() {
        exec("repo stat --encoding=json").apply {
//            DaemonService.daemon = this
            read {
                Log.e("daemonit","repo stat="+it)
                if(it.startsWith("{\"RepoSize\"")){
                    runOnUiThread {
                        tv_repo_size.text =Formatter.formatFileSize(this@SettingsActivity,JSONObject(it).getLong("RepoSize"))
                        ll_clear_cahe.isEnabled =true
                    }
                     }else{
                    runOnUiThread {
                        Toastinfo("获取失败")
                    }

                }
                }
            }
        }
//        if(DaemonService.daemon!=null){
//            OkGo.post<String>("http://127.0.0.1:5001/api/v0/stats/repo?size-only=true&human=true")
//                .tag(this)
//                .execute(object :StringCallback(){
//                    override fun onSuccess(response: Response<String>?) {
//                        Log.e("ok",response?.body().toString())
//                        return
//                        response?.body()?.apply{
//                            if(this.startsWith("{\"RepoSize\"")){
//                                tv_repo_size.text =Formatter.formatFileSize(this@SettingsActivity,JSONObject(response?.body()).getLong("RepoSize"))
//                                ll_clear_cahe.isEnabled =true
//                            }
//
//                        }
//
//                    }
//                })
//
//        }
//    }

    fun execmd(view: View) {
        if(et_exec.text.isEmpty()){
            Toastinfo("请输入命令")
            return
        }
        exec("${et_exec.text}").apply {

            read {
                Log.e("daemonit","${et_exec.text} info ="+it)
                runOnUiThread {
                    tv_exec_info.text= tv_exec_info.text.toString()+"\n"+it
                }
//
                }
        }


    }
}