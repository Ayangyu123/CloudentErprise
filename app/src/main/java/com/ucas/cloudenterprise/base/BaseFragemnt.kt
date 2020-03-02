package com.ucas.cloudenterprise.base

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lzy.okgo.OkGo
import com.lzy.okgo.utils.HttpUtils
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.fragment.MyFilesFragment
import com.ucas.cloudenterprise.utils.FileCP
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.get
import com.ucas.cloudenterprise.utils.store
import io.ipfs.api.IPFS
import io.ipfs.api.NamedStreamable

abstract class BaseFragment : Fragment() {

    var NetTag: Any? = null
    var mContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = inflater.context
        return inflater.inflate(GetRootViewID(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    fun NetRequest(
        url: String,
        RequestMethod: Int,
        paramsjson: HashMap<String, Any>?,
        tag: Any,
        onNetCallback: BaseActivity.OnNetCallback
    ) {
        this.NetTag = tag
        (activity as BaseActivity).NetRequest(url, RequestMethod, paramsjson, tag, onNetCallback)

    }

    override fun onDestroy() {
        super.onDestroy()
        NetTag?.let {
            OkGo.getInstance().cancelTag(NetTag)
        }

    }

    abstract fun initView()

    abstract fun initData()

    abstract fun GetRootViewID(): Int

    //<editor-fold desc="获取文件列表">
    fun GetFilesListForNet(
        url: String,
        tag: Any,
        onNetCallback: BaseActivity.OnNetCallback
    ) {
        NetRequest(url, NET_GET, null, tag, onNetCallback)
    }
    //</editor-fold>

    //<editor-fold desc=" 添加文件夹  ">
    fun CreateNewDir(
        dirname: String, iscommon: Boolean,
        pid: String,
        tag: Any,
        onNetCallback: BaseActivity.OnNetCallback
    ) {
        //TODO 请求创建新的文件夹
        (activity as BaseActivity).CreateNewDir(dirname, iscommon, pid, tag, onNetCallback)

    }
    //</editor-fold>

    //<editor-fold desc=" 添加文件  ">
    fun AddFile(
        uri: String,
        pid: String,
        tag: Any,
        onNetCallback: BaseActivity.OnNetCallback
    ) {
        val uri = Uri.parse(uri) // 获取用户选择文件的URI
        Log.e("uri)", "data.getData()=" + uri)
        Log.e("uri)", "uri.getScheme()=" + uri.getScheme())
        Log.e("uri)", "uri.authority=" + uri.authority)
        val cursor = activity!!.contentResolver.query(uri, null, null, null, null, null)
        var displayName: String? = null
        cursor?.use {
            if (it.moveToFirst()) {
                displayName =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                val size: Int =
                    it.getInt(it.getColumnIndex(OpenableColumns.SIZE))

                if (DaemonService.daemon != null && DaemonService.daemon!!.isAlive) {
                    Thread(Runnable {

                        FileCP(
                            activity!!.contentResolver.openInputStream(uri)!!,
                            activity!!.store[displayName!!].outputStream()
                        )

                        val ipfs = IPFS(CORE_CLIENT_ADDRESS)
                        val src = activity!!.store[displayName!!]
                        val srcmd5= MD5encode(src.readBytes())
                        val srcsize = src.length()
                        val file = NamedStreamable.FileWrapper(src)
                        val addResult = ipfs.add(file)[0]

                        Log.e("ifileContents", "addResult=" + addResult)
                        activity!!.store[displayName!!].delete()
                        runOnUiThread() {
                            addResult.apply {
                                val params = HashMap<String, Any>()
                                params["file_name"] = displayName + ""
                                params["is_dir"] = IS_FILE
                                params["user_id"] = "${USER_ID}" //TODO
                                params["fidhash"] = "${hash}"
                                params["pid"] = pid
                                params["size"] = size
                                NetRequest(URL_ADD_File, NET_POST, params, tag, onNetCallback)

                            }
                        }
                    }).start()
                } else {
                    Toastinfo("core服务未启动")
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="删除文件">
    fun DeleteFile(
        fileid: String,
        tag: Any,
        onNetCallback: BaseActivity.OnNetCallback
    ) {
        val params = HashMap<String, Any>()
        params["file_id"] = "${fileid}"
        params["user_id"] = USER_ID
        NetRequest(URL_DELETE_FILE, NET_PUT, params, tag, onNetCallback)

    }
    //</editor-fold>


}
