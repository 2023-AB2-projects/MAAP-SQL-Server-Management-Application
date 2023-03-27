package backend;

import lombok.extern.slf4j.Slf4j;

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

}

