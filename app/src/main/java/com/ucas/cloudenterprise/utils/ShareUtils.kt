package com.ucas.cloudenterprise.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File


/**
@author simpler
@create 2020年02月28日  16:25
 */
object ShareUtils {
     fun  sharebyandroidIntent(message: String,context: Context) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is my text to send.${message}")
                type = "text/plain"
            }
         context.startActivity(sendIntent)
        }

        fun sharebyandroidSheet(message:String,context: Context){
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${message}")
                type = "text/plain"
            }

          context.startActivity(Intent.createChooser(sendIntent,null))
        }


    //打开文件时调用
    fun openFiles(filesPath: String,context: Context) {
        val uri: Uri = Uri.parse("file://$filesPath")
        val intent = Intent()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "com.ucas.cloudenterprise.fileprovider",
                    File(filesPath)
                )
                Log.e("ok","contentUri is  $contentUri")
                intent.setDataAndType(contentUri, getMIMEType(filesPath))
            } else {
                intent.setDataAndType(uri, getMIMEType(filesPath))
            }
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMIMEType(filePath: String?): String? {
        val file = File(filePath)
        val mime = MimeTypeMap.getSingleton()
        val ext = file.name.substring(file.name.lastIndexOf(".") + 1)
        return mime.getMimeTypeFromExtension(ext)
    }
}