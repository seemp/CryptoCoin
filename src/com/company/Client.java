package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private Util util = new Util();

    public void get(InetAddress ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // send an HTTP request to the web server
        out.println("GET /blocks HTTP/1.0");
        out.println("Host: 127.0.0.1: "+port);
        out.println("User-Agent: Simple Http Client");
        out.println("Content-Type: application/json");
        out.println("Accept-Language: en-US");
        out.println("Connection: Close");
        out.println();
        // read the response
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        util.addBlocks(sb.toString().substring(sb.indexOf("{"),sb.indexOf("END")));
        System.out.println(util.getBlocks());
        socket.close();
    }

    public void post(InetAddress ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // send an HTTP request to the web server
        out.println("POST /blocks HTTP/1.0");
        out.println("Content-Length: " + util.getBlocks().getBytes().length);
        out.println("Content-Type: application/json");
        out.println(util.getBlocks()+"END");
        out.println();
        // read the response
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        System.out.println(sb.toString());
        socket.close();
    }
}
