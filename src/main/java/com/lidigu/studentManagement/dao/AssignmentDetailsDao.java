package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.AssignmentDetails;

public interface AssignmentDetailsDao {
	
	public AssignmentDetails findByAssignmentAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId);
	
	public void save(AssignmentDetails studentCourseAssignmentDetails);
}
