package com.service.monitor;

import com.service.monitor.constants.Constants;
import com.service.monitor.util.NotificationManager;
import com.service.monitor.model.ServiceMonitoringEvent;
import com.service.monitor.util.StatusCheckTask;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageServer extends Thread {

    HashMap<Integer, StatusCheckTask> taskMap;
    ExecutorService taskPool;
    NotificationManager notificationManager;

    public MessageServer() {
        taskMap = new HashMap<>();
        taskPool = Executors.newCachedThreadPool();
        notificationManager = NotificationManager.getInstance();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(Constants.SOCKET_PORT);
                Socket clientSocket = serverSocket.accept();

                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                ServiceMonitoringEvent event = (ServiceMonitoringEvent) objectInputStream.readObject();
                registerEvent(event);
                notificationManager.getSocketMap().put(event.getCallerUuid(), clientSocket);
                serverSocket.close();
            } catch (Exception e) {
                // LOG SERVER EXCEPTION HERE
            }
        }
    }

    private void registerEvent(ServiceMonitoringEvent event) {
        if(event.getGraceTime() != null) {
            notificationManager.setGraceTime(event.getGraceTime());
        }

        if (taskMap.containsKey(event.hashCode())) {
            StatusCheckTask task = taskMap.get(event.hashCode());
            if (event.getPollingFrequency() < task.getPollingFrequency()) {
                if(event.getPollingFrequency() < Constants.POLLING_THRESHOLD) {
                    task.setPollingFrequency(Constants.POLLING_THRESHOLD);
                } else {
                    task.setPollingFrequency(event.getPollingFrequency());
                }
                task.getCallerList().add(event.getCallerUuid());
            }
        } else {
            StatusCheckTask task = new StatusCheckTask(event);
            taskPool.submit(task);
        }
    }

    public static void main(String[] args) {
        MessageServer messageServer = new MessageServer();
        messageServer.start();
    }
}
