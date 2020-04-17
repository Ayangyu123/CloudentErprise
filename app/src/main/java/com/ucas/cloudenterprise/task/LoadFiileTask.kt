package com.ucas.cloudenterprise.task

import android.graphics.Color.pack
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.convert.StringConvert
import com.lzy.okgo.model.Response
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.model.CompletedFile
import com.ucas.cloudenterprise.model.File_Bean
import com.ucas.cloudenterprise.model.LoadIngStatus
import com.ucas.cloudenterprise.task.TaskStatus.COMPLETED
import com.ucas.cloudenterprise.task.UpLoadTasKSetp.UPLOAD_TRANSFERING
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.VerifyUtils
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.io.File
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
@author simpler
@create 2020年04月07日  13:55
 */
class LoadFiileTask(val load_type_falg:Int,  //0  up  1 down
                    val file_name:String,
                    val file_MD5:String?=null,
                    val file_hash:String?=null,
                    val file_size:Long,
                    val dest_file: File?=null,
                    val pid :String?=null,
                    var Ingstatus:Int = LoadIngStatus.WAITING,
                    var progress:Int=0,
                    var Speed:String="",
                    var src_file_info: File_Bean?=null):Serializable,Runnable{


        companion object{
            val UP_LOAD_FLAG=0
            val DWON_LOAD_FLAG=1
            val  INIT=1001
        }

      var CurrSetp:Int?=null //load 当前加载进度
       var CurrTaskStatus:Int=TaskStatus.WAITING // 任务状态

      var AES_KEY:String?=null //AESKEY
      var Multihash:String?=null

      var FileTransferClient: WebSocketClient? =null

    override fun run() {

        //检查core服务是否启动
        if(CheckCoreIsRun()==false){
            return
        }
        if(CheckSetp()){
            //第一次加载
            CurrSetp=INIT
            CurrTaskStatus = TaskStatus.RUNING
        }
        //判断Load类型
        when(load_type_falg){
            UP_LOAD_FLAG->{ //上传
                UpLoadSetp(CurrSetp)
            }
            DWON_LOAD_FLAG->{// 下载
                DownLoadSetp(CurrSetp)
            }
        }


    }
    // <editor-fold desc="上传步骤操作">
    fun UpLoadSetp(Setp: Int?) {
        when(Setp){
            INIT->{ //初始化状态
                getAeskey() //获取aeskey
            }
            UpLoadTasKSetp.GET_AES_KEY->{ // 已经获取完成AESKEY
                pack() //对文件进行压缩加密
            }

            UpLoadTasKSetp.PACK->{ // 已经获取完成AESKEY
                fileTransfer()//对文件传输
            }

            UpLoadTasKSetp.UPLOAD_TRANSFERING->{ // 已经获取完成AESKEY
                UploadFileMetaInfo()
            }
            UpLoadTasKSetp.UPLOAD_META_INFO->{ // 已经获取完成AESKEY
                    //运行状态修改
                CurrTaskStatus=COMPLETED
                if(FileTransferClient!=null&&FileTransferClient!!.isClosed){
                    FileTransferClient =null
                    Multihash = null
                    AES_KEY = null
                }

            }

        }
    }
    // </editor-fold >

    // <editor-fold desc="上传文件信息">
    private fun UploadFileMetaInfo() {
        OkGo.post<String>(URL_ADD_File).upJson(JSONObject().apply {
            put("file_name",file_name + "")
            put("is_dir",IS_FILE)
            put("user_id", "$USER_ID" )
            put("fidhash", "$file_hash" )
            put("filehash", "$file_MD5" )
            put("pid", "$pid" )
            put("size", file_size )
        }).tag(Multihash).converter(StringConvert()).adapt().execute()?.body()?.apply {
            if(VerifyUtils.VerifyRequestData(this)){
                Toastinfo("${file_name} 上传完成")
//
                MyApplication.upLoad_completed.add(0,
                    CompletedFile("${file_name}"
                        , SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                            Date()
                        )
                        ,file_size.toString(),
                        false)
                )

                for (loadingFile in MyApplication.upLoad_Ing) {
                    if(loadingFile.dest_file?.name.equals(file_name)){
                        MyApplication.upLoad_Ing.remove(loadingFile)
                        break
                    }
                }

                ChangeCurrSetpAndLoadfile(UpLoadTasKSetp.UPLOAD_META_INFO)
            }

        }
    }


    // </editor-fold >
    // <editor-fold desc="文件传输">
    private fun fileTransfer() {//文件传输
        if(FileTransferClient==null){
            FileTransferClient =object :WebSocketClient(
                URI.create("ws://127.0.0.1:9984/api/v0/ws/${if(load_type_falg==UP_LOAD_FLAG) "up" else "down"}"),
                Draft_6455()
                ,HashMap<String,String>().apply {put("Origin", "http://www.bejson.com/") }){

                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.e("WebSocketClient","onOpen")
                    if(load_type_falg== DWON_LOAD_FLAG) {
                        send(JSONObject(HashMap<String, String>().apply {
                            put("Hash", file_hash!!)
                        }).toString())
                        unpack()
                    }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.e("WebSocketClient","onClose")
                    Log.e("WebSocketClient","reason is ${reason}")
                    Log.e("WebSocketClient","code is ${code}")
                    Log.e("WebSocketClient","remote is ${remote}")

                }

                override fun onMessage(message: String?) {
                    Log.e("WebSocketClient","onMessage ${message}")
                    Log.e("WebSocketClient","uptask.Ingstatus is  ${Ingstatus}")
                    if(CurrTaskStatus!=TaskStatus.RUNING){
                        close()
                    }
                    if(TextUtils.isEmpty(message)){
                        return
                    }

                    JSONObject(message).apply {
                        progress= (getDouble("Percent")*100).toInt()
                        //TODO 速度显示
                        getString("Speed")?.apply {
                            if(this.toLong()!=0L)
                               Speed=(this.toLong()/1024).toString()+"KB/s"
                        }
                        if( progress==100){
//                            ChangeCurrSetpAndLoadfile(UPLOAD_TRANSFERING)
                            close()
                        }

                    }

                }

                override fun onError(ex: java.lang.Exception?) {
                    Log.e("WebSocketClient","onError")
                }

            }.apply {
                setConnectionLostTimeout(0)
            }

        }
        FileTransferClient!!.setConnectionLostTimeout(0)
        FileTransferClient!!.connectBlocking()



    }
    // </editor-fold >


    // <editor-fold desc="下载步骤操作">
    fun DownLoadSetp(Setp: Int?) {
        when(Setp){
            INIT->{ //初始化状态
                CurrTaskStatus =TaskStatus.RUNING
                fileTransfer()

            }
//            DownLoadTasKSetp.DOWN_LOAD_TRANSFERING->{ // 已经获取完成AESKEY
//
//            }

            DownLoadTasKSetp.UNPACK->{ // 已经获取完成AESKEY

                CurrTaskStatus =COMPLETED
                if(FileTransferClient!=null&&FileTransferClient!!.isClosed){
                    FileTransferClient =null
                    Multihash = null
                    AES_KEY = null
                }
            }

        }
    }
    // </editor-fold >

    // <editor-fold desc="解密解压">
    private fun unpack() {
        if(ROOT_DIR_PATH.equals("")){
            ROOT_DIR_PATH = Environment.getExternalStorageDirectory().absolutePath+"/ucas.cloudentErprise.down/${USER_ID}"
        }

        val root =  File(ROOT_DIR_PATH)
        if(!root.exists()){
            root.mkdirs()
        }
        Log.e("ok","destroot="+ ROOT_DIR_PATH.substring(0, ROOT_DIR_PATH.length-2))
        OkGo.post<File>("http://127.0.0.1:9984/api/v0/unpack")
            .params("hash","${file_hash}")
            .isMultipart(true)
            .execute(object :
                FileCallback(ROOT_DIR_PATH,file_name){
                override fun onSuccess(response: Response<File>?) {

                    MyApplication.downLoad_completed.add(CompletedFile(file_name,SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                        Date()
                    )
                        ,file_size.toString(),
                        false))
//                    MyApplication.downLoad_Ing.remove(task)
                    Log.e("it","文件路径 ${response?.body()?.absolutePath}")
                    Toastinfo("${file_name} 下载完成")
                    ChangeCurrSetpAndLoadfile(DownLoadTasKSetp.UNPACK)
                }

            })
    }

    // </editor-fold >




    // <editor-fold desc="文件进行压缩加密">
     fun pack() {
        var Uploadfile_adapt= OkGo.post<String>("http://127.0.0.1:9984/api/v0/pack")
            .params("flag", JSONObject().apply {
                put("MetaHash",file_MD5)
                put("MetaSize",file_size)
                put("Gzip",true)
                put("Aes",true)
                put("AesKey",AES_KEY)
                put("RS",true)
                put("Feed",true)
            }.toString())
            .params("file", dest_file)
            .isMultipart(true)
            .tag(file_MD5).converter(StringConvert()).adapt()
        val pack_start_time=java.lang.System.currentTimeMillis()
        Log.e("WebSocketClient","start pacak ${pack_start_time}")
        val Uploadfile_response: Response<String> =  Uploadfile_adapt.execute()
        Multihash= JSONObject(Uploadfile_response.body().toString()).getJSONObject("Encrypt").getString("Multihash")
        val pack_end_time=java.lang.System.currentTimeMillis()
        Log.e("WebSocketClient","end pacak $pack_end_time ")
        Log.e("WebSocketClient"," pacak 耗时 ${(pack_end_time-pack_start_time)/1000 }")
        ChangeCurrSetpAndLoadfile(UpLoadTasKSetp.PACK)
    }
    // </editor-fold >

    // <editor-fold desc="获取aeskey">
    fun getAeskey() {
       AES_KEY= OkGo.get<String>("http://127.0.0.1:9984/api/v0/aes/key/random").tag(file_MD5).converter(
            StringConvert()
        ).adapt().execute()?.body()
        ChangeCurrSetpAndLoadfile(UpLoadTasKSetp.GET_AES_KEY)
    }
    // </editor-fold >


    // <editor-fold desc="修改setp并调用LoadFileStatusBySetp方法">
    fun  ChangeCurrSetpAndLoadfile(Setp: Int){
        CurrSetp =Setp
        //判断Load类型
        when(load_type_falg){
            UP_LOAD_FLAG->{ //上传
                UpLoadSetp(CurrSetp)
            }
            DWON_LOAD_FLAG->{// 下载
                DownLoadSetp(CurrSetp)
            }
        }
    }
    // </editor-fold >

    // <editor-fold desc="检查core服务是否启动">
     fun CheckCoreIsRun():Boolean {
        if(DaemonService.daemon ==null){
            Toastinfo("Daemon 未启动")
            return false
        }
        if(DaemonService.plugindaemon ==null){
            Toastinfo("pulgDaemon 未启动")
            return false
        }
        return true
    }

    // </editor-fold >

    // <editor-fold desc="是否第一次加载">
    fun  CheckSetp():Boolean{
        return   CurrSetp==null
    }
    // </editor-fold >
}
object UpLoadTasKSetp{
    val GET_AES_KEY=20001 //获取用于加密的aeskey（AES密钥）
    val PACK = 20002  //压缩加密
    val UPLOAD_TRANSFERING = 10001 //文件上传
    val UPLOAD_META_INFO = 20003 //上传文件信息
}
object DownLoadTasKSetp{
    val DOWN_LOAD_TRANSFERING = 10001 //文件下载
    val UNPACK = 10002  //解密解压
}
object  TaskStatus{
    val WAITING =101
    val RUNING  =102
    val ERROR = 103
    val COMPLETED = 104
    val STOP =105
}