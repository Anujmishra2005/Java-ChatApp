// ChatClientGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class ChatClientGUI {

    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClientGUI(String serverAddress, int port) {
        createUI();
        connectToServer(serverAddress, port);
    }

    private void createUI() {
        frame = new JFrame("Java Chat-Client");
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");
        

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void connectToServer(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a thread to read messages
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        messageArea.append(message + "\n");
                    }
                } catch (IOException e) {
                    messageArea.append("Disconnected from server.\n");
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to connect to server", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI("localhost", 1234));
    }
}
