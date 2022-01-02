## Class explanation:

Apart from the following explanations, there are comments in the source code that also
explain what specific parts of the code do.

### Message (in [Message.java](src/Message.java)):

This class represents a message. It has the member variables the instructions
provide as well as a constructor, getters, and a setter.

### Account (in file [Account.java](src/Account.java)):

This class represents an account. It has the member variables the instructions
provide as well as a constructor, getters, a method to add a message to the account's
message box and a method to delete a message from the account's message box.

### Client (in file [Client.java](src/Client.java)):

This class is the client. It has one public static main method which is executed
to get the client up and running. In a try statement, first the connection to the
server is established and then a request with the client's appropriate arguments
is sent to the server. Then the server's reply is printed. If at any point an
exception is produced, its origin as well as its message are printed.

### Server (in file [Server.java](src/Server.java)):

This class is the server. It has one public static main method which is executed
to get the server up and running. In a try statement, inside a while true loop,
first the connection to the client is established and a Connection object is
created which handles replying to the client.

### Connection (in file [Server.java](src/Server.java)):

This class handles replying to the clients and extends the class Thread to be
able to reply to multiple clients at the same time using threads. Its member
variables are the server's DataInputStream in, the server's DataOutputStream
out, the client's socket clientSocket and a pointer to the server's Arraylist
of saved accounts.

It also has a constructor, in which the thread starts running after the needed
initializations are complete, a public method run which overrides the Thread's
method with the same name which reads the client's request and replies
accordingly. Lastly there are eight private methods which are used by the method
run to complete the clients' requests.

The method run first splits the client's request into the correct parts and then
based on the FN_ID of the request, completes the actions the specific function id
requires and replies to the client with the corresponding information. If at any
point an exception is created, the server prints its origin and message. The server
always tries to close the connection with the client once it has finished replying
(even if an exception was created). If closing the connection fails an exception
is created whose origin and message are printed.

The number 4 in the line of code in the file [Server.java](src/Server.java)
line 52 can be changed to an integer digit_num > 0 so that the authToken the server
creates has digit_num length (I have left it at 4 since that is what the project's
instructions use).

For more detailed information about what each private method does, you can check
the corresponding method's comments.

## Assumptions about the system:

The source code is using the jdk OracleJDK 17.0.1.