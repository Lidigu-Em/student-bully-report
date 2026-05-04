package com.lidigu.studentManagement.service;

import com.lidigu.studentManagement.dao.LostFoundItemDao;
import com.lidigu.studentManagement.dao.UserDao;
import com.lidigu.studentManagement.entity.LostFoundItem;
import com.lidigu.studentManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LostFoundItemServiceImpl implements LostFoundItemService {

    @Autowired
    private LostFoundItemDao itemDao;

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public void saveItem(LostFoundItem item, String username) {
        if (item.getId() == null) {
            User user = userDao.findByUserName(username);
            item.setReportedBy(user);
            item.setDateReported(LocalDateTime.now());
            if (item.getStatus() == null) {
                item.setStatus("PENDING");
            }
        }
        itemDao.save(item);
    }

    @Override
    @Transactional
    public LostFoundItem findById(Long id) {
        return itemDao.findById(id);
    }

    @Override
    @Transactional
    public List<LostFoundItem> findAll() {
        return itemDao.findAll();
    }

    @Override
    @Transactional
    public List<LostFoundItem> findByStudent(String username) {
        User user = userDao.findByUserName(username);
        return itemDao.findByStudentId(user.getId());
    }

    @Override
    @Transactional
    public List<LostFoundItem> findByType(String type) {
        return itemDao.findByType(type);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        itemDao.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        itemDao.updateStatus(id, status);
    }
}
