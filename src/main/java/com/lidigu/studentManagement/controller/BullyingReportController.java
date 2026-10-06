package com.lidigu.studentManagement.controller;

import com.lidigu.studentManagement.entity.BullyingReport;
import com.lidigu.studentManagement.entity.ReportComment;
import com.lidigu.studentManagement.entity.User;
import com.lidigu.studentManagement.service.BullyingReportService;
import com.lidigu.studentManagement.service.ReportExportService;
import com.lidigu.studentManagement.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    @Autowired
    private ReportExportService exportService;

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }

    private boolean isTeacher(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_TEACHER"));
    }

    // ----------------------------------------------------------------
    // Student — create & manage own reports
    // ----------------------------------------------------------------

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

        if (report == null || !report.getReportedBy().getUserName().equals(auth.getName())) {
            return "redirect:/access-denied";
        }

        // Students only see report details; comments are for teacher/admin board
        model.addAttribute("report", report);
        return "student/student-report-details";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BullyingReport report = reportService.findById(id);

        if (report == null || !report.getReportedBy().getUserName().equals(auth.getName())) {
            return "redirect:/access-denied";
        }
        if (!"PENDING".equals(report.getStatus())) {
            return "redirect:/reports/my";
        }
        model.addAttribute("report", report);
        return "student/report-edit-form";
    }

    @PostMapping("/update")
    public String updateReport(@ModelAttribute("report") BullyingReport report) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BullyingReport existingReport = reportService.findById(report.getId());

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

        if (report == null || !report.getReportedBy().getUserName().equals(auth.getName())) {
            return "redirect:/access-denied";
        }
        reportService.deleteReport(id);
        return "redirect:/reports/my";
    }

    // ----------------------------------------------------------------
    // Admin & Teacher — view all reports list
    // ----------------------------------------------------------------

    @GetMapping("/all")
    public String showAllReports(Model model) {
        List<BullyingReport> reports = reportService.findAllReports();
        model.addAttribute("reports", reports);
        return "admin/admin-report-list";
    }

    // ----------------------------------------------------------------
    // Admin & Teacher — view a single report with full audit trail
    // ----------------------------------------------------------------

    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable("id") Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BullyingReport report = reportService.findById(id);
        // Admin sees all comments (including pending); teacher too; student uses separate route
        List<ReportComment> comments = reportService.findCommentsByReportId(id);
        model.addAttribute("report", report);
        model.addAttribute("comments", comments);
        model.addAttribute("isAdmin", isAdmin(auth));
        model.addAttribute("isTeacher", isTeacher(auth));
        return "admin/report-details";
    }

    // ----------------------------------------------------------------
    // Status changes — role-aware
    // ----------------------------------------------------------------

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAdmin(auth)) {
            // Admin directly sets the status
            reportService.updateReportStatus(id, status);
        } else if (isTeacher(auth)) {
            // Teacher proposes the status — needs admin approval
            reportService.proposeStatusChange(id, status, auth.getName());
        }
        return "redirect:/reports/view/" + id;
    }

    // ----------------------------------------------------------------
    // Comments — auto-approved for admin, pending for teacher
    // ----------------------------------------------------------------

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable("id") Long id, @RequestParam("comment") String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        reportService.addComment(id, comment, auth.getName());
        return "redirect:/reports/view/" + id;
    }

    // ----------------------------------------------------------------
    // Admin — approve / reject teacher actions
    // ----------------------------------------------------------------

    @PostMapping("/{id}/approveAction")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveTeacherAction(@PathVariable("id") Long id) {
        reportService.approveTeacherAction(id);
        return "redirect:/reports/view/" + id;
    }

    @PostMapping("/{id}/rejectAction")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectTeacherAction(@PathVariable("id") Long id) {
        reportService.rejectTeacherAction(id);
        return "redirect:/reports/view/" + id;
    }

    @PostMapping("/{id}/approveComment/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId) {
        reportService.approveComment(commentId);
        return "redirect:/reports/view/" + id;
    }

    // ----------------------------------------------------------------
    // Export — PDF and Excel
    // ----------------------------------------------------------------

    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> exportExcel() {
        List<BullyingReport> reports = reportService.findAllReports();
        byte[] data = exportService.exportToExcel(reports);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bullying-reports.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<byte[]> exportPdf() {
        List<BullyingReport> reports = reportService.findAllReports();
        byte[] data = exportService.exportToPdf(reports);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bullying-reports.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    // ----------------------------------------------------------------
    // REST APIs (kept for backward compat)
    // ----------------------------------------------------------------

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
