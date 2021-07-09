package com.company;

import java.io.Serializable;

/**
 * data class to transfer messages between client and server.
* */
public class Message implements Serializable {

    private String nickName;
    private String msg = null;
    private boolean joined;
    private boolean left;

    public Message(String msg, String nickName, boolean joined, boolean left) {
        this.msg = msg;
        this.nickName = nickName;
        this.joined = joined;
        this.left = left;
    }

    public Message(String nickName, boolean joined, boolean left) {
        this.nickName = nickName;
        this.joined = joined;
        this.left = left;
    }


    public boolean isMessage() {
        return msg != null;
    }

    public String getMessage() {
        return msg;
    }

    public String getNickName() {
        return nickName;
    }

    public boolean isJoined() {
        return joined;
    }

    public boolean isLeft() {
        return left;
    }

}