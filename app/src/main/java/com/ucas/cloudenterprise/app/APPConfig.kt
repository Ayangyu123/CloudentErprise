package com.ucas.cloudenterprise.app

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import com.ucas.cloudenterprise.model.Company
import java.io.File
import java.security.AccessControlContext
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
@author simpler
@create 2019年12月27日  14:51
 */
var USER_PHONE = "" //登陆手机号
var COMP_ID = "" //企业ID
var USER_ID = ""
var IS_ROOT = false
var COMP: Company? = null  //企业ID

//<editor-fold desc="core文件目录">
val CORE_COMMAND_BIN = "core_bin" //core命令目录名称

val CORE_WORK_DIR = "core_work" //core工作目录名称

val CORE_WORK_CONFIG = "config" //core工作目录名称

val CORE_CLIENT_ADDRESS = "/ip4/127.0.0.1/tcp/5001" //client 地址
val TEST_DOWN_FiLE_HASH = "QmU6yMLXAku4komAi1bGyh3UwLdDcrY1NQ12QijnKAiNQ8" //client 地址
var ROOT_DIR_PATH =
    ""//Environment.getExternalStorageDirectory().absolutePath+"/ucas.cloudentErprise.down/"""//${USER_ID}/" //client 地址

//</editor-fold>

//<editor-fold desc="core 私钥">
val CORE_WORK_PRIVATE_KEY = "swarm.key"
//</editor-fold>

//<editor-fold desc="core Service（后台服务id） ID">
val CORE_SERVICE_ID = 1

//</editor-fold>

//<editor-fold desc="core 配置文件名称 ">
val CORE_CONFIG_FILE_NAME = "config"
//</editor-fold>

//<editor-fold desc="core config文件 BOOTSTRAP 节点 数组">
val BOOTSTRAPS_ARRAY = arrayOf<String>(
    "/ip4/39.106.216.189/tcp/4001/ipfs/QmabYFoBnCwUYHCx5wU75wtQ57YxqNdHRbLwpX8rVA2Bzo",
    "/ip4/47.95.145.45/tcp/4001/ipfs/QmVU7hgAfBEk7Z9W2nxy6GqLVpX77fREZL2h1D3NZo5Zr1",
    "/ip4/47.95.145.45/tcp/14001/ipfs/QmRiBs6ah7wLLun1K7xixXdN9aNiWfhfJAaDGJ7vrhwYLn",
    "/ip4/47.95.145.45/tcp/14002/ipfs/QmRuZpejRyBo5CesX7LJKqyY6fEucEtGu4fzYDqCt6RwbW",
    "/ip4/47.95.145.45/tcp/14003/ipfs/QmTStppi6kEMk2jewvmTyXHawphgKyQooESXfFePGE8vTs",
    "/ip4/47.95.145.45/tcp/14004/ipfs/QmT8oqQp2bwey8d7xMqxY3CFW3Thk6yCjmZkBCokaKvR84",
    "/ip4/47.95.145.45/tcp/14005/ipfs/QmdMBWAkbBJTvUJpgvDtJczUKKGTfuZFWrA4jozgEPw8AJ",
    "/ip4/47.95.145.45/tcp/14006/ipfs/Qmf82za9i7wd33wgq28q6cCbj1hdVhZHoU61AWg58ZhK92"
)
//</editor-fold>


//<editor-fold desc=" FILE_CHOOSER_RESULT_CODE 文件选择">
val FILE_CHOOSER_RESULT_CODE = 1000

//</editor-fold>
//<editor-fold desc=" APP 设置">
var IS_FIRSTRUN = true
var IS_NOT_INSTALLED = true


//</editor-fold>
//<editor-fold desc=" lib Version Code">
var APP_LAST_VERSION_CODE = 0
var CORE_VERSION_CODE = 0
var PLUGIN_VERSION_CODE = 0
//</editor-fold>

//<editor-fold desc=" CORE_PATH 环境变量">
val CORE_PATH = "SVBGU19QQVRI" // 需要base64 解密

//</editor-fold>
//<editor-fold desc=" sharedpreference 中 对应的存储名称 ">
val PREFERENCE__NAME__FOR_PREFERENCE = "AppSettings" //sharedpreference 名称
val FIRSTRUN_NAME_FOR_PREFERENCE = "isfirstrun"   //第一次运行
val NOT_INSTALLEDE_FOR_PREFERENCE = "notinstall"   //未安装
//</editor-fold>


//<editor-fold desc=" Register_Activity   ">
var vip_type = 0 //是否是VIP 0默认不是 1 是

//</editor-fold>
//<editor-fold desc=" WelcomActivity   ">
val WELCOME_GUIDES = arrayOf(
    com.ucas.cloudenterprise.R.drawable.user_guide_1,
    com.ucas.cloudenterprise.R.drawable.user_guide_2,
    com.ucas.cloudenterprise.R.drawable.user_guide_3
) //欢迎页面图片
//</editor-fold>

//<editor-fold desc=" WelcomActivity   ">
val PERMISSIONTOILLUSTRATE_GUIDES = arrayOf(
    com.ucas.cloudenterprise.R.drawable.can_edit,
    com.ucas.cloudenterprise.R.drawable.can_upload,
    com.ucas.cloudenterprise.R.drawable.can_see
) //欢迎页面图片
//</editor-fold>

//<editor-fold desc=" App主题色   ">
val APP_COLOR = "#4F73DF"
//</editor-fold>

//<editor-fold desc=" TOKEN name   ">
var ACCESS_TOKEN = "token"
var REFRESH_TOKEN = "token"

//</editor-fold>
//<editor-fold desc=" common head name   ">
var APP_ID = "app_id"
var LOGID = "app_id"
var CLIENTTYPE = "app_id"

//</editor-fold>
//<editor-fold desc=" 添加 TOKEN    ">
fun AddToken(token: String) {
    OkGo.getInstance().addCommonHeaders(HttpHeaders("Authorization", "${token}"))
}

//</editor-fold>
//<editor-fold desc=" 输入框长度限制  ">
val MIN_PASSWORD_LENGTH = 4
val MAX_PASSWORD_LENGTH = 16

//</editor-fold>

//<editor-fold desc=" 网络请求参数  ">
//val user_name_param="221669671319900160"
//val user_name_param="18152173267"
val user_name_param = "13716496502"

//val user_name_param="test"
//val password_param= "rdXRcM"
val password_param = "test"

//</editor-fold>
val Salt = "#$^^xsAd.."
fun MD5encode(text: String, AddSaltAble: Boolean): String {

    //对字符串加密，返回字节数组
    var str = if (AddSaltAble) "${text}${Salt}" else "${text}"
    return MD5encode(str.toByteArray())

}

fun MD5encode(bytearray: ByteArray): String {
    try {
        //获取md5加密对象
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val digest: ByteArray = instance.digest(bytearray)
        var sb: StringBuffer = StringBuffer()
        for (b in digest) {
            //获取低八位有效值
            var i: Int = b.toInt() and 0xff
            //将整数转化为16进制
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                //如果是一位的话，补0
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return ""
}

fun MD5encode(file: File): String {
    try {
        //获取md5加密对象
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        if (!file.exists()) {
//            Log.e("ok","文件不存在")
            return ""
        }
        var length: Int = -1
        var bytes = ByteArray(8192)

        while ((file.inputStream().read(bytes).apply { length = this }) != -1) {
            instance.update(bytes, 0, length);
        }
        val digest: ByteArray = instance.digest()
        var sb: StringBuffer = StringBuffer()
        for (b in digest) {
            //获取低八位有效值
            var i: Int = b.toInt() and 0xff
            //将整数转化为16进制
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                //如果是一位的话，补0
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

//</editor-fold>
//<editor-fold desc="检查权限">
fun checkPermission(context: Activity): Boolean? {
    var isGranted = true
    if (android.os.Build.VERSION.SDK_INT >= 23) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //如果没有写sd卡权限
            isGranted = false
        }
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            isGranted = false
        }
        Log.e("appconfig", "isGranted == $isGranted")
        if (!isGranted) {
            context.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                102
            )
        }
    }
    return isGranted
}

//</editor-fold>
//<editor-fold desc="检查sd卡剩余可用容量">
fun CheckFreeSpace(destScope: Long): Boolean {
    var freespace = getFreeSpace()
    if (freespace >= destScope) {
        return true
    }
    return false
}
//</editor-fold>

//<editor-fold desc="获取sd卡剩余可用容量">
fun getFreeSpace(): Long {
    var stat = StatFs(Environment.getExternalStorageDirectory().absolutePath)
    var blockSize = stat.getBlockSizeLong();
    var availableBlocks = stat.getAvailableBlocksLong();
    return availableBlocks * blockSize;


}
//</editor-fold>

//<editor-fold desc="获取app运行内存">
fun getMemory(): Int {
    var activityManager =
        MyApplication.context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    //最大分配内存
    var memory = activityManager.getMemoryClass();
    var memoryinfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryinfo)

    Log.e("ok", "memory: " + memory);
    Log.e("ok", "系统剩余内存:" + (memoryinfo.availMem) + "m")
    Log.e("ok", "系统是否处于低内存运行：" + memoryinfo.lowMemory);
    Log.e("ok", "当系统剩余内存低于" + memoryinfo.threshold + "时就看成低内存运行");

    //最大分配内存获取方法2
    var maxMemory = (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
    //当前分配的总内存
    var totalMemory = (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
    //剩余内存
    var freeMemory = (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
    Log.e("ok", "maxMemory最大分配内存: " + maxMemory);
    Log.e("ok", "totalMemory当前分配的总内存: " + totalMemory);
    Log.e("ok", "freeMemory剩余内存: " + freeMemory);

    return freeMemory.toInt()
}

//</editor-fold>
//<editor-fold desc="下载目录">
fun getRootPath(): String {
    if (ROOT_DIR_PATH.equals("")) {
        ROOT_DIR_PATH = getDeafultDownDir()
    }
    Log.e("ok", ROOT_DIR_PATH)
    return ROOT_DIR_PATH
}

fun getDeafultDownDir() =
    Environment.getExternalStorageDirectory().absolutePath + "/SaturnClound/${USER_PHONE}"
//</editor-fold>

fun main() {
    println("z4VRVt6JKDGCuDtB3q5tiaCqJvspgYaMny66xCCHYR81v".substring(0, 32))
}

