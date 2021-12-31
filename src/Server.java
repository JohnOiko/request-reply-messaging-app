import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

// Class that represents the messaging server.
public class Server {
    public static void main(String[] args) {
        ArrayList<Account> accounts = new ArrayList<>(); // The Arraylist which saves all the accounts.
        try {
            ServerSocket listenSocket = new ServerSocket(Integer.parseInt(args[0])); // Create a socket using the first argument as the port.
            while (true) {
                Socket clientSocket = listenSocket.accept(); // Listen for and accept new connections by saving them in a new socket.
                Connection connection = new Connection(clientSocket, accounts); // Create a new connection which handles the interaction with the client.
            }
        } catch (IOException e) {
            System.out.println("Listen socket: " + e.getMessage());
        }
    }
}

// Class that is used by the server to reply to the client's requests.
class Connection extends Thread {
    private DataInputStream in; // The server's input stream (used to transfer the client's requests).
    private DataOutputStream out; // The server's output stream (used to transfer the server's replies).
    private Socket clientSocket; // The server's socket.
    private ArrayList<Account> accounts; // The Arraylist which saves all the accounts (a pointer needs to be saved in this class).

    // Class constructor.
    public Connection (Socket aClientSocket, ArrayList<Account> accounts) {
        this.accounts = accounts;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start(); // Here is where the thread starts running.
        } catch(IOException e) {
            System.out.println("Connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method that is used to reply to the client's requests.
    public void run() {
        try {
            String[] request = in.readUTF().split("~", -1); // Split the client's request into an array using "~" as delimiter.
            int FN_ID = Integer.parseInt(request[0]); // The FN_ID is always the first part of the request.
            switch (FN_ID) {
                case 1: {
                    String username = request[1]; // The second part of the request when FN_ID=1 is the new account's username.
                    if (getUser(username) == null && isValid(username)) { // If the username doesn't exist already and is valid create a new account.
                        Account newAccount = new Account(username, createToken(4));
                        accounts.add(newAccount);
                        out.writeUTF(Integer.toString(newAccount.getAuthToken()));
                    } else if (getUser(username) != null) { // Else if the username already exists reply with an error message.
                        out.writeUTF("Sorry, the user already exists");
                    } else if (!isValid(username)) { // Else if the username isn't valid reply with an error message.
                        out.writeUTF("Invalid Username");
                    }
                    break;
                }
                case 2: {
                    int authToken = Integer.parseInt(request[1]); // The second part of the request when FN_ID=2 is the account's authToken.
                    if (getUser(authToken) != null) { // If there is a user with the given authToken reply with the list of usernames.
                        out.writeUTF(getUsernames());
                    }
                    else { // Else if there is no user with the given authToken reply with an error message.
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
                case 3: {
                    int authToken = Integer.parseInt(request[1]); // The second part of the request when FN_ID=3 is the account's authToken.
                    Account sender = getUser(authToken); // Get the account the sender's authToken belongs to.
                    Account recipient = getUser(request[2]); // Get the account the recipient's username belongs to.
                    String message = request[3]; // The fourth part of the request when FN_ID=3 is the message.
                    if (sender != null && recipient != null) { // If both the sender's and the recipient's accounts exist add the message to the recipient's message box.
                        recipient.addMessage(sender.getUsername(), recipient.getUsername(), message);
                        out.writeUTF("OK");
                    }
                    else if (recipient == null) { // Else if the recipient's username is not found reply with an error message.
                        out.writeUTF("User does not exist");
                    }
                    else { // Else if the sender's authToken is not found reply with an error message.
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
                case 4: {
                    int authToken = Integer.parseInt(request[1]); // The second part of the request when FN_ID=4 is the account's authToken.
                    Account user = getUser(authToken); // Get the account the authToken belongs to.
                    if (user != null) { // If there is a user with the given authToken reply with the list of the user's messages.
                        out.writeUTF(getMessages(user));
                    }
                    else { // Else if there is no user with the given authToken reply with an error message.
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
                case 5: {
                    int authToken = Integer.parseInt(request[1]); // The second part of the request when FN_ID=5 is the account's authToken.
                    int messageID = Integer.parseInt(request[2]); // The third part of the request when FN_ID=5 is the id of the message.
                    Account user = getUser(authToken); // Get the account the authToken belongs to.
                    if (user != null) { // If there is a user with the given authToken look for the message with the given id.
                        Message message = getMessage(user, messageID - 1);
                        if (message != null) { // If the message was found set it to read and reply with the message and its sender.
                            message.setRead(true);
                            out.writeUTF("(" + message.getSender() + ")" + message.getBody());
                        } else { // Else if the message was not found reply with an error message.
                            out.writeUTF("Message ID does not exist");
                        }
                    } else { // Else if there is no user with the given authToken reply with an error message.
                        out.writeUTF("Invalid Auth Token");
                    }
                    break;
                }
                case 6: {
                    int authToken = Integer.parseInt(request[1]); // The second part of the request when FN_ID=5 is the account's authToken.
                    int messageID = Integer.parseInt(request[2]); // The third part of the request when FN_ID=5 is the id of the message.
                    Account user = getUser(authToken); // Get the account the authToken belongs to.
                    if (user != null) { // If there is a user with the given authToken delete the message with the given id.
                        if (deleteMessage(user, messageID - 1)) { // If the message exists delete it and reply with a confirmation message.
                            out.writeUTF("OK");
                        } else { // Else if it doesn't exist reply with an error message.
                            out.writeUTF("Message ID does not exist");
                        }
                    } else { // Else if there is no user with the given authToken reply with an error message.
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
                clientSocket.close(); // Close the socket once the server has replied.
            } catch (IOException e) {
                System.out.println("Close: " + e.getMessage()); // Close failed.
            }
        }
    }

    // Method that returns an authentication token with digit_num digits that doesn't begin with 0.
    private int createToken(int digit_num) {
        Random generator = new Random();
        // Create the first random digit (between 1 and 9)
        String authToken = Integer.toString(generator.nextInt(9) + 1);
        for (int i = 0 ; i < (digit_num - 1) ; i++) {
            // Create the rest of the random digits (between 0 and 9) and concat them to the previous authToken
            authToken = authToken.concat(Integer.toString(generator.nextInt(10)));
        }
        // Return the authToken as an integer
        return Integer.parseInt(authToken);
    }

    // Method that searches for the username in the database and if it is found returns the account it belongs to, else returns null.
    private Account getUser(String username) {
        for (int i = 0 ; i < accounts.size() ; i++) {
            if (accounts.get(i).getUsername().equals(username)) {
                return accounts.get(i);
            }
        }
        return null;
    }

    // Method that searches for the authToken in the database and if it is found returns the account it belongs to, else returns null.
    private Account getUser(int authToken) {
        for (int i = 0 ; i < accounts.size() ; i++) {
            if (accounts.get(i).getAuthToken() == authToken) {
                return accounts.get(i);
            }
        }
        return null;
    }

    // Method that checks if the given username only contains latin letters, digits and underscores.
    private boolean isValid(String username) {
        for (int i = 0 ; i < username.length() ; i++) {
            if (!Character.isAlphabetic(username.charAt(i)) && !Character.isDigit(username.charAt(i)) && username.charAt(i) != '_') {
                return false;
            }
        }
        return true;
    }

    // Method that returns a string that contains the usernames of all the saved accounts.
    private String getUsernames() {
        String usernames = "";
        for (int i = 0 ; i < accounts.size() ; i++) {
            usernames = usernames.concat(i + 1 + ". " + accounts.get(i).getUsername());
            if (i != accounts.size() - 1) { // If this is not the last username append a newline character to the string.
                usernames = usernames.concat("\n");
            }
        }
        return usernames;
    }

    // Method that returns a string that contains all the messages saved in the given user's message box.
    private String getMessages(Account user) {
        String messages = "";
        if (user != null) {
            ArrayList<Message> messageBox = user.getMessageBox();
            for (int i = 0 ; i < messageBox.size() ; i++) {
                messages = messages.concat(i + 1 + ". from: " + (messageBox.get(i).getSender()) + (messageBox.get(i).isRead() ? "" : "*"));
                if (i != messageBox.size() - 1) { // If this is not the last message append a newline character to the string.
                    messages = messages.concat("\n");
                }
            }
        }
        return messages;
    }

    // Method that searches the given user's message box for a message with the given id and returns that message if it is found, else returns null.
    private Message getMessage(Account user, int messageID) {
        Message message = null;
        if (user != null) {
            if (messageID >= 0 && messageID < user.getMessageBox().size()) {
                message = user.getMessageBox().get(messageID);
            }
        }
        return message;
    }

    // Method that searches the given user's message box for a message with the given id and deletes that message if it is found and returns true, else returns null.
    private boolean deleteMessage(Account user, int messageID) {
        if (user != null) {
            return user.deleteMessage(messageID);
        }
        return false;
    }
}
