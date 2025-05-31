// ChatClient.java
import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1234);
        System.out.println("Connected to the server");

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread sendThread = new Thread(() -> {
            try {
                String message;
                while ((message = keyboard.readLine()) != null) {
                    out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Error sending message");
            }
        });

        Thread receiveThread = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Server: " + message);
                }
            } catch (IOException e) {
                System.out.println("Error receiving message");
            }
        });

        sendThread.start();
        receiveThread.start();
    }
}
