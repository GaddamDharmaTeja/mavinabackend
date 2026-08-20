package com.maviinamane.auth;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document("admin_users")
public class AdminUser {

    @Id
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private String passwordHash;
    private String role = "ADMIN";

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        email = value;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String value) { fullName = value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { phone = value; }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String value) {
        passwordHash = value;
    }

    public String getRole() { return role; }
    public void setRole(String value) { role = value; }
}
