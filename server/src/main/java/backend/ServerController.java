package backend;

import java.io.IOException;

public class ServerController {
    public void start(int port) throws IOException {
        ServerConnection serverConnection = new ServerConnection(port);

        serverConnection.start();
        String shutdownMsg = "SHUTDOWN";

        while(true){
            try{
                String msg;
                msg = serverConnection.receive();

                if(msg.equals(shutdownMsg)){
                    serverConnection.send("SERVER DISCONNECTED");
                    serverConnection.stop();
                    break;
                }

                //pass message to parser and receive the answer

                serverConnection.send(msg);
            }catch (NullPointerException e){
                serverConnection.stop();
                serverConnection.start();
            }
        }

        serverConnection.fullStop();
    }

//    public static void main(String[] args) throws IOException {
//        ServerController serverController = new ServerController();
//        serverController.start(4444);
//    }
}
