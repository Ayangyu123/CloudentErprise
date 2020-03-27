package com.ucas.cloudenterprise.loadclient

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

/**
@author simpler
@create 2020年03月27日  15:51
 */
class DownWebSocketClient(serverUri: URI?,val fileHash:String): WebSocketClient(serverUri) {
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.e("DownWebSocketClient","onOpen")
        Log.e("sendmessgae","{Hash:${fileHash}}")
       send("{Hash:${fileHash}}")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.e("DownWebSocketClient","onClose")
    }

    override fun onMessage(message: String?) {
        Log.e("DownWebSocketClient","onMessage")
        Log.e("DownWebSocketClient","message is  ${message}")
    }

    override fun onError(ex: Exception?) {
        Log.e("DownWebSocketClient","onError")
    }

}