package com.lidigu.studentManagement.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_found_comment")
public class LostFoundComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private LostFoundItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "author_role")
    private String authorRole;

    public LostFoundComment() {
    }

    public LostFoundComment(String comment, LocalDateTime createdAt, LostFoundItem item, User author) {
        this.comment = comment;
        this.createdAt = createdAt;
        this.item = item;
        this.author = author;
        if (author != null && author.getRole() != null) {
            this.authorRole = author.getRole().getName();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LostFoundItem getItem() {
        return item;
    }

    public void setItem(LostFoundItem item) {
        this.item = item;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }
}
