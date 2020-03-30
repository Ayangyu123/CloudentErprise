package com.ucas.cloudenterprise.utils;


import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class WebsocketClient {
    public static WebSocketClient client;
    public static void main(String[] args) {
        try {
           HashMap<String,String> map =  new HashMap<String, String>();
           map.put("Origin", "http://www.bejson.com/");
//           map.put("Access-Control-Allow-Origin", "*");ws://echo.websocket.org
//           map.put("Access-Control-Allow-Origin", "*"); draft_17
            client = new WebSocketClient(new URI("ws://192.168.0.11:9984/api/v0/ws/down"),new Draft_6455()
                    ,map
            ) {
//            client = new WebSocketClient(new URI("ws://121.40.165.18:8800"),new Draft_6455()) {
//            client = new WebSocketClient(new URI("ws://123.207.136.134:9010/ajaxchattest"),new Draft_6455()
//            ) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("握手成功");
                }

                @Override
                public void onMessage(String msg) {
                    System.out.println("收到消息=========="+msg);
                    if(msg.equals("over")){
                        client.close();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("链接已关闭");
                    System.out.println("链接已关闭 "+s);
                }

                @Override
                public void onError(Exception e){
                    e.printStackTrace();
                    System.out.println("发生错误已关闭");
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client.connect();
        //print.info(client.getDraft());
//        while(!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
////            System.out.println("正在连接...");
//        }
        //连接成功,发送信息
        client.send("哈喽,连接一下啊");
    }
    }


