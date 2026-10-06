package com.lidigu.studentManagement.service;

import com.lidigu.studentManagement.entity.BullyingReport;
import com.lidigu.studentManagement.entity.ReportComment;

import java.util.List;

public interface BullyingReportService {

    void saveReport(BullyingReport report);

    BullyingReport findById(Long id);

    List<BullyingReport> findAllReports();

    List<BullyingReport> findReportsByStudent(String username);

    void updateReportStatus(Long id, String status);

    void proposeStatusChange(Long reportId, String proposedStatus, String teacherUsername);

    void approveTeacherAction(Long reportId);

    void rejectTeacherAction(Long reportId);

    void addComment(Long reportId, String comment, String authorUsername);

    List<ReportComment> findCommentsByReportId(Long reportId);

    List<ReportComment> findApprovedCommentsByReportId(Long reportId);

    void approveComment(Long commentId);

    void deleteReport(Long id);
}
