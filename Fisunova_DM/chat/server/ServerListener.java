package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerListener {
    private final Set<ClientHandler> clients;
    private ServerSocket serverSocket;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public ServerListener() {
        clients = new HashSet<>();
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен и готов к приему соединений...");

            while (true) {
                waitForNewClient();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForNewClient() {
        try {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clients.add(clientHandler);
            executorService.execute(clientHandler);
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
