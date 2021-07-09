package com.company;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private final int PORT = 7777;
    private ServerSocket serverSocket;
    private final ArrayList<String> users = new ArrayList<>();
    private final ArrayList<ObjectOutputStream> streamsList = new ArrayList<>();

    public static void main(String[] args) {
        new Server();
    }

    /**
     * listening to incoming connections.
     * for each connection with client creating new thread
    * */
    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ServerThread runnable = new ServerThread(this, socket);
                Thread thread = new Thread(runnable);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * send messages to all the users that connected to the server.
     * looping through all the streams that the server holds and send the message one by one.
     * @param msg the message object to send
     * @param streamToDiscard stream to ignore while sending messages(own user stream)
    * */
    public synchronized void sendMessageToUsers(Message msg,ObjectOutputStream streamToDiscard) throws IOException {
        for (ObjectOutputStream stream: streamsList) {
            if (!stream.equals(streamToDiscard))
            {
                stream.writeObject(msg);
                stream.flush();
            }
        }
    }

    public synchronized ArrayList<String> getUsers() {
        return users;
    }

    /**
     * adds nickname to users list
     * @param nickName nick name of user
    */
    public synchronized void addToUsers(String nickName) {
        users.add(nickName);
    }
    /**
     *removes nickname from users list
     * @param nickName nick name of user
    * */
    public synchronized  void removeUser(String nickName)
    {
        users.remove(nickName);
    }

    /**
     * the server holds a list of stream in order to communicate with each client that is connected.
     * this function adds the stream to the list.
     * @param stream stream to add to lists of streams that the server holds.
    * */
    public synchronized void addOutputStream(ObjectOutputStream stream)
    {
        streamsList.add(stream);
    }
    /**
     * the server holds a list of stream in order to communicate with each client that is connected.
     * this function removes the stream from the list.
     * @param stream stream to remove from lists of streams that the server holds.
     * */
    public synchronized void removeOutputStream(ObjectOutputStream stream)
    {
        streamsList.remove(stream);
    }
}