import java.net.*;
import java.io.*;
import java.util.ArrayList;

// Class that represents a messaging client.
public class Client {
    public static void main(String[] args) {
        Socket socket = null; // Arguments supply hostname and port.
        try {
            socket = new Socket(args[0], Integer.parseInt(args[1])); // Create the socket with the first argument as hostname and the second as port.
            DataInputStream in = new DataInputStream(socket.getInputStream()); // Connect an input stream to the socket's input stream.
            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Connect an output stream to the socket's output stream.

            int FN_ID = Integer.parseInt(args[2]); // The FN_ID is given as the third argument.
            ArrayList<String> request = new ArrayList<>();
            request.add(Integer.toString(FN_ID)); // The FN_ID is needed in every request.

            if (FN_ID >= 1 && FN_ID <= 6) {
                switch (FN_ID) {
                    case 1:
                    case 2:
                    case 4: {
                        request.add(args[3]); // The fourth argument is the username for FN_ID=1 and the authToken for FN_ID=2 and FN_ID=4.
                        break;
                    }
                    case 3: {
                        request.add(args[3]); // The fourth argument is the authToken for FN_ID=3.
                        request.add(args[4]); // The fifth argument is the recipient for FN_ID=3.
                        request.add(args[5]); // The sixth argument is the message for FN_ID=3.
                        break;
                    }
                    case 5:
                    case 6: {
                        request.add(args[3]); // The fourth argument is the authToken for FN_ID=5 and FN_ID=6.
                        request.add(args[4]); // The fifth argument is the messageID for FN_ID=5 and FN_ID=6.
                        break;
                    }
                }

                out.writeUTF(String.join("~", request)); // Send the request as a string using "~" as delimiter to the server.
                String reply = in.readUTF(); // Read the server's reply.
                System.out.println(reply); // Print the server's reply.
            }
            else {
                System.out.println("Please give a function ID between 1 and 6");
            }

        } catch (UnknownHostException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Readline: " + e.getMessage());
        } finally {
            if (socket != null) try {
                socket.close(); // If connection was established, close the socket once the server's reply has been printed.
            } catch (IOException e) {
                System.out.println("Close: " + e.getMessage()); // Close failed.
            }
        }
    }
}
