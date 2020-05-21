package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private AddressUtil util = new AddressUtil();

    public void getAddress(InetAddress ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // send an HTTP request to the web server
        out.println("GET /address HTTP/1.0");
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
        System.out.println("GET response:" + sb);
        util.addAddress(sb.toString().substring(sb.indexOf("{"),sb.indexOf("END")));
        socket.close();
    }

    public void getBlocks(InetAddress ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // send an HTTP request to the web server
        out.println("GET /blocks HTTP/1.0");
        out.println("Host: 127.0.0.1: " + port);
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

        Main.addBlocksJSON(sb.toString().substring(sb.indexOf("["),sb.indexOf("END")));

        socket.close();
    }

    public void postAddress(InetAddress ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("POST /address HTTP/1.0");
        out.println("Content-Length: " + util.getAddress().getBytes().length);
        out.println("Content-Type: application/json");
        out.println(util.getAddress()+"END");
        out.println();

        ReadTheResponse(socket);
    }

    public void postTransaction(InetAddress ip, int port, Transaction transaction) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("POST /transaction HTTP/1.0");
        out.println("Content-Length: " + StringUtil.getJson(transaction).getBytes().length);
        out.println("Content-Type: application/json");
        out.println(StringUtil.getJson(transaction)+"END");
        out.println();

        ReadTheResponse(socket);
    }

    public void postBlock(InetAddress ip, int port, Block block) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("POST /block HTTP/1.0");
        out.println("Content-Length: " + StringUtil.getJson(block).getBytes().length);
        out.println("Content-Type: application/json");
        out.println(StringUtil.getJson(block)+"END");
        out.println();

        ReadTheResponse(socket);
    }

    public void postBlocks(InetAddress ip, int port, ArrayList<Block> blockMap) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("POST /blocks HTTP/1.0");
        out.println("Content-Length: " + StringUtil.getJson(blockMap).getBytes().length);
        out.println("Content-Type: application/json");
        out.println(StringUtil.getJson(blockMap)+"END");
        out.println();

        ReadTheResponse(socket);
    }

    private void ReadTheResponse(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        System.out.println("POST response:" + sb.toString());
        socket.close();
    }
}
