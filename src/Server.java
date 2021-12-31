import java.util.Random;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

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
                case 1: {
                    String username = request[1];
                    if (getUser(username) == null && isValid(username)) {
                        Account newAccount = new Account(username, createToken());
                        accounts.add(newAccount);
                        out.writeUTF(Integer.toString(newAccount.getAuthToken()));
                    } else if (getUser(username) != null) {
                        out.writeUTF("Sorry, the user already exists");
                    } else if (!isValid(username)) {
                        out.writeUTF("Invalid Username");
                    }
                    break;
                }
                case 2: {
                    int authToken = Integer.parseInt(request[1]);
                    if (getUser(authToken) == null) {
                        out.writeUTF("Invalid Auth Token");
                    }
                    else {
                        out.writeUTF(getUsernames());
                    }
                    break;
                }
                case 3: {
                    int authToken = Integer.parseInt(request[1]);
                    Account sender = getUser(authToken);
                    Account recipient = getUser(request[2]);
                    String message = request[3];
                    if (sender != null && recipient != null) {
                        recipient.addMessage(sender.getUsername(), recipient.getUsername(), message);
                        out.writeUTF("OK");
                    }
                    else if (recipient == null) {
                        out.writeUTF("User does not exist");
                    }
                    else {
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
                case 4: {
                    int authToken = Integer.parseInt(request[1]);
                    Account user = getUser(authToken);
                    if (user != null) {
                        out.writeUTF(getMessages(user));
                    }
                    else {
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
                case 5: {
                    int authToken = Integer.parseInt(request[1]);
                    int messageID = Integer.parseInt(request[2]);
                    Account user = getUser(authToken);
                    if (user != null) {
                        Message message = getMessage(user, messageID - 1);
                        if (message != null) {
                            message.setRead(true);
                            out.writeUTF("(" + message.getSender() + ")" + message.getBody());
                        } else {
                            out.writeUTF("Message ID does not exist");
                        }
                    } else {
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
                case 6: {
                    int authToken = Integer.parseInt(request[1]);
                    int messageID = Integer.parseInt(request[2]);
                    Account user = getUser(authToken);
                    if (user != null) {
                        if (deleteMessage(user, messageID - 1)) {
                            out.writeUTF("OK");
                        } else {
                            out.writeUTF("Message ID does not exist");
                        }
                    } else {
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
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

    private Account getUser(String username) {
        // search for the username in the database and if it is found return the account it belongs to, else return null
        for (int i = 0 ; i < accounts.size() ; i++) {
            if (accounts.get(i).getUsername().equals(username)) {
                return accounts.get(i);
            }
        }
        return null;
    }

    private Account getUser(int authToken) {
        // search for the username in the database and if it is found return the account it belongs to, else return null
        for (int i = 0 ; i < accounts.size() ; i++) {
            if (accounts.get(i).getAuthToken() == authToken) {
                return accounts.get(i);
            }
        }
        return null;
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

    private String getMessages(Account user) {
        String messages = "";
        if (user != null) {
            List<Message> messageBox = user.getMessageBox();
            for (int i = 0 ; i < messageBox.size() ; i++) {
                messages = messages.concat(i + 1 + ". from: " + (messageBox.get(i).getSender()) + (messageBox.get(i).isRead() ? "" : "*"));
                if (i != messageBox.size() - 1) {
                    messages = messages.concat("\n");
                }
            }
        }
        return messages;
    }

    private Message getMessage(Account user, int messageID) {
        Message message = null;
        if (user != null) {
            if (messageID >= 0 && messageID < user.getMessageBox().size()) {
                message = user.getMessageBox().get(messageID);
            }
        }
        return message;
    }

    private boolean deleteMessage(Account user, int messageID) {
        if (user != null) {
            return user.deleteMessage(messageID);
        }
        return false;
    }
}