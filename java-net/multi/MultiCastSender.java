package org.example;

import java.net.*;
import java.nio.charset.StandardCharsets;

public final class MultiCastSender implements Runnable {
    private static volatile long messageCounter = 0L;
    private static long lastMessageCounter = 0L;
    private static long lastTimestamp = System.currentTimeMillis();

    public static void main(final String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");

        final byte[] buffer = "This is a string with sufficient data to test a packet sending".getBytes(StandardCharsets.US_ASCII);

        final int multiCastPort = 4447;
        final String address = "127.0.0.1";
        final NetworkInterface networkInterface = NetworkInterface.getByName(args[0]);
        final int count = Integer.parseInt(args[1]);

        final InetAddress interfaceAddress = networkInterface.getInterfaceAddresses().get(0).getAddress();
        final MulticastSocket sendSocket = new MulticastSocket(new InetSocketAddress(interfaceAddress, multiCastPort));
        final InetAddress group = InetAddress.getByName(address);
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, multiCastPort);

        final Thread t = new Thread(new MultiCastSender());
        t.start();

        while (messageCounter++ < count) {
            packet.setData(buffer);
            sendSocket.send(packet);
        }

        sendSocket.close();
        t.interrupt();
        t.join();
    }

    private static void init(final String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        if (2 != args.length) {
            System.out.println("Usage: java MultiCastSender <interface name> <# messages>");
            System.exit(1);
        }
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000L);
            } catch (final InterruptedException ex) {
                break;
            }

            final long newTimestamp = System.currentTimeMillis();
            final long duration = newTimestamp - lastTimestamp;
            final long newMessageCounter = messageCounter;
            final long numberOfMessages = newMessageCounter - lastMessageCounter;

            System.out.format("Sent %d messages in %dms%n", numberOfMessages, duration);

            lastTimestamp = newTimestamp;
            lastMessageCounter = newMessageCounter;
        }
    }
}
