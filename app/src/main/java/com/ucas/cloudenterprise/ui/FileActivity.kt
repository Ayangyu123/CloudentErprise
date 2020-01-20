package com.ucas.cloudenterprise.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask.execute
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.model.Resource
import com.ucas.cloudenterprise.model.Team
import com.ucas.cloudenterprise.utils.FileCP
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.get
import com.ucas.cloudenterprise.utils.store
import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multiaddr.MultiAddress
import io.ipfs.multihash.Multihash
import org.json.JSONObject
import java.io.File

/**
@author simpler
@create 2020年01月07日  16:46
 */
class FileActivity : AppCompatActivity(){
    val TAG ="FileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
    }

    fun GetFiles(view: View) {
//        OkGo.get<String>(URL_LIST_FILES +"${USER_ID}").
//            execute(object : StringCallback(){
//                override fun onSuccess(response: Response<String>?) {
//                    Log.e(TAG, response?.body().toString())
//
//                    response?.let {
//                        val json = JSONObject(it.body().toString())
//                        val code = json.getInt("code")
//                        if (code == Resource.SUCCESS) {
//                            Toastinfo("获取成员列表成功")
////                            tv_result.text = it.body().toString()
//                            var files_list = Gson().fromJson<List<Team>>(json.getJSONArray("data").toString(),
//                                object : TypeToken<List<File_Bean>>(){}.type)
////                                Array<Team>::class.java).toMutableList())
//                            Log.e(TAG,files_list.toString())
//
//
//                        } else {
//                            //TODO
//                        }
//                    }
//                }
//            })

            OkGo.post<String>("http://10.0.130.239:9984/api/v0/rs/encode")
                .params("f",store["config"])
                .execute(object :StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        Log.e(TAG,response?.body().toString())
                    }

                })
        OkGo.post<String>("http://10.0.130.239:9984/api/v0/rs/decode")
            .params("hash","QmbM2rsUkP3o8sJy7cfKxRcY7ggK8qUWgirMtQoKRVB3Aq")
            .isMultipart(true)
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG,response?.body().toString())
                }

            })
    }




    fun DeleteFile(view: View) {
        Log.e(TAG, "deletefile")
        val params = HashMap<String,Any>()
        params["file_id"] = "boaorlboo3sg6t4p09d0"
        params["user_id"] = USER_ID
//        params["user_id"] = "test.txt"
        val json = JSONObject(params as Map<String, Any>)

        OkGo.put<String>(URL_LIST_FILES).
            upJson(json).
            execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG,response?.body().toString())
                }

            })


    }

    fun GetFile(view: View) {
        if(checkPermission(this) == false){
//            tv_text.text =  tv_text.text.toString()+"\n"+"没有写sd卡权限"
            Toastinfo("没有写sd卡权限")
            return
        }

        DaemonService.daemon?.let {
            Thread(object:Runnable{
                override fun run() {
                    var ipfs = IPFS( MultiAddress(CORE_CLIENT_ADDRESS))

                    var filePointer = Multihash.fromBase58(TEST_DOWN_FiLE_HASH)
                    var fileContents = ipfs.cat(filePointer)



                    val root =  File(ROOT_DIR_PATH)
                    if(!root.exists()){
                        root.mkdirs()
                    }
                    val dest  = File(ROOT_DIR_PATH+System.currentTimeMillis())

//                if(!dest.exists()){
//                    dest.createNewFile()
//                }

//                val dest  = store["lj.png"]
                    val output_swarm_key = dest.outputStream()
                    try {
                        output_swarm_key.write(fileContents)
                    }finally {
                        output_swarm_key.close()
                    }

                    Log.e("ok","文件写入完毕")
                    runOnUiThread(){
//                        tv_text.text =  tv_text.text.toString()+"\n 文件名称：${dest.name}"+"\n"+"filesize=${dest.length()}\nfilepath=${dest.absolutePath}"
                    }

                }

            }).start()
        }
    }

    fun ChooseFile(view: View) {
            if(checkPermission(this)!! == false){
                Toastinfo("没有sd卡读取权限")
              return
            }



        var i =  Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "文件选择"), FILE_CHOOSER_RESULT_CODE);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        when (requestCode) {
//            FilePickerManager.REQUEST_CODE -> {
//                if (resultCode == Activity.RESULT_OK) {
////                    val list = FilePickerManager.obtainData()
////                    list.forEach {
////                        Log.e("",""+it)
////                    }
//
//
//
//                    // do your work
//                } else {
//                    Toast.makeText(this, "没有选择任何东西~", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }


        if (requestCode == FILE_CHOOSER_RESULT_CODE && data !=null) {

            val uri = Uri.parse(data.dataString) // 获取用户选择文件的URI
            Log.e("uri)","data.getData()="+uri)
            Log.e("uri)","uri.getScheme()="+uri.getScheme())
            Log.e("uri)","uri.authority="+uri.authority)




            val cursor= contentResolver.query(uri, null, null, null, null, null)
            var displayName: String? =null
            cursor?.use {
                if(it.moveToFirst()) {
                    displayName =
                        it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                    tv_text.text  =  tv_text.text.toString()+"\n 文件名称：$displayName"}
                    val size: Int =
                        it.getInt(it.getColumnIndex(OpenableColumns.SIZE))
//                tv_text.text  =  tv_text.text.toString()+"\n 文件大小：$size"}

                    DaemonService.daemon?.let {
                        Thread(Runnable {
                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"开始复制文件"
                            }
                            FileCP(
                                contentResolver.openInputStream(uri)!!,
                                store[displayName!!].outputStream()
                            )
                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"复制文件完毕，准备上传"
                            }
                            val ipfs = IPFS(CORE_CLIENT_ADDRESS)
                            val src =store[displayName!!]
                            val srcsize =src.length()
                            val file = NamedStreamable.FileWrapper(src)
                            val addResult = ipfs.add(file)[0]
                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"上传文件完毕"
                            }
                            Log.e("ifileContents", "addResult=" + addResult)

                            runOnUiThread() {
                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"addResult="+addResult
                            }
                            store[displayName!!].delete()
                            runOnUiThread() {
                                addResult.apply {
                                    Log.e(TAG,"${this.hash}")
//                                    Log.e(TAG,"${this.size.get()}")


                                    AddFile("${name.get()}",srcsize,"${hash}")
                                }


                                //                        tv_text.text =  tv_text.text.toString()+"\n"+"临时文件已删除"
                            }
                        }).start()
                    }
                }}


    }


}
    fun AddFile(displayName:String, size: Long, hash: String) {
        val params = HashMap<String,Any>()
        params["file_name"] = displayName
        params["is_dir"] = IS_DIR
        params["user_id"] = "W3kFFln013giupMDYrLCTM3w7F" //TODO
        params["fidhash"] = hash
        params["pid"] = "root"
        params["size"] = size


        val json = JSONObject(params as Map<String, Any>)

        OkGo.post<String>(URL_ADD_File).
            upJson(json).
            execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    Log.e(TAG,response?.body().toString())

                }

            })


    }

}