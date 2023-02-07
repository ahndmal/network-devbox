package org.example.psq;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class AsynchronousSocketServer {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(executor);

        try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group)) {
            server.bind(new InetSocketAddress(HOST, PORT));
            System.out.println("[SERVER] Listening on " + HOST + ":" + PORT);

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                AsynchronousSocketChannel client = future.get();
                System.out.println("[SERVER] Accepted connection from " + client.getRemoteAddress());
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                client.read(buffer, buffer, new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();
                        if (result != -1) {
                            onMessageReceived(client, attachment);
                        }
                        attachment.clear();
                        client.read(attachment, attachment, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.err.println("[SERVER] Failed to read from client: " + exc);
                        exc.printStackTrace();
                    }
                });
            }
        }
    }

    private static void onMessageReceived(AsynchronousSocketChannel client, ByteBuffer buffer) {
        System.out.println("[SERVER] Received message from client: " + client);
        System.out.println("[SERVER] Buffer: " + buffer);
    }
}

class MainSimplest {
    public static void main(String[] args) throws Exception {
        AsynchronousSocketServer.main(args);
    }
}
