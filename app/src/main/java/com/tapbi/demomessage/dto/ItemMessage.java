package com.tapbi.demomessage.dto;

import java.io.Serializable;

public class ItemMessage implements Serializable {
    private int id;
    private String name;
    private String content;
    private String number;
    private String creator;
    private String folder;
    private String address;
    private String date;
    private int type_sent;
    private String read;

    public ItemMessage() {
    }

    public ItemMessage(int id, String name, String content, String number,
                       String creator, String folder, String address,
                       String date, int type_sent, String read) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.number = number;
        this.creator = creator;
        this.folder = folder;
        this.address = address;
        this.date = date;
        this.type_sent = type_sent;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public int getType_sent() {
        return type_sent;
    }

    public void setType_sent(int type_sent) {
        this.type_sent = type_sent;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
