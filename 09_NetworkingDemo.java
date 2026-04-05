// WAP in Java Using Basic Networking Features
//
// Demonstrates:
//   • InetAddress   : DNS lookup, IP info
//   • URL / URLConnection : fetching web content (HTTP GET)
//   • TCP Server (ServerSocket) + TCP Client (Socket) in separate threads
//   • UDP DatagramSocket sender + receiver
//   • Simple multi-client echo server concept

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class NetworkingDemo {

    // ══════════════════════════════════════════════════════════════════════════
    // 1. InetAddress — DNS lookup & host info
    // ══════════════════════════════════════════════════════════════════════════
    static void demoInetAddress() {
        System.out.println("── 1. InetAddress (DNS Lookup) ────────────");

        String[] hosts = {"www.google.com", "localhost"};

        for (String host : hosts) {
            try {
                InetAddress addr = InetAddress.getByName(host);
                System.out.println("  Host       : " + addr.getHostName());
                System.out.println("  IP Address : " + addr.getHostAddress());
                System.out.println("  Canonical  : " + addr.getCanonicalHostName());
                System.out.println("  Reachable  : " + addr.isReachable(2000));

                // All addresses for a host
                InetAddress[] all = InetAddress.getAllByName(host);
                System.out.println("  All IPs    : " + Arrays.toString(all));
                System.out.println();

            } catch (UnknownHostException e) {
                System.out.println("  [!] Unknown host: " + host);
            } catch (IOException e) {
                System.out.println("  [!] Error: " + e.getMessage());
            }
        }

        // Local machine info
        try {
            InetAddress local = InetAddress.getLocalHost();
            System.out.println("  Local Host : " + local.getHostName());
            System.out.println("  Local IP   : " + local.getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 2. URL & URLConnection — HTTP GET
    // ══════════════════════════════════════════════════════════════════════════
    static void demoURL() {
        System.out.println("\n── 2. URL & URLConnection (HTTP GET) ───────");

        try {
            URL url = new URL("https://httpbin.org/get");

            // URL components
            System.out.println("  Protocol : " + url.getProtocol());
            System.out.println("  Host     : " + url.getHost());
            System.out.println("  Port     : " + url.getPort());   // -1 = default
            System.out.println("  Path     : " + url.getPath());
            System.out.println("  Full URL : " + url.toString());

            // Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "JavaNetworkingDemo/1.0");

            int status = conn.getResponseCode();
            System.out.println("  HTTP Status  : " + status + " " + conn.getResponseMessage());
            System.out.println("  Content-Type : " + conn.getContentType());
            System.out.println("  Content-Len  : " + conn.getContentLength());

            // Read first 300 characters of response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
                String body = sb.toString();
                System.out.println("  Response (first 300 chars):");
                System.out.println(body.substring(0, Math.min(300, body.length()))
                        .lines().map(l -> "    " + l).reduce("", (a, b) -> a + b + "\n"));
            }

            conn.disconnect();

        } catch (IOException e) {
            System.out.println("  [!] HTTP Error: " + e.getMessage());
            System.out.println("  (Network may be restricted in this environment)");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 3. TCP Server (ServerSocket) — runs on a background thread
    // ══════════════════════════════════════════════════════════════════════════
    static class EchoServer implements Runnable {
        private final int       port;
        private final CountDownLatch ready;   // signals when server is listening

        EchoServer(int port, CountDownLatch ready) {
            this.port  = port;
            this.ready = ready;
        }

        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(port)) {
                server.setSoTimeout(6000);
                System.out.println("  [Server] Listening on port " + port);
                ready.countDown();             // signal that we're ready

                Socket client = server.accept();
                System.out.println("  [Server] Client connected: " + client.getInetAddress());

                try (BufferedReader  in  = new BufferedReader(
                             new InputStreamReader(client.getInputStream()));
                     PrintWriter     out = new PrintWriter(client.getOutputStream(), true)) {

                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("  [Server] Received : " + msg);
                        out.println("ECHO: " + msg.toUpperCase());
                        if (msg.equalsIgnoreCase("bye")) break;
                    }
                }
                System.out.println("  [Server] Client disconnected");

            } catch (SocketTimeoutException e) {
                System.out.println("  [Server] Timed out waiting for client");
            } catch (IOException e) {
                System.out.println("  [Server] Error: " + e.getMessage());
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 3b. TCP Client (Socket)
    // ══════════════════════════════════════════════════════════════════════════
    static class EchoClient implements Runnable {
        private final int port;

        EchoClient(int port) { this.port = port; }

        @Override
        public void run() {
            // Give server a moment to set up
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}

            try (Socket socket = new Socket("localhost", port);
                 PrintWriter    out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in  = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()))) {

                System.out.println("  [Client] Connected to localhost:" + port);

                String[] messages = {"hello", "java networking", "bye"};
                for (String m : messages) {
                    System.out.println("  [Client] Sending  : " + m);
                    out.println(m);
                    String response = in.readLine();
                    System.out.println("  [Client] Received : " + response);
                    Thread.sleep(200);
                }

            } catch (IOException | InterruptedException e) {
                System.out.println("  [Client] Error: " + e.getMessage());
            }
        }
    }

    static void demoTCP() throws InterruptedException {
        System.out.println("\n── 3. TCP Socket (ServerSocket / Socket) ──");

        int port = 9_001;
        CountDownLatch ready = new CountDownLatch(1);

        Thread serverThread = new Thread(new EchoServer(port, ready), "TCP-Server");
        Thread clientThread = new Thread(new EchoClient(port),        "TCP-Client");

        serverThread.setDaemon(true);
        serverThread.start();

        ready.await(3, TimeUnit.SECONDS);  // wait until server is ready
        clientThread.start();
        clientThread.join(6000);
        serverThread.join(6000);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 4. UDP DatagramSocket
    // ══════════════════════════════════════════════════════════════════════════
    static void demoUDP() throws IOException, InterruptedException {
        System.out.println("\n── 4. UDP DatagramSocket ──────────────────");

        int UDP_PORT = 9_002;

        // ── UDP Receiver (server) ──
        Thread receiver = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
                socket.setSoTimeout(4000);
                System.out.println("  [UDP-Receiver] Listening on port " + UDP_PORT);

                for (int i = 0; i < 3; i++) {
                    byte[]         buf    = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("  [UDP-Receiver] Got: '" + msg
                            + "' from " + packet.getAddress().getHostAddress()
                            + ":" + packet.getPort());
                }
            } catch (IOException e) {
                System.out.println("  [UDP-Receiver] " + e.getMessage());
            }
        }, "UDP-Receiver");

        receiver.setDaemon(true);
        receiver.start();
        Thread.sleep(300);  // give receiver time to bind

        // ── UDP Sender (client) ──
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress addr     = InetAddress.getByName("localhost");
            String[]    messages = {"Ping!", "Hello UDP", "Bye!"};

            for (String msg : messages) {
                byte[]         data   = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, addr, UDP_PORT);
                socket.send(packet);
                System.out.println("  [UDP-Sender]   Sent: '" + msg + "'");
                Thread.sleep(200);
            }
        }

        receiver.join(5000);
    }

    // ── Main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Java Networking Features Demo       ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        demoInetAddress();
        demoURL();
        demoTCP();
        demoUDP();

        System.out.println("\n[Done]");
    }
}