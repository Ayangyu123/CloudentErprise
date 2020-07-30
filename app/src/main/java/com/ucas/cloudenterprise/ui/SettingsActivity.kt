package com.ucas.cloudenterprise.ui

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.base.BaseActivity
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.common_head.*
import kotlinx.coroutines.launch
import me.rosuh.filepicker.adapter.FileListAdapter
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.AbstractFileFilter
import me.rosuh.filepicker.config.FileItemOnClickListener
import me.rosuh.filepicker.config.FilePickerConfig
import me.rosuh.filepicker.config.FilePickerManager
import org.json.JSONObject
import java.io.File
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
        SetDestdirText()
        getrepostat()
    }

    private fun SetDestdirText() {
        var sdpath = Environment.getExternalStorageDirectory().absolutePath
        var path = getRootPath().replace(sdpath+"/","")
        tv_default_down_dest_dir.text = "默认下载位置：\n ${path}"
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

    fun changedowndestdir(view: View) {

        if(checkPermission(this)!! == false){
            Toastinfo("没有sd卡读取权限")
            return
        }



        FilePickerManager
            .from(this)

            .enableSingleChoice()
            .filter(object : AbstractFileFilter() {
                override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                    return ArrayList(listData.filter { item ->
                        item.isDir
                    })
                }
            })
            .skipDirWhenSelect(false)
            .setTheme(R.style.FilePickerThemeReply)
            .forResult(FilePickerManager.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==FilePickerManager.REQUEST_CODE&&resultCode== Activity.RESULT_OK){

            if(FilePickerManager.obtainData().isEmpty()){
                Toastinfo("没有选择文件夹")
                return
            }
            var downdestdir=FilePickerManager.obtainData()[0]+"/SaturnClound/$USER_PHONE"
            ROOT_DIR_PATH=downdestdir
            Log.e("SettingsActivity","ROOT_DIR_PATH ${ROOT_DIR_PATH}")
            MyApplication.getInstance().GetSP().edit().putString("downdestdir",downdestdir).apply()
            SetDestdirText()
        }
    }

    fun seedowndestdir(view: View) {
        if(com.ucas.cloudenterprise.app.checkPermission(this)!! == false){
            Toastinfo("没有sd卡读取权限")
            return
        }
        var file = File(getRootPath())
        if(!file.exists()){
            Toastinfo("该目录为空，没有下载任何文件")
            return
        }
       try {
           FilePickerManager.from(this)
               .setItemClickListener(
                   object :FileItemOnClickListener{
                       override fun onItemClick(
                           itemAdapter: FileListAdapter,
                           itemView: View,
                           position: Int
                       ) {
                           val filepath=itemAdapter.getItem(position)!!.filePath
                           Log.e("ok","file  path  $filepath")
                           val file_uri=Uri.fromFile(File(filepath))
                           Log.e("ok","file  uri  $file_uri")
                           ShareUtils.openFiles(filepath,MyApplication.context)
//                    val sendIntent = Intent(Intent.ACTION_VIEW, file_uri)
////// Verify that the intent will resolve to an activity
//                    if (sendIntent.resolveActivity(packageManager) != null) {
//                        startActivity(sendIntent)
//                    }else{
//                        Log.e("ok"," open file fail")
//                    }
                       }

                       override fun onItemChildClick(
                           itemAdapter: FileListAdapter,
                           itemView: View,
                           position: Int
                       ) {

                       }

                       override fun onItemLongClick(
                           itemAdapter: FileListAdapter,
                           itemView: View,
                           position: Int
                       ) {

                       }
                   })
               .storageType("下载目录", FilePickerConfig.STORAGE_CUSTOM_ROOT_PATH)
               .setCustomRootPath(file.absolutePath).forResult(1)
//           startActivity(  Intent().apply {
//               action =Intent.ACTION_GET_CONTENT
////               action =Intent.ACTION_OPEN_DOCUMENT_TREE
////               flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//               this.addCategory(Intent.CATEGORY_DEFAULT);
//               this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//               Log.e("ok",file.parentFile.absolutePath)
//               Log.e("ok",Uri.fromFile(file).toString())
//                setType("file/*")
////               setDataAndType(Uri.fromFile(file),"*/*")
////               setDataAndType(Uri.fromFile(file),"*/*")
////               putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(file))
//           })
       }catch (e: ActivityNotFoundException){
           Toastinfo("无响应应用")
       }
    }

    fun cleardowndestdir(view: View) {
        if(com.ucas.cloudenterprise.app.checkPermission(this)!! == false){
            Toastinfo("没有sd卡读取权限")
            return
        }
        var file = File(getRootPath())
        if(!file.exists()){
            Toastinfo("该目录为空，没有下载任何文件")
            return
        }else{
            lifecycleScope.launch {
                view.apply {
                    isEnabled = false
                    setBackgroundColor(Color.GRAY)
                }
                Toastinfo("正在清理文件中")
                cleardowndestdir(file)
                view.apply {
                    isEnabled = true
                    setBackgroundColor(Color.WHITE)
                }
                Toastinfo("文件清理完成")
            }

        }
    }
    //<editor-fold desc="清除文件夹">
    suspend fun cleardowndestdir(file:File){
        var filelist=file.listFiles()
        var filesize =filelist.size
        if (filesize== 0){
            file.delete()
        }else{
            for (i in   0 until filesize){
                if(filelist[i].isDirectory){
                    cleardowndestdir(file.listFiles()[i])
                }else{
                    filelist[i].delete()}
            }

        }

    }
    //</editor-fold>
}