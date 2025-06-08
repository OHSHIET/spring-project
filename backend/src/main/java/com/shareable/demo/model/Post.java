package com.shareable.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String title;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JsonIgnoreProperties("posts")
    private User user;

    private boolean edited = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(String content, User user) {
        this.content = content;
        this.user = user;
    }

    public Post(String content, User user, String title) {
        this.content = content;
        this.user = user;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.edited = false;
    }

    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getUser() { return user; }
    public String getTitle() { return title; }
    public Long getId() { return id; }
    public boolean isEdited() { return edited; }

    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUser(User user) { this.user = user; }
    public void setTitle(String title) { this.title = title; }
    public void setId(Long id) { this.id = id; }

    public void setEdited(boolean edited) {
        this.edited = edited;
        setCreatedAt(LocalDateTime.now());
    }

}