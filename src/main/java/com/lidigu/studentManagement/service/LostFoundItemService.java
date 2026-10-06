package com.lidigu.studentManagement.service;

import com.lidigu.studentManagement.entity.LostFoundItem;
import java.util.List;

public interface LostFoundItemService {

    void saveItem(LostFoundItem item, String username);

    LostFoundItem findById(Long id);

    List<LostFoundItem> findAll();

    List<LostFoundItem> findByStudent(String username);

    List<LostFoundItem> findByType(String type);

    void deleteItem(Long id);

    void updateStatus(Long id, String status);

    void addComment(Long itemId, String comment, String authorUsername);

    List<com.lidigu.studentManagement.entity.LostFoundComment> findCommentsByItemId(Long itemId);
}
