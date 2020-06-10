package com.ucas.cloudenterprise.utils
import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ucas.cloudenterprise.app.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*

operator fun File.get(path: String) = File(this, path)
val Context.store get() = getExternalFilesDir(null)!![CORE_WORK_DIR]
//val Context.cache get() = externalCacheDir(null)!![""]

val Context.bin get() = filesDir[CORE_COMMAND_BIN]
val Context.cacheDir get() = cacheDir
val Context.pluginbin get() = filesDir["Plugin"]
val Context.config get() = JsonParser().parse(FileReader(store[CORE_WORK_CONFIG])).asJsonObject

fun Context.config(consumer: JsonObject.() -> Unit) {
    val config = config.apply(consumer)
    val data = GsonBuilder().setPrettyPrinting().create().toJson(config)
    store[CORE_WORK_CONFIG].writeBytes(data.toByteArray())
}

fun Context.exec(cmd: String) = Runtime.getRuntime().exec(
    "${bin.absolutePath} $cmd",
    arrayOf(String(Base64.getDecoder().decode(CORE_PATH),StandardCharsets.UTF_8 )+"=${store.absolutePath}")//此处字符串为环境变量

)

fun Process.read(consumer: (String) -> Unit) {
    listOf(inputStream, errorStream).forEach { stream ->
        GlobalScope.launch {
            try {
                stream.bufferedReader().forEachLine { consumer(it) }
            } catch (ex: InterruptedIOException) {
            }
        }
    }
}
fun AssetFileCP(context:Context,filename:String)= AssetFileCP(context,filename,context.store[filename])

fun AssetFileCP(context:Context,src:String,dest:File)= FileCP(context.assets.open(src),dest.outputStream())

fun FileCP(inputStream: InputStream,outputStream: OutputStream){
    try {
        inputStream.copyTo(outputStream)
    }finally {
        inputStream.close();
        outputStream.close()
    }

}
fun main(){
    println(FormatFileSize(10))
}
fun FormatFileSize(filesize:Long):String{
    if(filesize<1024){ //B
        return  "${String.format("%.2fB",filesize.toFloat())}"
    }

    if(filesize<(1024*1024)){ //1048576  MB
        return  "${String.format("%.2fKB",filesize.toFloat()/(1024))}"
    }
    if(filesize<(1024*1024*1024)){ //1073 7418 24  G
        return  "${String.format("%.2fMB",filesize.toFloat()/(1024*1024))}"
    }else{
        return  "${String.format("%.2fGB",filesize.toFloat()/(1024*1024*1024))}"
    }
    return ""
}