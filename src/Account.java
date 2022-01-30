import java.util.ArrayList;

// Class that represents and account.
public class Account {
    private String username; // The user's name. Includes only alphanumeric characters and the special character "_".
    private int authToken; // The user's unique identification number (created by the server and is personal/hidden).
    private ArrayList<Message> messageBox; // The users message box, which is a list of messages.

    // Class constructor.
    public Account(String username, int authToken) {
        this.username = username;
        this.authToken = authToken;
        messageBox = new ArrayList<>(); // The message box of the account is initialized to be empty.
    }

    public String getUsername() { return username; } // Getter for the account's username.

    public int getAuthToken() { return authToken; } // Getter for the account's authToken.

    // Getter for the account's message box (return a shallow copy of the message box).
    public ArrayList<Message> getMessageBox() { return new ArrayList<>(messageBox); }

    // Method that adds a message to the account's message box.
    public void addMessage(String sender, String receiver, String body, int id) { messageBox.add(new Message(sender, receiver, body, id)); }

    /* Method that deletes the message with the given messageID from the account's message box.
    Returns true if deletion was successful, else false. */
    public boolean deleteMessage(int messageID) {
        for (int i = 0 ; i < messageBox.size() ; i++) {
            if (messageBox.get(i).getId() == messageID) {
                messageBox.remove(i);
                return true;
            }
        }
        return false;
    }
}
