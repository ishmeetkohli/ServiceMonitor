package com.service.monitor.util;

import com.service.monitor.constants.ServiceStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NotificationManager {

    private static NotificationManager instance;
    private HashMap<UUID, Socket> socketMap;
    private Long graceTime;

    public static NotificationManager getInstance() {
        if(instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    private NotificationManager() {
        this.socketMap = new HashMap<>();
        this.graceTime = 0l;
    }

    public HashMap<UUID, Socket> getSocketMap() {
        return socketMap;
    }

    public Long getGraceTime() {
        return graceTime;
    }

    public void setGraceTime(Long graceTime) {
        this.graceTime = graceTime;
    }

    public void notifyCallers(List<UUID> callerList, ServiceStatus status) {
        for(UUID callerUUID : callerList) {
            notifyCaller(callerUUID, status);
        }
    }

    public void notifyCaller(UUID callerUUID, ServiceStatus status){
        Socket clientSocket = socketMap.get(callerUUID);
        try {
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            printWriter.println(status.name());
        } catch (IOException e) {
            // LOG SERVER EXCEPTION HERE
        }
    }
}
