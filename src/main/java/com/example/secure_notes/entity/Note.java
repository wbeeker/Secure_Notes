package com.example.secure_notes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // encrypted content

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime timeCreated;
    private LocalDateTime timeUpdated;

    public Note() {
    }

    public Note(Long id, String title, String content, User user, LocalDateTime timeCreated, LocalDateTime timeUpdated) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.timeCreated = timeCreated;
        this.timeUpdated = timeUpdated;
    }

    public Long getId() {
    return id;
}

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public LocalDateTime getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(LocalDateTime timeUpdated) {
        this.timeUpdated = timeUpdated;
    }
    
}
