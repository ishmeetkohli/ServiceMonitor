package com.global.relay.monitor;

import java.io.IOException;
import java.net.Socket;

class ServiceMonitor {

//    String host = "127.0.0.1";
//    int port = 8888;


    String host = "www.google.ca";
    int port = 80;

    public static void main(String[] args) {
        ServiceMonitor serviceMonitor = new ServiceMonitor();

        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println(serviceMonitor.isUp() ? "Service is Up" : "Service Down");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isUp() {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            return false;
        }
        if (socket.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}




