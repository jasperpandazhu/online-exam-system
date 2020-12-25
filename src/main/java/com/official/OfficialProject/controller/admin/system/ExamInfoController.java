package com.official.OfficialProject.controller.admin.system;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.jqGridService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class ExamInfoController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getExamInfo")
    public List<Map<String, Object>> getExamInfo(HttpServletRequest request) {
        String ExamInfo = TableNamesConfig.getExamInfoTable();
        String Instructor = TableNamesConfig.getInstructorTable();
        String query = "SELECT ExamID, ExamName, ExamType, Course, (" +
                Instructor + ".FirstName + ' ' + " + Instructor + ".LastName + ' ' + " + Instructor + ".StaffNum) AS Instructor, " +
                "StartTime, EndTime, QTypeOrder, Score, Submitted, Graded FROM " +
                ExamInfo + " LEFT JOIN " + Instructor + " ON " +
                ExamInfo + ".StaffNum = " + Instructor + ".StaffNum WHERE EndTime BETWEEN @From AND @To";
        return ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
    }

    @PostMapping("/editExamInfo")
    public int editExamInfo(HttpServletRequest request) {
        return jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getExamInfoTable(), "ExamID", true);
    }
}
