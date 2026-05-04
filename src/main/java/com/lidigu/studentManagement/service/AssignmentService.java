package com.lidigu.studentManagement.service;

import com.lidigu.studentManagement.entity.Assignment;

public interface AssignmentService {
	
	public void save(Assignment assignment);
	
	public void deleteAssignmentById(int id);
}
