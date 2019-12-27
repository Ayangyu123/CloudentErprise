package com.ucas.cloudenterprise.utils
import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ucas.cloudenterprise.app.CORE_COMMAND_BIN
import com.ucas.cloudenterprise.app.CORE_WORK_DIR
import com.ucas.cloudenterprise.app.CORE_WORK_CONFIG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileReader
import java.io.InterruptedIOException

operator fun File.get(path: String) = File(this, path)
val Context.store get() = getExternalFilesDir(null)!![CORE_WORK_DIR]
val Context.bin get() = filesDir[CORE_COMMAND_BIN]
val Context.config get() = JsonParser().parse(FileReader(store[CORE_WORK_CONFIG])).asJsonObject

fun Context.config(consumer: JsonObject.() -> Unit) {
    val config = config.apply(consumer)
    val data = GsonBuilder().setPrettyPrinting().create().toJson(config)
    store["config"].writeBytes(data.toByteArray())
}

fun Context.exec(cmd: String) = Runtime.getRuntime().exec(
    "${bin.absolutePath} $cmd",
    arrayOf("IPFS_PATH=${store.absolutePath}")
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
