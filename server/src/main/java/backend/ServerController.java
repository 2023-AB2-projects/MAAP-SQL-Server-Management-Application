package backend;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ServerController {
    public void start(int port) throws IOException {

        ServerConnection serverConnection = new ServerConnection(port);

        serverConnection.start();
        log.info("Client Connected");
        String shutdownMsg = "SHUTDOWN";

        

        while(true){
            try{
                String msg;
                msg = serverConnection.receive();

                if(msg.equals(shutdownMsg)){
                    serverConnection.send("SERVER DISCONNECTED");
                    serverConnection.stop();
                    log.info("Server Shutting Down");
                    break;
                }

                //pass message to parser and receive the answer

                serverConnection.send(msg);
            }catch (NullPointerException e){
                serverConnection.stop();
                log.info("Client Disconnected");
                serverConnection.start();
                log.info("Client Connected");
            }
        }

        serverConnection.fullStop();
    }

//    public static void main(String[] args) throws IOException {
//        ServerController serverController = new ServerController();
//        serverController.start(4444);
//    }
}
