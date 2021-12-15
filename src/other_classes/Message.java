package other_classes;

public class Message {
    /* Signifies whether the message has been read */
    private boolean isRead;
    /* The sender of the message */
    private String sender;
    /* The receiver of the message*/
    private String receiver;
    /* The body of the message */
    private String body;

    public Message(String sender, String receiver, String body) {
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }
}
