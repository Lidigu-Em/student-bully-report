package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.BullyingReport;
import com.lidigu.studentManagement.entity.ReportComment;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class BullyingReportDaoImpl implements BullyingReportDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void save(BullyingReport report) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(report);
    }

    @Override
    public BullyingReport findById(Long id) {
        Session session = entityManager.unwrap(Session.class);
        return session.get(BullyingReport.class, id);
    }

    @Override
    public List<BullyingReport> findAll() {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery("from BullyingReport", BullyingReport.class).getResultList();
    }

    @Override
    public List<BullyingReport> findByStudentId(int studentId) {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery("from BullyingReport where reportedBy.id = :studentId", BullyingReport.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public void updateStatus(Long id, String status) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("update BullyingReport set status = :status where id = :id");
        query.setParameter("status", status);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public void addComment(ReportComment comment) {
        Session session = entityManager.unwrap(Session.class);
        session.save(comment);
    }

    @Override
    public List<ReportComment> findCommentsByReportId(Long reportId) {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery("from ReportComment where report.id = :reportId", ReportComment.class)
                .setParameter("reportId", reportId)
                .getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Session session = entityManager.unwrap(Session.class);
        BullyingReport report = session.get(BullyingReport.class, id);
        if (report != null) {
            session.delete(report);
        }
    }

    @Override
    public List<ReportComment> findApprovedCommentsByReportId(Long reportId) {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery(
                "from ReportComment where report.id = :reportId and approved = true",
                ReportComment.class)
                .setParameter("reportId", reportId)
                .getResultList();
    }

    @Override
    public void approveComment(Long commentId) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("update ReportComment set approved = true where id = :id");
        query.setParameter("id", commentId);
        query.executeUpdate();
    }
}
