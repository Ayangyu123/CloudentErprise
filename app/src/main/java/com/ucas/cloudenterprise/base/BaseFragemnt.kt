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
import com.lzy.okgo.request.base.Request
import com.lzy.okgo.utils.HttpUtils
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import com.ucas.cloudenterprise.app.*
import com.ucas.cloudenterprise.core.DaemonService
import com.ucas.cloudenterprise.ui.fragment.MyFilesFragment
import com.ucas.cloudenterprise.utils.FileCP
import com.ucas.cloudenterprise.utils.Toastinfo
import com.ucas.cloudenterprise.utils.get
import com.ucas.cloudenterprise.utils.store


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

    override fun onPause() {
        Log.e("ok","onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.e("ok","onStop")
        super.onStop()

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
    fun DeleteFile(
        fileid: String,
        flag:Int,
        tag: Any,
        onNetCallback: BaseActivity.OnNetCallback
    ) {
        val params = HashMap<String, Any>()
        params["file_id"] = "${fileid}"
        params["user_id"] = USER_ID
        params["falg"] = flag
        NetRequest(URL_DELETE_FILE, NET_PUT, params, tag, onNetCallback)

    }
    //</editor-fold>


}
