package com.service.monitor.util;

import com.service.monitor.constants.ServiceStatus;
import com.service.monitor.model.ServiceMonitoringEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatusCheckTask implements Runnable {
    NotificationManager notificationManager;

    int pollingFrequency;
    String host;
    int port;
    ServiceStatus serviceStatus;
    Instant outageStart, outageEnd;
    Instant downSince;
    List<UUID> callerList;

    public StatusCheckTask(ServiceMonitoringEvent event) {
        this.notificationManager = NotificationManager.getInstance();
        this.pollingFrequency = event.getPollingFrequency();
        this.host = event.getHost();
        this.port = event.getPort();
        this.outageStart = event.getOutageStart();
        this.outageEnd = event.getOutageEnd();
        callerList = new ArrayList<>();
        callerList.add(event.getCallerUuid());
        serviceStatus = null;
        downSince = null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (outageStart != null && outageStart.isBefore(outageEnd) && Instant.now().isAfter(outageStart)
                        && Instant.now().isBefore(outageEnd)) {
                    Duration between = Duration.between(Instant.now(), outageEnd);
                    Thread.sleep(between.toMillis());
                }

                Socket socket = new Socket(host, port);
                if (socket.isConnected()) {
                    notifyCallers(ServiceStatus.UP);
                } else {
                    notifyCallers(ServiceStatus.DOWN);
                }
            } catch (UnknownHostException e) {
                notifyCallers(ServiceStatus.DOWN);
            } catch (IOException e) {
                notifyCallers(ServiceStatus.DOWN);
            } catch (InterruptedException e) {
                // LOG SERVER EXCEPTION HERE
            }

            try {
                if(downSince != null && notificationManager.getGraceTime() < pollingFrequency) {
                    Thread.sleep(notificationManager.getGraceTime());
                } else {
                    Thread.sleep(pollingFrequency);
                }
            } catch (InterruptedException e) {
                // LOG SERVER EXCEPTION HERE
            }
        }
    }

    private void notifyCallers(ServiceStatus status) {

        switch (status) {
            case UP:
                if(serviceStatus == null || serviceStatus == ServiceStatus.DOWN) {
                    serviceStatus = ServiceStatus.UP;
                    notificationManager.notifyCallers(callerList, ServiceStatus.UP);
                    downSince = null;
                }
                break;
            case DOWN:
                if(serviceStatus == null || serviceStatus == ServiceStatus.UP) {
                    serviceStatus = ServiceStatus.DOWN;
                    downSince = Instant.now();
                }

                if(Instant.now().isAfter(downSince.plus(notificationManager.getGraceTime(), ChronoUnit.MILLIS))) {
                    notificationManager.notifyCallers(callerList, ServiceStatus.DOWN);
                    downSince = null;
                }
                break;
        }

    }

    public int getPollingFrequency() {
        return pollingFrequency;
    }

    public void setPollingFrequency(int pollingFrequency) {
        this.pollingFrequency = pollingFrequency;
    }

    public List<UUID> getCallerList() {
        return callerList;
    }
}
