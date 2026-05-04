package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.Assignment;

public interface AssignmentDao {
	
	public void save(Assignment assignment);
	
	public void deleteAssignmentById(int id);
}
