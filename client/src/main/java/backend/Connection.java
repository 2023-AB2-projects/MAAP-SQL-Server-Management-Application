package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
    protected Socket clientSocket;
    protected PrintWriter out;
    protected BufferedReader in;
    public void send(String msg){
        out.println(msg);
        out.println("END OF COMMAND");
        out.flush();
    }
    public String receive() throws IOException, NullPointerException {
        String msg;
        StringBuilder fullMessage = new StringBuilder();
        while(true){
            msg = in.readLine();
            if(msg.equals("END OF COMMAND")){
                break;
            }
            fullMessage.append(msg);
            fullMessage.append("\r\n");
        }
        fullMessage.delete(fullMessage.lastIndexOf("\r\n"), fullMessage.length());
        return fullMessage.toString();
    }
}

