package com.lidigu.studentManagement.controller;

import com.lidigu.studentManagement.entity.LostFoundItem;
import com.lidigu.studentManagement.entity.User;
import com.lidigu.studentManagement.dao.UserDao;
import com.lidigu.studentManagement.service.LostFoundItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/lostfound")
public class LostFoundItemController {

    @Autowired
    private LostFoundItemService itemService;

    @Autowired
    private UserDao userDao;

    @GetMapping("/my")
    public String showMyItems(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.findByUserName(auth.getName());
        List<LostFoundItem> items = itemService.findByStudent(auth.getName());
        model.addAttribute("items", items);
        model.addAttribute("user", user);
        return "student/my-lost-found";
    }

    @GetMapping("/create")
    public String showCreateForm(@RequestParam("type") String type, Model model) {
        LostFoundItem item = new LostFoundItem();
        item.setType(type); // LOST or FOUND
        model.addAttribute("item", item);
        return "student/lost-found-form";
    }

    @PostMapping("/save")
    public String saveItem(@ModelAttribute("item") LostFoundItem item) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        itemService.saveItem(item, auth.getName());
        return "redirect:/lostfound/my";
    }

    @GetMapping("/all")
    public String showAllItems(Model model) {
        List<LostFoundItem> items = itemService.findAll();
        model.addAttribute("items", items);
        return "admin/all-lost-found";
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        itemService.updateStatus(id, status);
        return "redirect:/lostfound/all";
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") Long id) {
        itemService.deleteItem(id);
        return "redirect:/lostfound/my";
    }

    @GetMapping("/view/{id}")
    public String viewItem(@PathVariable("id") Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LostFoundItem item = itemService.findById(id);
        if (item == null) {
            return "redirect:/lostfound/all";
        }
        User currentUser = userDao.findByUserName(auth.getName());
        boolean isStudent = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        boolean isTeacher = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<com.lidigu.studentManagement.entity.LostFoundComment> comments = itemService.findCommentsByItemId(id);
        model.addAttribute("item", item);
        model.addAttribute("comments", comments);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("isAdmin", isAdmin);
        return "admin/lost-found-details";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable("id") Long id, @RequestParam("comment") String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        itemService.addComment(id, comment, auth.getName());
        return "redirect:/lostfound/view/" + id;
    }
}
