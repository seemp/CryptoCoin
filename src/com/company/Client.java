package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

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
        System.out.println(util.getBlocks());
        socket.close();
    }

    public void postAddress(InetAddress ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // send an HTTP request to the web server
        out.println("POST /address HTTP/1.0");
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

        System.out.println("POST response:" + sb.toString());
        socket.close();
    }

    public void postTransaction(InetAddress ip, int port, Transaction transaction) throws IOException {
        Socket socket = new Socket(ip, port);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // send an HTTP request to the web server
        out.println("POST /transaction HTTP/1.0");
        out.println("Content-Length: " + StringUtil.getJson(transaction).getBytes().length);
        out.println("Content-Type: application/json");
        out.println(StringUtil.getJson(transaction)+"END");
        out.println();

        // read the response
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
