package com.ucas.cloudenterprise.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.zxing.client.result.ParsedResultType
import com.mylhyl.zxing.scanner.encode.QREncode

/**
@author simpler
@create 2020年03月03日  16:25
 */
object QRCodeUtils {
    fun CreateQRCode(content:String,Context:Context,logoRes:Int,logosize:Int):Bitmap{
        return QREncode.Builder(Context)
            .setColor(Context.getColor(android.R.color.black))//二维码颜色
//            .setParsedResultType(ParsedResultType.TEXT)//默认是TEXT类型
            .setContents(content+"")//二维码内容
            .setMargin(0)//marg距离默认4
             .setLogoBitmap(BitmapFactory.decodeResource(Context.resources,logoRes),logosize)//二维码中间logo
            .build().encodeAsBitmap()
    }
}