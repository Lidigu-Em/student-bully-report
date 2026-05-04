package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.Role;

public interface RoleDao {

	public Role findRoleByName(String theRoleName);

	public void save(Role role);
}
