package com.official.OfficialProject.controller.admin.system;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.jqGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class ExamVersionController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getCourse")
    public List<Map<String, Object>> getCourse() {
        return jqGridService.getFullTable(jdbcTemplate, TableNamesConfig.getCourseTable());
    }

    @PostMapping("/editCourse")
    public int editCourse(HttpServletRequest request) {
        return jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getCourseTable(), "CourseID", false);
    }

    @GetMapping("/getExamType")
    public List<Map<String, Object>> getExamType() {
        return jqGridService.getFullTable(jdbcTemplate, TableNamesConfig.getExamTypeTable());
    }

    @PostMapping("/editExamType")
    public int editExamType(HttpServletRequest request) {
        return jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getExamTypeTable(), "ExamID", false);
    }

    @GetMapping("/getQuestionType")
    public List<Map<String, Object>> getQuestionType() {
        return jqGridService.getFullTable(jdbcTemplate, TableNamesConfig.getQTypeTable());

    }

    @PostMapping("/editQuestionType")
    public int editQuestionType(HttpServletRequest request) {
        return jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getQTypeTable(), "QTypeID", false);
    }

}
