package com.ucas.cloudenterprise.base

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lzy.okgo.OkGo
import com.ucas.cloudenterprise.app.NET_GET
import com.ucas.cloudenterprise.app.NET_PUT
import com.ucas.cloudenterprise.app.URL_DELETE_FILE
import com.ucas.cloudenterprise.app.USER_ID
import com.ucas.cloudenterprise.ui.MainActivity
import kotlin.collections.set


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

    /* fun GetFenXiangTuPian(){

         val intent:Intent =getIntent()
         val extras = intent.extras
         val action = intent.action
         if (Intent.ACTION_SEND == action) {
             if (extras.containsKey(Intent.EXTRA_STREAM)) {
                 try {
                     // Get resource path from intent
                     val uri2 = extras.getParcelable<Parcelable>(
                         Intent.EXTRA_STREAM
                     ) as Uri

                     // 返回路径getRealPathFromURI
                     val path: String = getRealPathFromURI(, uri2)
                 } catch (e: Exception) {
                     Log.e(this.javaClass.name, e.toString())
                 }
             }
         }

     }*/

  /*   open fun getRealPathFromURI(
        mainActivity: MainActivity,
        uri2: Uri
    ): String {
        val proj = arrayOf(
            MediaStore.Images.Media.DATA
        )
        val cursor: Cursor =
            mainActivity.managedQuery(uri2, proj, null, null, null)
                ?: return uri2.path
        val columnIndexOrThrow = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        // mAba.setText(cursor.getString(columnIndexOrThrow));
        Log.e("123456", "返回图片路径: " + cursor.getString(columnIndexOrThrow))
        return cursor.getString(columnIndexOrThrow)
    }*/

}
