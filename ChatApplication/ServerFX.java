import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.*;
import java.io.*;
import java.time.LocalTime;
import java.util.Vector;

public class ServerFX extends Application {

    TextArea chatArea;
    TextField messageField;
    Button sendButton;

    static Vector<ClientHandler> clients = new Vector<>();

    @Override
    public void start(Stage stage) {

        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        sendButton = new Button("Send");

        HBox bottom = new HBox(10, messageField, sendButton);
        VBox root = new VBox(10, chatArea, bottom);

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Server");
        stage.show();

        sendButton.setOnAction(e -> sendMessage());

        
        messageField.setOnAction(e -> sendMessage());

        
        new Thread(() -> startServer()).start();
    }

    @Override
    public void stop() {
        System.out.println("Closing server...");
        System.exit(0);
    }

    private void startServer() {

        try (ServerSocket server = new ServerSocket(4405)) {

            append("Server started on port 4405...\n");

            while (true) {

                Socket socket = server.accept();

                append("Client connected\n");

                ClientHandler client = new ClientHandler(socket, this);

                clients.add(client);

                client.start();
            }

        } catch (Exception e) {

            append("Server stopped or error occurred.\n");
        }
    }

    void append(String msg) {

        Platform.runLater(() -> chatArea.appendText(msg));

        
        try {
            FileWriter writer = new FileWriter("chat.txt", true);
            writer.write(msg);
            writer.close();

        } catch (Exception e) {

            System.out.println("File error");
        }
    }

    private void sendMessage() {

        String time = LocalTime.now().withNano(0).toString();

        Message m = new Message("SERVER", messageField.getText());

        String msg = "[" + time + "] " + m.format();

        for (ClientHandler c : clients) {

            try {

                c.out.writeUTF(msg);

            } catch (Exception ignored) {
            }
        }

        append(msg + "\n");

        messageField.clear();
    }

    public static void main(String[] args) {

        launch(args);
    }
}



class ClientHandler extends Thread {

    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    ServerFX server;

    public ClientHandler(Socket socket, ServerFX server) throws Exception {

        this.socket = socket;
        this.server = server;

        in = new DataInputStream(socket.getInputStream());

        out = new DataOutputStream(socket.getOutputStream());
    }

    public void run() {

        try {

            while (true) {

                String msg = in.readUTF();

                server.append(msg + "\n");

                
                for (ClientHandler c : ServerFX.clients) {

                    c.out.writeUTF(msg);
                }
            }

        } catch (Exception e) {

            server.append("Client disconnected\n");

        } finally {

            try {

                ServerFX.clients.remove(this);

                socket.close();

                in.close();

                out.close();

            } catch (Exception ex) {

                System.out.println("Cleanup error");
            }
        }
    }
}



class Message {

    private String sender;

    private String text;

    public Message(String sender, String text) {

        this.sender = sender;

        this.text = text;
    }

    public String format() {

        return sender + ": " + text;
    }
}