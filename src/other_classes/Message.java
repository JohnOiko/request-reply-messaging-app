package other_classes;

public class Message {
    private boolean isRead; //signifies whether the message has been read
    private String sender; //the sender of the message
    private String receiver; //the receiver of the message
    private String body; //the body of the message

    public Message(String sender, String receiver, String body) {
        isRead = false;
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getBody() {
        return body;
    }
}
