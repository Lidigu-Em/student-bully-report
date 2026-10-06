package com.lidigu.studentManagement.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lidigu.studentManagement.dao.RoleDao;
import com.lidigu.studentManagement.entity.Role;
import com.lidigu.studentManagement.service.StudentService;
import com.lidigu.studentManagement.service.TeacherService;
import com.lidigu.studentManagement.user.UserDto;

@Controller
@RequestMapping("/register")
public class RegistrationController {

	@Autowired
	private StudentService studentService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private RoleDao roleDao;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {

		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model theModel) {
		theModel.addAttribute("userDto", new UserDto());
		return "registration/registration-form";
	}

	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(@Valid @ModelAttribute("userDto") UserDto user,
			BindingResult theBindingResult, Model theModel) {
		if (theBindingResult.hasErrors()) {
			return "registration/registration-form";
		}

		String userName = user.getUserName();

		// if username already exists in db
		if (studentService.findByStudentName(userName) != null) {
			theModel.addAttribute("userDto", new UserDto());
			theModel.addAttribute("registrationError", "User name already exists!");
			return "registration/registration-form";
		}

		// All self-registrations are students
		Role role = roleDao.findRoleByName("ROLE_STUDENT");
		user.setRole(role);
		studentService.save(user);

		return "redirect:/showLoginPage?registrationSuccess";
	}
}
