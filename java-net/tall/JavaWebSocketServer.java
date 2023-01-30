package com.anma.skt.tall;

import com.anma.skt.models.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.util.ByteBufferUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Collections;

public class JavaWebSocketServer extends WebSocketServer {

    Gson gson = new GsonBuilder().create();

    public JavaWebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public JavaWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    public JavaWebSocketServer(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast("new connection: " + handshake
                .getResourceDescriptor()); //This method sends a message to all clients connected
        System.out.println(
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Message msg = new Message("Hi there!", LocalDateTime.now(), "John Doe");
        String msgJson = gson.toJson(msg);
        broadcast(msgJson);

        ByteBuffer byteBuffer = ByteBuffer.wrap("Hello".getBytes());
        ByteBufferUtils.transferByteBuffer(byteBuffer, ByteBuffer.allocate(2));

        System.out.println(conn + ": " + message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {

        Message msg = new Message("Hi there!", LocalDateTime.now(), "John Doe");
        String msgJson = gson.toJson(msg);
        broadcast(msgJson);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(message.array())));
        StringBuilder builder = new StringBuilder();
        try {
            while (reader.readLine() != null) {
                    builder.append(reader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        System.out.println(gson.toJson(builder));
    }

    //    @Override
    public void onMessageAll(WebSocket conn, ByteBuffer message) {
        broadcast(message.array());
        System.out.println(conn + ": " + message);
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        int port = 7072; // 843 flash policy port

        JavaWebSocketServer server = new JavaWebSocketServer(port);
        server.start();
        System.out.println("ChatServer started on port: " + server.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            server.broadcast(in);
            if (in.equals("exit")) {
                server.stop(1000);
                break;
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}
