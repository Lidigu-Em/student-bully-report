package com.lidigu.studentManagement.service;

import com.lidigu.studentManagement.dao.BullyingReportDao;
import com.lidigu.studentManagement.dao.UserDao;
import com.lidigu.studentManagement.entity.BullyingReport;
import com.lidigu.studentManagement.entity.ReportComment;
import com.lidigu.studentManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BullyingReportServiceImpl implements BullyingReportService {

    @Autowired
    private BullyingReportDao reportDao;

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public void saveReport(BullyingReport report) {
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(LocalDateTime.now());
        }
        if (report.getStatus() == null) {
            report.setStatus("PENDING");
        }
        reportDao.save(report);
    }

    @Override
    @Transactional
    public BullyingReport findById(Long id) {
        return reportDao.findById(id);
    }

    @Override
    @Transactional
    public List<BullyingReport> findAllReports() {
        return reportDao.findAll();
    }

    @Override
    @Transactional
    public List<BullyingReport> findReportsByStudent(String username) {
        User user = userDao.findByUserName(username);
        if (user != null) {
            return reportDao.findByStudentId(user.getId());
        }
        return null;
    }

    @Override
    @Transactional
    public void updateReportStatus(Long id, String status) {
        reportDao.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void proposeStatusChange(Long reportId, String proposedStatus, String teacherUsername) {
        BullyingReport report = reportDao.findById(reportId);
        User teacher = userDao.findByUserName(teacherUsername);
        if (report != null && teacher != null) {
            report.setProposedStatus(proposedStatus);
            report.setProposedBy(teacher);
            report.setStatus("TEACHER_ACTION_PENDING");
            reportDao.save(report);
        }
    }

    @Override
    @Transactional
    public void approveTeacherAction(Long reportId) {
        BullyingReport report = reportDao.findById(reportId);
        if (report != null && report.getProposedStatus() != null) {
            report.setStatus(report.getProposedStatus());
            report.setProposedStatus(null);
            report.setProposedBy(null);
            reportDao.save(report);
        }
    }

    @Override
    @Transactional
    public void rejectTeacherAction(Long reportId) {
        BullyingReport report = reportDao.findById(reportId);
        if (report != null) {
            report.setStatus("UNDER_REVIEW");
            report.setProposedStatus(null);
            report.setProposedBy(null);
            reportDao.save(report);
        }
    }

    @Override
    @Transactional
    public void addComment(Long reportId, String comment, String authorUsername) {
        BullyingReport report = reportDao.findById(reportId);
        User author = userDao.findByUserName(authorUsername);
        if (report != null && author != null) {
            ReportComment reportComment = new ReportComment(comment, LocalDateTime.now(), report, author);
            reportDao.addComment(reportComment);
        }
    }

    @Override
    @Transactional
    public List<ReportComment> findCommentsByReportId(Long reportId) {
        return reportDao.findCommentsByReportId(reportId);
    }

    @Override
    @Transactional
    public List<ReportComment> findApprovedCommentsByReportId(Long reportId) {
        return reportDao.findApprovedCommentsByReportId(reportId);
    }

    @Override
    @Transactional
    public void approveComment(Long commentId) {
        reportDao.approveComment(commentId);
    }

    @Override
    @Transactional
    public void deleteReport(Long id) {
        reportDao.deleteById(id);
    }
}
