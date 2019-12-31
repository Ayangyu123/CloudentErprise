package com.ucas.cloudenterprise.utils
import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ucas.cloudenterprise.app.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileReader
import java.io.InterruptedIOException
import java.nio.charset.StandardCharsets
import java.util.*

operator fun File.get(path: String) = File(this, path)
val Context.store get() = getExternalFilesDir(null)!![CORE_WORK_DIR]
val Context.bin get() = filesDir[CORE_COMMAND_BIN]
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
fun AssetFileCP(context:Context,filename:String){
    AssetFileCP(context,filename,context.store[filename])

}
fun AssetFileCP(context:Context,src:String,dest:File){
    val input_swarm_key = context.assets.open(src)
    val output_swarm_key =dest.outputStream()
    try {

        input_swarm_key.copyTo(output_swarm_key)

    }finally {
        input_swarm_key.close();
        output_swarm_key.close()
    }

}
