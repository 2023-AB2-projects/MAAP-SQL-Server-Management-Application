package backend;

import java.net.*;
import java.io.*;

public class ServerConnection extends Connection{
    private ServerSocket serverSocket;
    public ServerConnection(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    public void start() throws IOException {
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    public void fullStop() throws IOException {
        serverSocket.close();
    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//        ServerConnection serverConnection = new ServerConnection(4444);
//        serverConnection.start();
//        for (int i = 0; i < 10; i++) {
//            String msg = serverConnection.receive();
//            serverConnection.send(msg);
//        }
//        Thread.sleep(1000);
//        serverConnection.stop();
//    }
}

