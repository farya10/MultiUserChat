package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerListener {
    private final Set<ClientHandler> clients;
    private ServerSocket serverSocket;

    public ServerListener() {
        clients = new HashSet<>();
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Сервер запущен и готов к приему соединений...");

        while (true) {
            waitForNewClient();
        }
    }

    public void waitForNewClient() {
        try {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToAllClients(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
