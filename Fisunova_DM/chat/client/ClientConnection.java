package chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnection {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String nickName;
    private Thread readThread, writeThread;

    public ClientConnection() {
        try {
            System.out.print("Введите ваш никнейм: ");
            Scanner scanner = new Scanner(System.in);
            nickName = scanner.nextLine();

            socket = new Socket("localhost", 27015);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write(nickName);
            out.newLine();
            out.flush();

            readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            String message = in.readLine();
                            if (message == null) {
                                break;
                            }
                            System.out.println(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            writeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Scanner scanner = new Scanner(System.in);
                        while (!Thread.currentThread().isInterrupted()) {
                            String message = scanner.nextLine();
                            out.write(message);
                            out.newLine();
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            readThread.start();
            writeThread.start();

            readThread.join();
            writeThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                writeThread.interrupt(); // Сигнализируем потоку writeThread о необходимости завершения
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
