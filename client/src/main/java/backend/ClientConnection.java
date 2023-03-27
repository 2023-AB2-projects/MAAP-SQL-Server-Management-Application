package backend;

import java.net.*;
import java.io.*;

public class ClientConnection extends Connection{
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();

    }

//    public static void main(String[] args) throws IOException{
//        ClientConnection clientConnection = new ClientConnection();
//        clientConnection.startConnection("127.0.0.1", 4444);
//
//        String msg;
//        for (int i = 0; i < 10; i++) {
//            clientConnection.send(Integer.toString(i));
//            msg = clientConnection.receive();
//            System.out.println(msg);
//        }
//
//        clientConnection.send("SHUTDOWN");
//        msg = clientConnection.receive();
//        System.out.println(msg);
//        clientConnection.stopConnection();
//    }
}
