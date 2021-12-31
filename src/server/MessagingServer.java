package server;

import other_classes.Account;

import java.util.Random;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessagingServer {

    public static void main(String[] args) {
        List<Account> accounts = new ArrayList<>();

        try {
            int serverPort = Integer.parseInt(args[0]);
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Request from client " + clientSocket.getInetAddress()+" at port "+ clientSocket.getPort());
                Connection c = new Connection(clientSocket, accounts);
            }
        } catch (IOException e) {
            System.out.println("Listen socket: "+e.getMessage());
            e.printStackTrace();
        }
    }
}

class Connection extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;
    private List<Account> accounts;

    public Connection (Socket aClientSocket, List<Account> accounts) {
        this.accounts = accounts;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch(IOException e) {
            System.out.println("Connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // split the client's request into an array
            String[] request = in.readUTF().split("~", -1);
            int FN_ID = Integer.parseInt(request[0]);
            switch (FN_ID) {
                case 1:
                    String username = request[1];
                    if (isUnique(username) && isValid(username)) {
                        Account newAccount = new Account(username, createToken());
                        accounts.add(newAccount);
                        out.writeUTF(Integer.toString(newAccount.getAuthToken()));
                    }
                    else if (!isUnique(username)) {
                        out.writeUTF("Sorry, the user already exists");
                    }
                    else if (!isValid(username)) {
                        out.writeUTF("Invalid Username");
                    }
                    break;
                case 2:
                    int authToken = Integer.parseInt(request[1]);
                    out.writeUTF(getUsernames());
                    break;
                case 3:
                    // need to fill in code here
                    break;
                case 4:
                    // need to fill in code here
                    break;
                case 5:
                    // need to fill in code here
                    break;
                case 6:
                    // need to fill in code here
                    break;
                    // clientSocket.close();
                    // System.exit(0);
            }
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

    private int createToken() {
        Random generator = new Random();
        // create the first random digit (between 1 and 9)
        String authToken = Integer.toString(generator.nextInt(9) + 1);
        for (int i = 0 ; i < 3 ; i++) {
            // create the rest of the random digits (between 0 and 9) and concat them to the previous authToken
            authToken = authToken.concat(Integer.toString(generator.nextInt(10)));
        }
        // return the authToken as an integer
        return Integer.parseInt(authToken);
    }

    private boolean isUnique(String username) {
        // check if the username already exists in the database
        for (int i = 0 ; i < accounts.size() ; i++) {
            if (accounts.get(i).getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValid(String username) {
        // check if the username only contains latin letters, digits and underscores
        for (int i = 0 ; i < username.length() ; i++) {
            if (!Character.isAlphabetic(username.charAt(i)) && !Character.isDigit(username.charAt(i)) && username.charAt(i) != '_') {
                return false;
            }
        }
        return true;
    }

    private String getUsernames() {
        String usernames = "";
        for (int i = 0 ; i < accounts.size() ; i++) {
            usernames = usernames.concat(Integer.toString(i + 1) + ". " + accounts.get(i).getUsername());
            if (i != accounts.size() - 1) {
                usernames = usernames.concat("\n");
            }
        }
        return usernames;
    }
}
