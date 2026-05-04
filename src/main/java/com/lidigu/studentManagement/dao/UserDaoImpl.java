package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public User findByUserName(String username) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User where userName = :username", User.class);
        query.setParameter("username", username);
        try {
            return (User) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
