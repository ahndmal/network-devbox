package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cleint2 {
    public static void main(String[] args) throws IOException {
        try (Socket client2 = new Socket("localhost", 10000)) {
            var client2InputStream = client2.getInputStream();
            var client2OutputStream = client2.getOutputStream();

            BufferedReader clientReader = new BufferedReader(new InputStreamReader(client2InputStream));
            PrintWriter clientWriter = new PrintWriter(client2OutputStream);

            String line = clientReader.readLine();
            System.out.println(line);

            clientWriter.write("Hello from client!");

        }
    }
}
