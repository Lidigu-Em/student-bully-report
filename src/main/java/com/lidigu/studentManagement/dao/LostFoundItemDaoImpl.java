package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.LostFoundItem;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class LostFoundItemDaoImpl implements LostFoundItemDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void save(LostFoundItem item) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(item);
    }

    @Override
    public LostFoundItem findById(Long id) {
        Session session = entityManager.unwrap(Session.class);
        return session.get(LostFoundItem.class, id);
    }

    @Override
    public List<LostFoundItem> findAll() {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from LostFoundItem order by dateReported desc", LostFoundItem.class);
        return query.getResultList();
    }

    @Override
    public List<LostFoundItem> findByStudentId(int studentId) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery(
                "from LostFoundItem where reportedBy.id = :studentId order by dateReported desc", LostFoundItem.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<LostFoundItem> findByType(String type) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from LostFoundItem where type = :type order by dateReported desc",
                LostFoundItem.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Session session = entityManager.unwrap(Session.class);
        LostFoundItem item = session.get(LostFoundItem.class, id);
        if (item != null) {
            session.delete(item);
        }
    }

    @Override
    public void updateStatus(Long id, String status) {
        Session session = entityManager.unwrap(Session.class);
        LostFoundItem item = session.get(LostFoundItem.class, id);
        if (item != null) {
            item.setStatus(status);
            session.update(item);
        }
    }
}
