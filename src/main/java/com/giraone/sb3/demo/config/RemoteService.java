package com.giraone.sb3.demo.config;

public class RemoteService {

    private String host = "127.0.0.1";
    private int port = 8080;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "RemoteService{" +
            "host='" + host + '\'' +
            ", port=" + port +
            '}';
    }
}
