package com.andmal.psq;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Predicate;

import static com.andmal.psq.PGServerMessage.handleQueryMessage;
import static com.andmal.psq.PGServerMessage.handleStartupMessage;

sealed interface PGClientMessage
        permits PGClientMessage.SSLNegotation, PGClientMessage.StartupMessage, PGClientMessage.QueryMessage {

    record SSLNegotation() implements PGClientMessage {
    }

    record StartupMessage(Map<String, String> parameters) implements PGClientMessage {
    }

    record QueryMessage(String query) implements PGClientMessage {
    }

    Predicate<ByteBuffer> isSSLRequest = (ByteBuffer b) -> {
        return b.get(4) == 0x04 && b.get(5) == (byte) 0xd2 && b.get(6) == 0x16 && b.get(7) == 0x2f;
    };

    Predicate<ByteBuffer> isStartupMessage = (ByteBuffer b) -> {
        return b.remaining() > 8 && b.get(4) == 0x00 && b.get(5) == 0x03 // Protocol version 3
                && b.get(6) == 0x00 && b.get(7) == 0x00;
    };

    static PGClientMessage decode(ByteBuffer buffer) {
        if (isSSLRequest.test(buffer)) {
            return new SSLNegotation();
        } else if (isStartupMessage.test(buffer)) {
            var segment = MemorySegment.ofBuffer(buffer);
            var length = buffer.getInt(0);
            var parameters = new HashMap<String, String>();
            var offset = 8;
            while (offset < length - 1) {
                var name = segment.getUtf8String(offset);
                offset += name.length() + 1;
                var value = segment.getUtf8String(offset);
                offset += value.length() + 1;
                parameters.put(name, value);
            }
            return new StartupMessage(parameters);
        } else {
            // Assume it's a query message
            var query = MemorySegment.ofBuffer(buffer).getUtf8String(5);
            return new QueryMessage(query);
        }
    }


    // In "AsynchronousSocketServer"
    private static void onMessageReceived(AsynchronousSocketChannel client, ByteBuffer buffer) {
        System.out.println("[SERVER] Received message from client: " + client);
        System.out.println("[SERVER] Buffer: " + buffer);

        PGClientMessage message = PGClientMessage.decode(buffer);
        switch (message) {
            case PGClientMessage.SSLNegotation ssl -> handleSSLRequest(ssl, client);
            case PGClientMessage.StartupMessage startup -> handleStartupMessage(startup, client);
            case PGClientMessage.QueryMessage query -> handleQueryMessage(query, client);
        }
    }

    // Where each of those methods contains the previous logic it held in the "if/else" statement, for example:
    private static void handleSSLRequest(PGClientMessage.SSLNegotation sslRequest, AsynchronousSocketChannel client) {
        System.out.println("[SERVER] SSL Request: " + sslRequest);
        ByteBuffer sslResponse = ByteBuffer.allocate(1);
        sslResponse.put((byte) 'N');
        sslResponse.flip();
        try {
            client.write(sslResponse).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}