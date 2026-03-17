package com.assettrackpro.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean readStatus = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Notification() {}

    public Notification(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public Long getId() { return id; }

    public String getMessage() { return message; }

    public boolean isReadStatus() { return readStatus; }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public User getUser() { return user; }
}