package com.service.monitor.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ServiceMonitoringEvent implements Serializable {
    String host;
    int port;
    int pollingFrequency;
    UUID callerUuid;

    Instant outageStart, outageEnd;
    Long graceTime;

    public ServiceMonitoringEvent(String host, int port, int pollingFrequency, UUID callerUuid) {
        this.host = host;
        this.port = port;
        this.pollingFrequency = pollingFrequency;
        this.callerUuid = callerUuid;
    }

    public int getPollingFrequency() {
        return pollingFrequency;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public UUID getCallerUuid() {
        return callerUuid;
    }

    public Instant getOutageStart() {
        return outageStart;
    }

    public void setOutageStart(Instant outageStart) {
        this.outageStart = outageStart;
    }

    public Instant getOutageEnd() {
        return outageEnd;
    }

    public void setOutageEnd(Instant outageEnd) {
        this.outageEnd = outageEnd;
    }

    public Long getGraceTime() {
        return graceTime;
    }

    public void setGraceTime(Long graceTime) {
        this.graceTime = graceTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
