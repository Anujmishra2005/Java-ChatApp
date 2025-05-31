// ChatServer.java
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());
    private static Scanner scanner = new Scanner(System.in); // For server input

    public static void main(String[] args) throws IOException {
        System.out.println("Server is running on port 1234...");
        ServerSocket serverSocket = new ServerSocket(1234);

        // Accept client connections in a new thread
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected: " + socket.getInetAddress());
                    new ClientHandler(socket).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Server can send messages too
        while (true) {
            String serverMsg = scanner.nextLine();
            broadcast("Server: " + serverMsg);
        }
    }

    public static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientWriters.add(out);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Client: " + message);
                    broadcast("Client: " + message);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected.");
            } finally {
                try {
                    socket.close();
                    clientWriters.remove(out);
                } catch (IOException e) {}
            }
        }
    }
}
