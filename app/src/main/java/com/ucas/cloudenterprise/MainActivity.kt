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
import kotlinx.android.synthetic.main.activity_main.*
import io.ipfs.multiaddr.MultiAddress
import io.ipfs.api.IPFS
import com.ucas.cloudenterprise.utils.FileUtils
import io.ipfs.api.NamedStreamable
import io.ipfs.multihash.Multihash
import java.io.File


class MainActivity : AppCompatActivity() {
    var ipfs:IPFS?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService<DaemonService>()

        webview.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            loadUrl("https://sweetipfswebui.netlify.com/")

        }

    }

    fun GetStats(view: View) {
       DaemonService.daemon?.let {
           Thread(object:Runnable{
               override fun run() {
                   var ipfs = IPFS(MultiAddress("/ip4/127.0.0.1/tcp/5001"))
                   val stats =ipfs?.stats?.bw().toString()
                   Log.e("ipfs.stats.bw()","ipfs.stats.bw()="+ stats   )

                   runOnUiThread(){
                       tv_text.text = stats
                   }

               }

           }).start()
           return
       }


    }

    fun GetFile(view: View) {

        Thread(object:Runnable{
            override fun run() {
                var ipfs = IPFS(MultiAddress("/ip4/127.0.0.1/tcp/5001"))

                val filePointer = Multihash.fromBase58("QmZACMR5Zj2nLYdm1FwS5SFz1QcbtVty7rp5s4aGAiTDHu")
                val fileContents = ipfs.cat(filePointer)
                Log.e("ifileContents","fileContents="+ String(fileContents,Charsets.UTF_8)   )

                runOnUiThread(){
                    tv_text.text =  tv_text.text.toString()+"\n"+"fileContents="+String(fileContents,Charsets.UTF_8)
                }

            }

        }).start()
    }
    //上传文件
    fun UploadFile(view: View) {
//        val i4 = Intent(applicationContext, FileBrowser::class.java)
//        startActivity(i4)
//        val i2 = Intent(applicationContext, FileChooser::class.java)
//        i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal())
//        startActivityForResult(i2, FILE_CHOOSER_RESULT_CODE)
//        FilePickerManager
//            .from(this)
//            .forResult(FilePickerManager.REQUEST_CODE)
//
        var i =  Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);

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

              val uri =Uri.parse(data.dataString) // 获取用户选择文件的URI
                Log.e("uri)","data.getData()="+uri)
                Log.e("uri)","uri.getScheme()="+uri.getScheme())
                Log.e("uri)","uri.authority="+uri.authority)
//                Log.e("uri)","uri.authority="+uri.)


//                 var cidcontent  =FileUtils.readTextFromUri(this, uri)
////                 var path =FileUtils.getRealPathFromUri(this, uri)
//            Log.e("path","cidcontent="+cidcontent)
//            tv_text.text =cidcontent
//            return

//               val cursor= contentResolver.query(uri, null, null, null, null, null)
//
//            if (cursor != null && cursor.moveToFirst()) {
//                val displayName = cursor.getString(
//                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                )
//                Log.e("ok", "Display Name: $displayName")
//            }


//            Thread(object:Runnable{
//                override fun run() {
//                    var ipfs = IPFS(MultiAddress("/ip4/127.0.0.1/tcp/5001"))
//                    val file = NamedStreamable.FileWrapper(File(contentResolver.openFileDescriptor(uri,"rw")))
//                    val addResult = ipfs.add(file)[0]
//                    Log.e("ifileContents","addResult="+ addResult   )
//
//                    runOnUiThread(){
//                        tv_text.text =  tv_text.text.toString()+"\n"+"addResult="+addResult
//                    }
//
//                }
//
//            }).start()

}
    }



}
