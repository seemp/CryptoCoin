package com.company;

import java.net.InetAddress;

public class Block {

    private InetAddress ip;
    private int port;

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Block(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public long getToken() {
       return getPort();
    }

    @Override
    public String toString() {
        return "ip: " + ip + ", port: " + port;
    }
}