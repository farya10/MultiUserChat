package chat.server;

import java.io.IOException;

public class ChatServer {
    public static void main(String[] args) {
        ServerListener serverListener = new ServerListener();
        try {
            serverListener.start(27015);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
