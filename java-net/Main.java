package org.example;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        head();
    }

    private static void get() throws IOException {
        try (var socket = new Socket("example.com", 80)) {
            try (var writer = new PrintWriter(socket.getOutputStream())) {
                writer.print("GET / HTTP/1.1\r\n");
                writer.print("Host: example.com\r\n");
                writer.print("\r\n");
                writer.flush();
                socket.shutdownOutput();

                String outStr;
                try (var bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    while ((outStr = bufRead.readLine()) != null) {
                        System.out.println(outStr);
                    }

                    socket.shutdownInput();
                }
            }
        }
    }

    public static void head() throws IOException {
        var host = "example.com";
        var port = 80;
        try (var socket = new Socket(host, port)) {

            try (var writer = new PrintWriter(socket.getOutputStream(), true)) {

                writer.println("""
                        HEAD / HTTP/1.1
                        Host: %s
                        User-Agent: Console Http Client
                        Accept: text/html
                        Accept-Language: en-US
                        Connection: close
                        """.formatted(host));

                try (var reader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            }
        }
    }
}