package com.ucas.cloudenterprise.ui

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.ucas.cloudenterprise.R
import com.ucas.cloudenterprise.base.BaseActivity
import kotlinx.android.synthetic.main.activity_commom_web.*
import kotlinx.android.synthetic.main.common_head.*

/**
@author simpler
@create 2020年03月16日  17:06
 */
class CommonWebActivity:BaseActivity(){
    override fun GetContentViewId() = R.layout.activity_commom_web

    override fun InitView() {
        tv_edit.visibility = View.GONE
        tv_title.text="服务协议"
        iv_back.setOnClickListener { finish() }
    }

    override fun InitData() {
        webview.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
            }
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()

//        }.loadUrl("https://www.baidu.com/")
        }.loadUrl("http://www.saturncloud.com.cn/#/agreement")
//        }.loadUrl("file:///android_asset/服务协议.docx")
    }
}