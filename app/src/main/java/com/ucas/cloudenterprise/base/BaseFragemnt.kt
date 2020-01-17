package com.ucas.cloudenterprise.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lzy.okgo.OkGo

abstract class BaseFragemnt : Fragment()  {

    var NetTag: Any ? =null
    var mContext:Context ? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext =  inflater.context
        return inflater.inflate(GetRootViewID(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }
    fun NetRequest(url:String,RequestMethod:Int,paramsjson:HashMap<String,Any>?,tag:Any,onNetCallback: BaseActivity.OnNetCallback){
        this.NetTag =tag
        (activity as BaseActivity).NetRequest(url,RequestMethod,paramsjson,tag,onNetCallback)

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


}
