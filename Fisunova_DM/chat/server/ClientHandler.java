package chat.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String nickName;
    private ServerListener serverListener;
    private boolean isRunning;

    public ClientHandler(Socket clientSocket, ServerListener serverListener) throws IOException {
        this.clientSocket = clientSocket;
        this.serverListener = serverListener;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        isRunning = true;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            nickName = in.readLine();
            System.out.println("Клиент с никнеймом " + nickName + " подключился к серверу.");

            String message;
            while (isRunning) {
                try {
                    message = in.readLine();
                } catch (IOException e) {
                    if (e instanceof SocketException) {
                        System.out.println("Клиент с никнеймом " + nickName + " отключился от сервера.");
                        serverListener.removeClient(this);
                        isRunning = false;
                        break;
                    } else {
                        e.printStackTrace();
                        break;
                    }
                }

                if (message == null) {
                    System.out.println("Клиент с никнеймом " + nickName + " отключился от сервера.");
                    serverListener.removeClient(this);
                    isRunning = false;
                    break;
                }

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Клиент с никнеймом " + nickName + " отключился от сервера.");
                    serverListener.removeClient(this);
                    isRunning = false;
                    break;
                }

                System.out.println(nickName + ": " + message);
                serverListener.sendMessageToAllClients(nickName + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
