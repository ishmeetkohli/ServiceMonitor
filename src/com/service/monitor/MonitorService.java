package com.service.monitor;

import com.service.monitor.constants.Constants;
import com.service.monitor.model.ServiceMonitoringEvent;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.UUID;

public class MonitorService {

    public static void main(String[] args) throws IOException {
        UUID callerUUID = UUID.randomUUID();
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int pollingFrequency = Integer.parseInt(args[2]);
        ServiceMonitoringEvent event = new ServiceMonitoringEvent(host, port, pollingFrequency, callerUUID);

        if(args.length > 3 && args[3] != null) {
            Instant outageStart = Instant.parse(args[3]);
            Instant outageEnd = Instant.parse(args[4]);

            event.setOutageStart(outageStart);
            event.setOutageEnd(outageEnd);
        }

        if(args.length > 5 && args[5] != null) {
            Long graceTime = Long.parseLong(args[5]);
            event.setGraceTime(graceTime);
        }

        Socket socket = new Socket(Constants.LOCAL_HOST, Constants.SOCKET_PORT);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(event);

        InputStream inputStream = socket.getInputStream();
        BufferedReader receiveRead = new BufferedReader(new InputStreamReader(inputStream));

        String receivedMessage = null;
        try {
            while (true) {
                if ((receivedMessage = receiveRead.readLine()) != null) {
                    System.out.println("Service is : " + receivedMessage);
                }
            }
        } finally {
            socket.close();
        }
    }
}
