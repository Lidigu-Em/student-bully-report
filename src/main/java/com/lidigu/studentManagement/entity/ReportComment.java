package com.lidigu.studentManagement.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_comment")
public class ReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private BullyingReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    public ReportComment() {
    }

    public ReportComment(String comment, LocalDateTime createdAt, BullyingReport report, User author) {
        this.comment = comment;
        this.createdAt = createdAt;
        this.report = report;
        this.author = author;
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

    public BullyingReport getReport() {
        return report;
    }

    public void setReport(BullyingReport report) {
        this.report = report;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
