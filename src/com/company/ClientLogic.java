package com.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientLogic implements ActionListener {

    private final int PORT = 7777;
    private Socket socket;
    private ClientView view;

    private ObjectOutputStream output = null;
    private ObjectInputStream input = null;
    private volatile boolean isConnectionActive = false;
    private String nickName;

    private ArrayList<String> users = new ArrayList<>();
    private Thread listenMessages;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(view.getJoinBtn()))
            join(view.getServerJoinTextFieldText(),view.getNickNameTextFieldText());

        if (e.getSource().equals(view.getLeaveBtn()))
            leave();

        if(e.getSource().equals(view.getSendBtn()))
            sendMessage(view.getMessageTextFieldText());
    }

    /**
     * sends message to other clients via servers.
     * @param message the message to send
     * */
    private void sendMessage(String message) {
        if (socket.isConnected() && isConnectionActive && output != null) {
            try {
                Message msg = new Message(message,nickName,false,false);
                output.writeObject(msg);
                output.flush();
                view.addMessage(message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Joins the server
     * @param address the address for the server
     * */
    private void join(String address,String nickName) {
        if (!isConnectionActive )
            try {
                socket = new Socket(InetAddress.getByName(address),PORT);
                output = new ObjectOutputStream(socket.getOutputStream());

                isConnectionActive = true;
                view.setConnected();
                this.nickName = nickName;

//            send a message to the server that client is joined.
                sendJoinedToServer(nickName);

                listen();
            }
            catch (IOException e) {
                e.printStackTrace();
                isConnectionActive = false;
            }
    }

    private void sendJoinedToServer(String nickName) throws IOException {
        Message msg = new Message(nickName,true,false);
        output.writeObject(msg);
        output.flush();
    }

    /**
     * listens to incoming messages from the server
     * */
    private void listen() {
        listenMessages = new Thread(new Runnable() {
            @Override
            public void run() {
                Object msg;
                try {
                    input = new ObjectInputStream(socket.getInputStream());
                    while (!socket.isClosed() && isConnectionActive && (msg = input.readObject()) != null) {
                        {
                            if (msg instanceof ArrayList) {
                                users = (ArrayList<String>) msg;
                                view.setUsersModel(users);
                            }
                            if (msg instanceof Message) {
                                if (((Message) msg).isMessage())
                                    view.addMessage((Message)msg);

                                else if (((Message) msg).isJoined()) {
                                    String nickName = ((Message) msg).getNickName();
                                    users.add(nickName);
                                    view.userJoined(nickName);

                                } else if (((Message) msg).isLeft()) {
                                    String nickName = ((Message) msg).getNickName();
                                    users.remove(nickName);
                                    view.userLeft(nickName);
                                }
                            }
                        }
                    }
                } catch(IOException | ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }
            });
        listenMessages.start();
    }


    /**
     * closes the socket.
     * */
    private void leave() {
        if (!socket.isClosed())
        {
            isConnectionActive = false;
            view.setDisconnected();

            if (socket != null) {
                try {
                    sendLeaveMsgToServer();
                    listenMessages.interrupt();
                    output.close();
                    input.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendLeaveMsgToServer() throws IOException {
        if (output != null)
        {
            Message msg = new Message(nickName,false,true);
            output.writeObject(msg);
            output.flush();
            output.close();
        }
    }

    public void setView(ClientView view) {
        this.view = view;
    }
}