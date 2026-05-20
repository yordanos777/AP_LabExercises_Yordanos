import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

public class ClientFX extends Application {

    TextArea chatArea;
    TextField messageField;
    Button sendButton;

    DataOutputStream out;

    @Override
    public void start(Stage stage) {

        chatArea = new TextArea();

        chatArea.setEditable(false);

        messageField = new TextField();

        sendButton = new Button("Send");

        HBox bottom = new HBox(10, messageField, sendButton);

        VBox root = new VBox(10, chatArea, bottom);

        stage.setScene(new Scene(root, 400, 300));

        stage.setTitle("Chat Client");

        stage.show();

        sendButton.setOnAction(e -> sendMessage());

        
        messageField.setOnAction(e -> sendMessage());

        
        new Thread(this::connectToServer).start();
    }

    private void connectToServer() {

        try {

            Socket socket = new Socket("localhost", 4405);

            out = new DataOutputStream(socket.getOutputStream());

            DataInputStream in = new DataInputStream(socket.getInputStream());

            Platform.runLater(() ->
                    chatArea.appendText("Connected to Server!\n"));

            while (true) {

                String msg = in.readUTF();

                Platform.runLater(() ->
                        chatArea.appendText(msg + "\n"));
            }

        } catch (Exception e) {

            Platform.runLater(() ->
                    chatArea.appendText("Disconnected from server.\n"));
        }
    }

    private void sendMessage() {

        try {

            String time = LocalTime.now().withNano(0).toString();

            Message m = new Message("CLIENT", messageField.getText());

            String msg = "[" + time + "] " + m.format();

            out.writeUTF(msg);

            messageField.clear();

        } catch (Exception e) {

            chatArea.appendText("Failed to send message.\n");
        }
    }

    public static void main(String[] args) {

        launch(args);
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