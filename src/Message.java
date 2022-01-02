// Class that represents a message.
public class Message {
    private boolean isRead; // Signifies whether the message has been read.
    private String sender; // The sender of the message.
    private String receiver; // The receiver of the message.
    private String body; // The body of the message.

    // Class constructor.
    public Message(String sender, String receiver, String body) {
        isRead = false;
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }

    public boolean isRead() { return isRead; } // Getter for isRead.

    public String getSender() { return sender; } // Getter for sender of the message.

    public String getReceiver() { return receiver; } // Getter for receiver of the message.

    public String getBody() { return body; } // Getter for the body of the message.

    public void setRead(boolean read) { isRead = read; } // Setter for isRead
}
