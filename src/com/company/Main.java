package com.company;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        httpServer server = new httpServer();

        Thread t1 = new Thread(() -> server.runServer(port));
        t1.start();

        Util util = new Util();
        Client client = new Client();

        util.addBlock(new Block(InetAddress.getByName("127.0.0.1"), port));
        util.readFile();

        Map<Long, Block> map = new ConcurrentHashMap<>(util.getBlockMap());

        map.values().forEach(block -> {
            try {
                if (block.getPort() != port){

                    client.post(block.getIp(),block.getPort());
                    System.out.println(map.size());

                    client.get(block.getIp(),block.getPort());
                    System.out.println(map.size());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.println(util.getBlocks());
    }
}
