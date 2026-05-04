package com.lidigu.studentManagement.controller;

import com.lidigu.studentManagement.entity.User;
import com.lidigu.studentManagement.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserDao userDao;

    @GetMapping("/")
    public String showHome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/showLoginPage";
        }

        User user = userDao.findByUserName(auth.getName());
        if (user == null) {
            return "redirect:/showLoginPage";
        }

        String role = user.getRole().getName();

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/adminPanel";
        } else if (role.equals("ROLE_STUDENT")) {
            return "redirect:/student/" + user.getId() + "/courses";
        } else if (role.equals("ROLE_TEACHER")) {
            return "redirect:/teacher/" + user.getId() + "/courses";
        }

        return "redirect:/showLoginPage";
    }
}
