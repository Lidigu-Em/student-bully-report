package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.LostFoundItem;
import java.util.List;

public interface LostFoundItemDao {

    void save(LostFoundItem item);

    LostFoundItem findById(Long id);

    List<LostFoundItem> findAll();

    List<LostFoundItem> findByStudentId(int studentId);

    List<LostFoundItem> findByType(String type);

    void deleteById(Long id);

    void updateStatus(Long id, String status);
}
