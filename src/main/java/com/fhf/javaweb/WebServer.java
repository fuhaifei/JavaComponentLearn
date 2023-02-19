package com.fhf.javaweb;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebServer {
    private static class Handler extends Thread{
        Socket socket;
        public Handler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                //按照协议读取数据
                String[] requestInfo = reader.readLine().split(" ");
                if(requestInfo[0].equals("GET")){
                    this.handleGet(requestInfo[1], writer);
                }else{
                    //暂不支持其他类型
                    this.notFound(requestInfo[0], writer);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void handleGet(String url, BufferedWriter writer) throws IOException {
            //首先构建响应体
            StringBuilder data = new StringBuilder("<html><body><h1>Hello World,You Request has no parameter</h1></body></html>");
            int paramIndex = url.indexOf('?');
            if(paramIndex != -1){
                String[] allParams = url.substring(paramIndex + 1).split("&");
                data = new StringBuilder("<html><body><h1>Hello World,Your Request params are:");
                for(int i = 0;i < allParams.length;i++){
                    data.append(i + 1).append(".").append(allParams[i]).append(';');
                }
                data.append("</h1></body></html>");
            }
            int length = data.toString().getBytes(StandardCharsets.UTF_8).length;
            writer.write("HTTP/1.0 200 OK\r\n");
            writer.write("Connection: close\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: " + length + "\r\n");
            writer.write("\r\n"); // 空行标识Header和Body的分隔
            writer.write(data.toString());
            writer.flush();
            socket.close();
        }

        public void notFound(String requestType, BufferedWriter writer) throws IOException {

            String data = "<html><body><h1>Sorry,We not support " + requestType + " request</h1></body></html>";
            int length = data.getBytes(StandardCharsets.UTF_8).length;
            writer.write("HTTP/1.0 404 Not Found\r\n");
            writer.write("Connection: close\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: " + length + "\r\n");
            writer.write("\r\n"); // 空行标识Header和Body的分隔
            writer.write(data);
            writer.flush();
            socket.close();
        }
    }
    public static void startServer(){
        try {
            //绑定端口号
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Webserver is start to work");
            while(true){
                Socket accept = serverSocket.accept();
                //将
                new Handler(accept).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        startServer();
    }

}
