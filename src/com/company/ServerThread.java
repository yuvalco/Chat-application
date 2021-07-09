package com.company;

import java.io.*;
import java.net.Socket;

/**
* a connection thread with the client.
 * actively listening to messages from the client and sending them to all clients that are connected.
* */
public class ServerThread implements Runnable {


    private final Socket clientSocket;
    private final Server server;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ServerThread(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;

        try {
            input = new ObjectInputStream(clientSocket.getInputStream());
            output = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        adds stream to server list of active streams
        server.addOutputStream(output);
    }

    @Override
    public void run() {
        sendMsgUserJoinedTheServer();
        Message msg;
        while (!clientSocket.isClosed()) {
            try {
                msg = (Message) input.readObject();
                server.sendMessageToUsers(msg, output);

                if (msg.isLeft())
                    server.removeUser(msg.getNickName());

            } catch (IOException | ClassNotFoundException e) {
                server.removeOutputStream(output);
                return;
            }
        }
    }

    /**
     * sending the user the list of active users in the chat and
     * adding user to list of users in the chat and updating all
     * the clients about it.
    * */
    private void sendMsgUserJoinedTheServer() {
        try {
//        add the user to severs lists of users
            Message msg = (Message)input.readObject();
            server.addToUsers(msg.getNickName());
//        send the new user the list of users that are logged in
            output.writeObject(server.getUsers());
            output.flush();
//        update all users about the new user that joined.
            server.sendMessageToUsers(msg, output);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}