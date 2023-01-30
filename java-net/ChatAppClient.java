package com.anma.skt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatAppClient {
    public static void main(String[] args) {
        try(Socket client = new Socket(InetAddress.getLocalHost(),7072);) {
            System.out.println("[ ** ] Client created");

            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter writer = new PrintWriter(client.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            Thread sender = new Thread(() -> {
                while (true) {
                    System.out.println("> Client Thread name is" + Thread.currentThread().getName());
                    String msg = scanner.nextLine();
                    writer.println(msg);
                    writer.flush();
                }
            });
            sender.start();

            Thread receiver = new Thread(() -> {
                String msg = null;
                try {
                    msg = reader.readLine();

                    while (msg != null) {
                        System.out.println("Message from Server:: " + msg);
                        msg = reader.readLine();
                    }

                    System.out.println("> Server disconnected");
                    writer.close();
                    client.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiver.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
