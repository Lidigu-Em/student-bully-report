package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.BullyingReport;
import com.lidigu.studentManagement.entity.ReportComment;

import java.util.List;

public interface BullyingReportDao {

    void save(BullyingReport report);

    BullyingReport findById(Long id);

    List<BullyingReport> findAll();

    List<BullyingReport> findByStudentId(int studentId);

    void updateStatus(Long id, String status);

    void addComment(ReportComment comment);

    List<ReportComment> findCommentsByReportId(Long reportId);

    List<ReportComment> findApprovedCommentsByReportId(Long reportId);

    void approveComment(Long commentId);

    void deleteById(Long id);
}
