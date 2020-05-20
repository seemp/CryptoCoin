package com.company;

import java.net.InetAddress;

public class Address {

    private InetAddress ip;
    private int port;
    private String publicKey;

    public Address(InetAddress ip, int port, String publicKey) {
        this.ip = ip;
        this.port = port;
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }


    @Override
    public String toString() {
        return "publicKey: " + publicKey + "ip: " + ip + ", port: " + port;
    }
}