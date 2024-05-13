package chat.server;

import java.io.IOException;

public class ChatServer {
    public static void main(String[] args) {
        ServerListener serverListener = new ServerListener();
        serverListener.start(27015);
    }
}
