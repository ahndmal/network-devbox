package org.example;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public final class MultiCastSender implements Runnable {
    private static volatile long messageCounter = 0L;
    private static long lastMessageCounter = 0L;
    private static long lastTimestamp = System.currentTimeMillis();

    public static void main(final String[] args) throws Exception {

        printNetworkInterfaces();

        System.setProperty("java.net.preferIPv4Stack", "true");

        final byte[] buffer = "This is a string with sufficient data to test a packet sending"
                .getBytes(StandardCharsets.US_ASCII);

        final int multiCastPort = 8888;
//        final String address = "224.0.0.10";
        final String address = "192.168.88.226";
        /*
        name:veth133bf61 (veth133bf61)
        name:docker0 (docker0)
        name:wlp61s0 (wlp61s0)
        name:lo (lo)
         */
        final NetworkInterface networkInterface = NetworkInterface.getByName("wlp61s0");
        final int count = 5;

        final InetAddress interfaceAddress = networkInterface.getInterfaceAddresses().get(0).getAddress();
        final MulticastSocket sendSocket = new MulticastSocket(new InetSocketAddress(interfaceAddress, multiCastPort));
        final InetAddress group = InetAddress.getByName(address);
//        final InetAddress inetAddress = InetAddress.getByName("localhost");

        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, multiCastPort);

        final Thread t = new Thread(new MultiCastSender());
        t.start();

        AtomicInteger counter = new AtomicInteger(2);

        while (counter.incrementAndGet() < count) {
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
                Thread.sleep(5000L);
            } catch (final InterruptedException ex) {
                ex.printStackTrace();
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

    private static void printNetworkInterfaces() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            System.out.println(netint);
            displaySubInterfaces(netint);
        }
    }

    static void displaySubInterfaces(NetworkInterface netIf) throws SocketException {
        Enumeration<NetworkInterface> subIfs = netIf.getSubInterfaces();

        for (NetworkInterface subIf : Collections.list(subIfs)) {
            System.out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
            System.out.printf("\tSub Interface Name: %s\n", subIf.getName());
        }
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        System.out.printf("Display name: %s\n", netint.getDisplayName());
        System.out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            System.out.printf("InetAddress: %s\n", inetAddress);
        }
        System.out.printf("\n");
    }
}
