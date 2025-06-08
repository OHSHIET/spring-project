package com.shareable.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean edited = false;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public Comment() {

    }

    public Comment(String content, User user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
        this.createdAt = LocalDateTime.now();
        this.edited = false;
    }

    public Long getId() {
        return this.id;
    }
    public String getContent() {
        return this.content;
    }
    public User getUser() {
        return this.user;
    }
    public Post getPost() {
        return this.post;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public boolean isEdited() {return this.edited;}

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
        setCreatedAt(LocalDateTime.now());
    }
}
