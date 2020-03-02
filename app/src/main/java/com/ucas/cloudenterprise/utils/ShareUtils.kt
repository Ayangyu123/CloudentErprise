package com.ucas.cloudenterprise.utils

import android.content.Context
import android.content.Intent
import com.ucas.cloudenterprise.app.MyApplication

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


}