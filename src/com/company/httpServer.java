package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class httpServer {

    public void runServer(int port) {
        ServerSocket server_socket;
        try {
            server_socket = new ServerSocket(port);
            System.out.println("httpServer running on port "
                    + server_socket.getLocalPort());

            while (true) {
                Socket socket = server_socket.accept();
                System.out.println("New connection accepted "
                        + socket.getInetAddress() + ":" + socket.getPort());

                try {
                    httpRequestHandler request = new httpRequestHandler(socket);
                    Thread thread = new Thread(request);
                    thread.start();

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

class httpRequestHandler implements Runnable {
    private AddressUtil util = new AddressUtil();
    private final static String CRLF = "\r\n";

    private Socket socket;

    private InputStream input;

    private OutputStream output;

    private BufferedReader br;

    public httpRequestHandler(Socket socket) throws Exception {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
        this.br = new BufferedReader(new InputStreamReader(socket
                .getInputStream()));
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        while (true) {

            String headerLine = br.readLine();
            System.out.println(headerLine);
            if (headerLine.equals(CRLF) || headerLine.equals("")){
                break;}

            StringTokenizer s = new StringTokenizer(headerLine);
            String type = s.nextToken();
            String content = s.nextToken();
            System.out.println(content);

            if (type.equals("GET")) {

                String statusLine;
                String contentTypeLine;
                String entityBody;

                statusLine = "HTTP/1.0 200" + CRLF;
                contentTypeLine = "Content-Type: application/json";
                entityBody = util.getAddress()+"END";
                if (content.equals("/blocks")) {
                    if (!Main.lastBlock.get(0).transactions.isEmpty()){
                        Main.addBlock(Main.lastBlock.get(0));
                    }
                    entityBody = StringUtil.getJson(Main.blockchain)+"END";
                    System.out.println(entityBody);
                }

                output.write(statusLine.getBytes());

                output.write(contentTypeLine.getBytes());

                output.write(CRLF.getBytes());

                output.write(entityBody.getBytes());

                output.write(CRLF.getBytes());
            }

            if (type.equals("POST")) {

                String line = "";
                StringBuilder sb = new StringBuilder();


                do {
                    line = br.readLine();

                    sb.append(line);
                    System.out.println(line);
                } while (!line.contains("END"));

                if (content.equals("/address")) {
                    util.addAddress(sb.toString().substring(sb.indexOf("{"),sb.indexOf("END")));
                }
                if (content.equals("/transaction")) {
                    Main.lastBlock.get(0).addTransactionJSON(sb.toString().substring(sb.indexOf("{"),sb.indexOf("END")));
                }
                if (content.equals("/block")) {
                    Main.addBlockJSON(sb.toString().substring(sb.indexOf("{"),sb.indexOf("END")));
                }
                if (content.equals("/blocks")) {
                    Main.addBlocksJSON(sb.toString().substring(sb.indexOf("{"),sb.indexOf("END")));
                }


                String statusLine = "HTTP/1.0 200" + CRLF;

                output.write(statusLine.getBytes());

                output.write(CRLF.getBytes());

            }
        }

        try {
            output.close();
            br.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
