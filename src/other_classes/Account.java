package other_classes;

import java.util.ArrayList;
import java.util.List;

public class Account {
    /* The user's name. Includes only alphanumeric characters and the special character "_" */
    private String username;
    /* The user's unique identification number (created by the server and is personal/hidden) */
    private int authToken;
    /* THe users inbox, which is a list of messages */
    private List<Message> messageBox;

    public Account(String username, int authToken) {
        this.username = username;
        this.authToken = authToken;
        messageBox = new ArrayList<>();
    }
}
