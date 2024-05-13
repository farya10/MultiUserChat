package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

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

            Thread readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (isRunning) {
                            String message = in.readLine();

                            if (Objects.isNull(message)) {
                                break;
                            }

                            if (message.equalsIgnoreCase("exit")) {
                                isRunning = false;
                                System.out.println("Клиент с никнеймом " + nickName + " отключился от сервера.");
                                serverListener.removeClient(ClientHandler.this);
                                serverListener.waitForNewClient();
                                break;
                            }

                            System.out.println(nickName + ": " + message);
                            serverListener.sendMessageToAllClients(nickName + ": " + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            readThread.start();
            readThread.join();
        } catch (IOException | InterruptedException e) {
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
