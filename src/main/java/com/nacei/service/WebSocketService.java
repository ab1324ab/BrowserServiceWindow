package com.nacei.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nacei.enums.RequstSignEnum;
import com.nacei.enums.StatusEnum;
import com.nacei.model.BindingResponseDTO;
import com.nacei.model.ResponseDTO;
import com.nacei.view.MainView;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.UUID;

public class WebSocketService extends WebSocketServer {

    private WebSocket webSocket;
    private String connKey;
    private MainView mainView;

    public WebSocketService(int port, MainView main) {
        super(new InetSocketAddress(port));
        this.mainView = main;
    }

    public WebSocketService(InetSocketAddress address, MainView main) {
        super(address);
        this.mainView = main;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        this.webSocket = webSocket;
        connKey = UUID.randomUUID().toString().replace("-","");
        this.onMessage("{\"cmd\":\"connKey\",\"status\":\""+ StatusEnum.success.getStatus() +"\",\"connKey\":\""+connKey+"\",\"desc\":\"服务已连接\"}");
        WebSocketServiceUtils.addWebSocketConn(connKey,this);
        mainView.statusLabel().setText("<html><span style='color:green'>浏览器已连接</span></html>");
        mainView.setConnKey(connKey);
//        for(int i=0;i<3;i++){
//            boolean isSucc = mainView.bindingInputBoxInterface(null);
//        }

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        mainView.statusLabel().setText("<html><span style='color:red'>浏览器连接关闭</span></html>");
        WebSocketServiceUtils.removeWebSocketConn(connKey);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        //System.out.println("消息");
        JSONObject jsonObject = JSON.parseObject(s);
        System.out.println(s);
        String cmd = jsonObject.getString("cmd");
        if(cmd.equals(RequstSignEnum.service_bind_element.getCmdCode())){
            //TODO 调用mainView页面添加输入框
            boolean isSucc = mainView.bindingInputBoxInterface(jsonObject);
            BindingResponseDTO responseDTO = new BindingResponseDTO();
            responseDTO.setCmd(RequstSignEnum.service_bind_element_success.getCmdCode());
            if(isSucc){
                responseDTO.setElement(jsonObject.getString("element"));
            }else{
                responseDTO.setCode(ResponseDTO.response_fail);
            }
            String dto = JSON.toJSONString(responseDTO);
            this.onMessage(dto);
        }
        //webSocket.send(s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        mainView.statusLabel().setText("<html><span style='color:red'>浏览器连接异常,请重试!</span></html>");
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        mainView.statusLabel().setText("<html><span style='color:green'>服务启动成功等待连接...</span></html>");
    }

    public void onMessage(String string){
        webSocket.send(string);
    }
}
