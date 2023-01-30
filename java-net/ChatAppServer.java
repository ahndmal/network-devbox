package com.anma.skt;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ChatAppServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(7072);
            Socket client1 = serverSocket.accept();
            System.out.println("[ -- ] Client connected");

            BufferedReader reader = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            PrintWriter writer = new PrintWriter(client1.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            Thread sender = new Thread(() -> {
                System.out.println("Creating new Thread");
               while (true) {
                   String msg = scanner.nextLine();
                   writer.println(msg);
                   writer.println(">> Server message sent");
                   writer.flush();
               }
            });
            sender.start();

            Thread receiver = new Thread(() -> {
                System.out.println("> Server receiver in Thread: " + Thread.currentThread().getName());
                String msg = null;
                try {
                    msg = reader.readLine();

                    while (msg != null) {
                        System.out.println("Message from Client:: " + msg);
                        msg = reader.readLine();
                    }

                    System.out.println("> Client disconnected");
                    writer.close();
                    client1.close();
                    serverSocket.close();

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
