package server;

import java.net.*;
import java.io.*;

public class MessagingServer {
    public static void main(String[] args) {
        try {
            int serverPort = Integer.getInteger(args[0]);
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Request from client" + clientSocket.getInetAddress()+"at port "+ clientSocket.getPort());
                Connection c = new Connection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:"+e.getMessage());
            e.printStackTrace();
        }
    }
}

class Connection extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;

    public Connection (Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch(IOException e) {
            System.out.println("Connection:"+e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        /* an echo server */
        try {
            // read a line of data from the stream
            String data = in.readUTF();
            out.writeUTF(data);
        } catch (EOFException e) {
            System.out.println("EOF:"+e.getMessage());
        } catch(IOException e) {
            System.out.println("readline:"+e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                /* close failed */
            }
        }
    }
}
