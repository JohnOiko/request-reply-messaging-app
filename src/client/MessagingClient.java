package client;

import other_classes.Account;

import java.net.*;
import java.io.*;

public class MessagingClient {
    public static void main(String[] args) {
        Socket socket = null; //arguments supply message and hostname
        try {
            int serverPort = Integer.parseInt(args[1]);
            socket = new Socket(args[0], serverPort);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            int FN_ID = Integer.parseInt(args[2]);
            String request = "";
            switch (FN_ID) {
                case 1:
                    String username = args[3];
                    request = FN_ID + "~" + username;
            }
            out.writeUTF(request); //UTF is a string encoding see Sn. 4.4
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

    private int CreateAccount(String username, DataInputStream in, DataOutputStream out) {
        try {
            out.writeUTF(1 + username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // int authToken = (int) Math.floor(Math.random() * (9999 - 1000 + 1) + 1000);
        // Account account = new Account(username, authToken);
        //return authToken;
        return 1;
    }
}
