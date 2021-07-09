package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ClientView extends JFrame {

//    randomize user nick name
    private int RANDOMINT = 1000;

    private final JLabel statusJlabel;
    private final JTextField serverJoinTextField;
    private final JTextField messageTextField;
    private final JTextField nickNameTextField;
    private final JButton joinBtn;
    private final JButton leaveBtn;
    private final JButton sendBtn;

    private JList<String> messagesList;
    private final JList<String> usersList;
    private final JScrollPane scrollPaneMessageList;
    private final JScrollPane scrollPaneUsersList;
    private final DefaultListModel<String> messagesListModel = new DefaultListModel<>();
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();

    public static void main(String [] args)
    {
        ClientLogic clientLogic = new ClientLogic();
        ClientView clientView = new ClientView(clientLogic);

        clientLogic.setView(clientView);
    }

    public ClientView(ClientLogic clientLogic) {
        super("Client");

        setLayout(null);

        messagesList = new JList<>(messagesListModel);
        usersList = new JList<>(userListModel);
        scrollPaneMessageList = new JScrollPane(messagesList);
        scrollPaneUsersList = new JScrollPane(usersList);
        statusJlabel = new JLabel("Disconnected");
        serverJoinTextField = new JTextField("127.0.0.1");
        messageTextField = new JTextField("");


//        makes nickname unique if using default
        Random rnd = new Random();
        nickNameTextField = new JTextField("Nick name" + rnd.nextInt(RANDOMINT));

        joinBtn = new JButton("Join");
        leaveBtn = new JButton("Leave");
        sendBtn = new JButton("send");

        messagesList = new JList<>();

        scrollPaneMessageList.setBounds(0, 0, 400, 300);
        scrollPaneUsersList.setBounds(410,0,170,300);

        messageTextField.setBounds(0,310,400,25);
        sendBtn.setBounds(480, 310, 70, 30);

        serverJoinTextField.setBounds(110,350,100,30);
        nickNameTextField.setBounds(0, 350, 100, 30);
        joinBtn.setBounds(220, 350, 100, 30);
        leaveBtn.setBounds(340, 350, 120, 30);
        statusJlabel.setBounds(465, 350, 120, 30);

        add(scrollPaneMessageList, BorderLayout.CENTER);
        add(scrollPaneUsersList,BorderLayout.CENTER);
        add(serverJoinTextField);
        add(joinBtn);
        add(leaveBtn);
        add(sendBtn);
        add(messageTextField);
        add(nickNameTextField);
        add(statusJlabel);

        setSize(600, 430);
        setVisible(true);

        joinBtn.addActionListener(clientLogic);
        leaveBtn.addActionListener(clientLogic);
        sendBtn.addActionListener(clientLogic);
    }

    public JButton getJoinBtn() {
        return joinBtn;
    }

    public JButton getLeaveBtn() {
        return leaveBtn;
    }

    public JButton getSendBtn() {
        return sendBtn;
    }

    public String getServerJoinTextFieldText() {
        return serverJoinTextField.getText();
    }

    public String getMessageTextFieldText() {
        return messageTextField.getText();
    }

    public String getNickNameTextFieldText() {
        return nickNameTextField.getText();
    }

    /**
     * adds a message to List of messages in chat.
     * the message is formatted with the nick name of the sender and the message.
     * @param msg the message object
    * */
    public void addMessage(Message msg)
    {
        messagesListModel.addElement(msg.getNickName()+" : "+msg.getMessage());
    }

    /**
     * adds own user message to List of messages in the chat window.
     * @param msg the message text
     * */
    public void addMessage(String msg)
    {
        messagesListModel.addElement("Me : "+ msg);
    }

    /**
     * changes status of client to connected.
    * */
    public void setConnected() {
        statusJlabel.setText("Connected");
    }

    /**
     * changes status of client to disconnected.
     * */
    public void setDisconnected() {
        statusJlabel.setText("Disconnected");
    }

    /**
     * display a message in the chat that user joined.
     * @param nickName the nick name of the user that joined.
     * */
    public void userJoined(String nickName)
    {
        userListModel.addElement(nickName);
        messagesListModel.addElement("---"+nickName+"--- Joined the chat");
    }

    /**
     * display a message in the chat that user has left.
     * @param nickName the nick name of the user that left.
     * */
    public void userLeft(String nickName)
    {
        userListModel.removeElement(nickName);
        messagesListModel.addElement("---"+nickName+"--- left the chat");
    }

    public void setUsersModel(ArrayList <String> arrayList)
    {
        userListModel.clear();
        for (String item : arrayList) {
            userListModel.addElement(item);
        }
    }
}