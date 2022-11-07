package com.giraone.sb3.demo.config;

public record RemoteService(String host, int port) {
    public RemoteService() {
        this("127.0.0.1", 8080);
    }
}
