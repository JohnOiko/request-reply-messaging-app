package client;

import java.net.*;
import java.io.*;

public class MessagingClient {
    public static void main(String[] args) {
        Socket socket = null; //arguments supply message and hostname
        try {
            int serverPort = Integer.getInteger(args[1]);
            socket = new Socket(args[0], serverPort);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(args[0]); //UTF is a string encoding see Sn. 4.4
            String data = in.readUTF(); //read a line of data from the stream
            System.out.println("Received: "+ data) ;
        } catch (UnknownHostException e) {
            System.out.println("Socket:"+e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:"+e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:"+e.getMessage());
        } finally {
            if(socket != null) try {
                socket.close();
            } catch (IOException e) {
                System.out.println("close:"+e.getMessage());
            }
        }
    }
}
