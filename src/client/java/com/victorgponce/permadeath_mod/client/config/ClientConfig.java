package com.victorgponce.permadeath_mod.client.config;

public class ClientConfig {
    private String serverAddress;
    private int serverPort;
    private boolean enabledServerCheck;

    public String getServerAddress() {return serverAddress;}
    public void setServerAddress(String serverAddress) {this.serverAddress = serverAddress;}
    public int getServerPort() {return serverPort;}
    public void setServerPort(int serverPort) {this.serverPort = serverPort;}
    public boolean isEnabledServerCheck() {return enabledServerCheck;}
    public void setEnabledServerCheck(boolean enabledServerCheck) {this.enabledServerCheck = enabledServerCheck;}
}
