package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    static private Client client = new Client();
    public static int port;

    public static void main(String[] args) throws Exception {

        port = Integer.parseInt(args[0]);
        Util util = new Util();
        httpServer server = new httpServer();

        Thread t1 = new Thread(() -> server.runServer(port));
        t1.start();

        util.addBlock(new Block(InetAddress.getByName("127.0.0.1"), port));
        util.readFile();

        Map<Long, Block> map = new ConcurrentHashMap<>(util.getBlockMap());

        getAndPostAll(port, map);
    }

    public static void getAndPostAll(int port, Map<Long, Block> map) {
        map.values().forEach(block -> {
            try {
                if (block.getPort() != port){
                    client.post(block.getIp(),block.getPort());
                    client.get(block.getIp(),block.getPort());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //for non local
    private static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            System.out.println(ip);
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
