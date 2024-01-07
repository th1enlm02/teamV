package com.example.teamv.object;

public class User {
    private String user_id;
    private String fullname;
    private String email;
    private String password;
    public User(){};
    public User(String user_id, String fullname, String email, String password) {
        this.user_id = user_id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }
    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
