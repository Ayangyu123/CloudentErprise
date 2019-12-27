package com.ucas.cloudenterprise

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ucas.cloudenterprise.app.FILE_CHOOSER_RESULT_CODE
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.utils.startService
import com.ucas.cloudenterprise.utils.store
import kotlinx.android.synthetic.main.activity_main.*
import io.ipfs.multiaddr.MultiAddress
import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable
import io.ipfs.multihash.Multihash
import java.io.File


class MainActivity : AppCompatActivity() {
    var ipfs:IPFS?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService<DaemonService>()



        webview.loadUrl("https://sweetipfswebui.netlify.com/")

    }

    fun GetStats(view: View) {
        Thread(object:Runnable{
            override fun run() {
                ipfs = IPFS(MultiAddress("/ip4/127.0.0.1/tcp/5001"))
                val stats =ipfs?.stats?.bw().toString()
                Log.e("ipfs.stats.bw()","ipfs.stats.bw()="+ stats   )

                runOnUiThread(){
                    tv_text.text = stats
                }

            }

        }).start()

    }

    fun GetFile(view: View) {


    }
    //上传文件
    fun UploadFile(view: View) {
        var i =  Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
//            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            var result = if(data == null || resultCode != RESULT_OK )  null else data.getData();
//            File(data?.getData())
            data?.let {
                 val uri =Uri.parse(data.dataString) // 获取用户选择文件的URI
                Log.e("data.getData()","data.getData()="+uri)

                // 通过Contentvider查询文件路径
                var resolver = getContentResolver();
                var cursor = resolver.query(uri, null, null, null, null);

                var path:String?=null
                if (cursor == null) {
                    // 未查询到，说明为普通文件，可直接通过URI获取文件路径
                     path = uri.getPath()
                    return;
                }
                if (cursor.moveToFirst()) {
                    // 多媒体文件，从数据库中获取文件的真实路径
                     path = cursor.getString(cursor.getColumnIndex("_data"));
                }
                cursor.close();
                    tv_text.text = tv_text.text.toString()+"\n"+"FilePath="+path

                var file = NamedStreamable.FileWrapper(File(path))
                var addResult = ipfs!!.add(file)[0]
                tv_text.text = tv_text.text.toString()+"\n"+"addResult="+addResult
                Log.e("filepath","filepath="+ path)


//                Log.e("addResult","fileContents="+ String(fileContents, Charsets.UTF_8))
                Log.e("addResult","addResult="+ addResult)

            }


//            val filePointer = Multihash.fromBase58("Qmaisz6NMhDB51cCvNWa1GMS7LU1pAxdF4Ld6Ft9kZEP2a")
//            val fileContents = ipfsclinet.cat(filePointer)


        }






    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
//        if (requestCode != FILE_CHOOSER_RESULT_CODE || mUploadCallbackAboveL == null)
//            return
//        var results: Array<Uri>? = null
//        if (resultCode == Activity.RESULT_OK) {
//            if (intent != null) {
//                val dataString = intent.dataString
//                val clipData = intent.clipData
//                if (clipData != null) {
//                    results = arrayOf<Uri>()
//                    for (i in 0 until clipData.itemCount) {
//                        val item = clipData.getItemAt(i)
//                        results[i] = item.uri
//                    }
//                }
//                if (dataString != null)
//                    results = arrayOf(Uri.parse(dataString))
//            }
//        }
//        mUploadCallbackAboveL!!.onReceiveValue(results)
//        mUploadCallbackAboveL = null
//    }


}
