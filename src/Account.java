import java.util.ArrayList;
import java.util.List;

public class Account {
    private String username; //the user's name. Includes only alphanumeric characters and the special character "_"
    private int authToken; //the user's unique identification number (created by the server and is personal/hidden)
    private List<Message> messageBox; //the users inbox, which is a list of messages

    public Account(String username, int authToken) {
        this.username = username;
        this.authToken = authToken;
        messageBox = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public int getAuthToken() {
        return authToken;
    }

    public List<Message> getMessageBox() {
        return new ArrayList<>(messageBox);
    }

    public void addMessage(String sender, String receiver, String body) {
        messageBox.add(new Message(sender, receiver, body));
    }

    public boolean deleteMessage(int messageID) {
        if (messageID >= 0 && messageID < messageBox.size()) {
            messageBox.remove(messageID);
            return true;
        }
        return false;
    }
}
