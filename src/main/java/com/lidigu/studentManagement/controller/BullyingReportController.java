package com.lidigu.studentManagement.controller;

import com.lidigu.studentManagement.entity.BullyingReport;
import com.lidigu.studentManagement.entity.ReportComment;
import com.lidigu.studentManagement.entity.User;
import com.lidigu.studentManagement.service.BullyingReportService;
import com.lidigu.studentManagement.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class BullyingReportController {

    @Autowired
    private BullyingReportService reportService;

    @Autowired
    private UserDao userDao;

    // --- Student Views & APIs ---

    @GetMapping("/create")
    public String showReportForm(Model model) {
        model.addAttribute("report", new BullyingReport());
        return "student/bullying-report-form";
    }

    @PostMapping("/save")
    public String saveReport(@ModelAttribute("report") BullyingReport report) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.findByUserName(auth.getName());
        report.setReportedBy(user);
        reportService.saveReport(report);
        return "redirect:/reports/my";
    }

    @GetMapping("/my")
    public String showMyReports(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.findByUserName(auth.getName());
        List<BullyingReport> reports = reportService.findReportsByStudent(auth.getName());
        model.addAttribute("reports", reports);
        model.addAttribute("user", user);
        return "student/report-list";
    }

    @GetMapping("/my/view/{id}")
    public String viewStudentReport(@PathVariable("id") Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BullyingReport report = reportService.findById(id);

        // Security check: ensure student owns the report
        if (report == null || !report.getReportedBy().getUserName().equals(auth.getName())) {
            return "redirect:/access-denied";
        }

        List<ReportComment> comments = reportService.findCommentsByReportId(id);
        model.addAttribute("report", report);
        model.addAttribute("comments", comments);
        return "student/student-report-details";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BullyingReport report = reportService.findById(id);

        // Security check: ensure student owns the report and it's still PENDING
        if (report == null || !report.getReportedBy().getUserName().equals(auth.getName())) {
            return "redirect:/access-denied";
        }

        if (!"PENDING".equals(report.getStatus())) {
            // Cannot edit if it's already being reviewed or resolved
            return "redirect:/reports/my";
        }

        model.addAttribute("report", report);
        return "student/report-edit-form";
    }

    @PostMapping("/update")
    public String updateReport(@ModelAttribute("report") BullyingReport report) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BullyingReport existingReport = reportService.findById(report.getId());

        // Security check
        if (existingReport == null || !existingReport.getReportedBy().getUserName().equals(auth.getName())) {
            return "redirect:/access-denied";
        }

        if (!"PENDING".equals(existingReport.getStatus())) {
            return "redirect:/reports/my";
        }

        existingReport.setDescription(report.getDescription());
        existingReport.setBullyDetails(report.getBullyDetails());
        existingReport.setIncidentDate(report.getIncidentDate());
        existingReport.setLocation(report.getLocation());
        existingReport.setAnonymous(report.isAnonymous());

        reportService.saveReport(existingReport);
        return "redirect:/reports/my";
    }

    @PostMapping("/revoke/{id}")
    public String revokeReport(@PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BullyingReport report = reportService.findById(id);

        // Security check
        if (report == null || !report.getReportedBy().getUserName().equals(auth.getName())) {
            return "redirect:/access-denied";
        }

        reportService.deleteReport(id);
        return "redirect:/reports/my";
    }

    // --- Admin/Teacher Views & APIs ---

    @GetMapping("/all")
    public String showAllReports(Model model) {
        List<BullyingReport> reports = reportService.findAllReports();
        model.addAttribute("reports", reports);
        return "admin/admin-report-list";
    }

    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable("id") Long id, Model model) {
        BullyingReport report = reportService.findById(id);
        List<ReportComment> comments = reportService.findCommentsByReportId(id);
        model.addAttribute("report", report);
        model.addAttribute("comments", comments);
        return "admin/report-details";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        reportService.updateReportStatus(id, status);
        return "redirect:/reports/view/" + id;
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable("id") Long id, @RequestParam("comment") String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        reportService.addComment(id, comment, auth.getName());
        return "redirect:/reports/view/" + id;
    }

    // --- REST APIs (as requested) ---

    @PostMapping("/api/reports")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public void createReportApi(@RequestBody BullyingReport report) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.findByUserName(auth.getName());
        report.setReportedBy(user);
        reportService.saveReport(report);
    }

    @GetMapping("/api/reports/my")
    @ResponseBody
    @PreAuthorize("hasRole('STUDENT')")
    public List<BullyingReport> getMyReportsApi() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return reportService.findReportsByStudent(auth.getName());
    }

    @GetMapping("/api/reports")
    @ResponseBody
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public List<BullyingReport> getAllReportsApi() {
        return reportService.findAllReports();
    }
}
