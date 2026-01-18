package rmi;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String hashedPassword;
    private String role;

    public User(String username, String hashedPassword, String role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getRole() {
        return role;
    }
} 