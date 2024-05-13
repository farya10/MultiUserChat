package chat.server;

import java.util.ArrayList;
import java.util.List;

public class ChatLog {
    private List<String> messages = new ArrayList<>();
    private ServerListener serverListener;

    public ChatLog(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public void put(String message, ClientHandler sender) {
        messages.add(message);
        serverListener.sendMessageToAllClients(message);
    }
}
