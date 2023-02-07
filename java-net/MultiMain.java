package org.example;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MultiMain {
    public static void main(String[] args) throws IOException {
        final byte[] buffer = "HELLO!".getBytes(StandardCharsets.US_ASCII);
        final int multiCastPort = 6566;

        final String address = "192.168.88.226";
        final NetworkInterface networkInterface = NetworkInterface.getByName("wlp61s0");

        final InetAddress interfaceAddress = networkInterface.getInterfaceAddresses().get(0).getAddress();
        final InetAddress inetAddress = InetAddress.getByName(address);

        while (true) {
            try (MulticastSocket sendSocket = new MulticastSocket(new InetSocketAddress(inetAddress, multiCastPort))) {
                final InetAddress group = InetAddress.getByName(address);
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, multiCastPort);

                int counter = 0;
                for (int i = 0; i < 5; i++) {
                    sendSocket.send(packet);
                    counter++;
                }

                System.out.format("Sent %d messages", counter);

            }
        }
    }
}
