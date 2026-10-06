package com.lidigu.studentManagement.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bullying_report")
public class BullyingReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "bully_details")
    private String bullyDetails;

    @Column(name = "incident_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime incidentDate;

    @Column(name = "location")
    private String location;

    @Column(name = "anonymous")
    private boolean anonymous;

    @Column(name = "status")
    private String status; // PENDING, UNDER_REVIEW, RESOLVED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;

    @Column(name = "proposed_status")
    private String proposedStatus; // status proposed by teacher, awaiting admin approval

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposed_by_id")
    private User proposedBy; // teacher who proposed the status change

    public BullyingReport() {
    }

    public BullyingReport(String description, String bullyDetails, LocalDateTime incidentDate, String location,
            boolean anonymous, String status, LocalDateTime createdAt, User reportedBy) {
        this.description = description;
        this.bullyDetails = bullyDetails;
        this.incidentDate = incidentDate;
        this.location = location;
        this.anonymous = anonymous;
        this.status = status;
        this.createdAt = createdAt;
        this.reportedBy = reportedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBullyDetails() {
        return bullyDetails;
    }

    public void setBullyDetails(String bullyDetails) {
        this.bullyDetails = bullyDetails;
    }

    public LocalDateTime getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDateTime incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getProposedStatus() {
        return proposedStatus;
    }

    public void setProposedStatus(String proposedStatus) {
        this.proposedStatus = proposedStatus;
    }

    public User getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(User proposedBy) {
        this.proposedBy = proposedBy;
    }
}
