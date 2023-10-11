package com.danila.javafxauth.model;

import java.util.UUID;

public class User {

    private int id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String  uuid;

    public User(String email, String password, String uuid) {
        this.email = email;
        this.password = password;
        this.uuid = uuid;
    }
    public User(String name, String phone, String email, String password, String uuid) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
