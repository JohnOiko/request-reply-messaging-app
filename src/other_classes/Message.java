package other_classes;

public class Message {
    /* signifies whether the message has been read */
    private boolean isRead;
    /* the sender of the message */
    private String sender;
    /* the receiver of the message*/
    private String receiver;
    /* the body of the message */
    private String body;

    public Message(String sender, String receiver, String body) {
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }
}
