package com.giovanniburresi.mfr15iotserver.data;

import com.giovanniburresi.mfr15iotserver.tcpserver.WorkerRunnable;

/**
 * Created by Mario on 21/09/2015.
 */
public class IoTClient {

    private String hostname;
    private String ip;
    private String port;
    private IoTService[] Services;
    private WorkerRunnable thread;

    public IoTClient(String hostname, String ip, String port, IoTService[] services, WorkerRunnable thread) {
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
        Services = services;
        this.thread = thread;
    }

    public IoTService[] getServices() {
        return Services;
    }

    public void setServices(IoTService[] services) {
        Services = services;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public WorkerRunnable getThread() {
        return thread;
    }

    public void setThread(WorkerRunnable thread) {
        this.thread = thread;
    }

}
