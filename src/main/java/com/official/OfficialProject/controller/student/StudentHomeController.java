package com.official.OfficialProject.controller.student;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class StudentHomeController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getStudentCourses")
    public List<Map<String, Object>> getStudentCourses(HttpSession session) {
        String query = "SELECT Courses FROM " + TableNamesConfig.getStudentTable() + " WHERE StudentNum = " + session.getAttribute("StudentNum");
        return jdbcTemplate.queryForList(query);
    }

    @GetMapping("/getCurrentExam")
    public String getCurrentExam(HttpServletRequest request, HttpSession session) {
        String query = "SELECT TOP(1) * FROM " + TableNamesConfig.getExamInfoTable() + " WHERE Course=@Course AND Submitted='Y' AND CURRENT_TIMESTAMP < EndTime ORDER BY StartTime ASC";
        SqlRowSet rs = ZZSDBService.getSqlRowSet(jdbcTemplate, query, CommonService.getParamsMap(request));
        JSONArray jsonArr = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        while (rs.next()){
            String query2 = "SELECT Submitted FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=" + rs.getString("ExamID") + " AND StudentNum=" + session.getAttribute("StudentNum") + " AND Submitted=1";
            Optional<Object> submitted = Optional.ofNullable(ZZSDBService.GetSingleValue(jdbcTemplate,query2));
            if (submitted.isPresent()){
                break;
            }
            else {
                jsonObj.put("ExamID", rs.getString("ExamID"));
                session.setAttribute("ExamID",rs.getString("ExamID"));
                jsonObj.put("ExamName", rs.getString("ExamName"));
                session.setAttribute("ExamName",rs.getString("ExamName"));
                jsonObj.put("ExamType", rs.getString("ExamType"));
                jsonObj.put("Course", rs.getString("Course"));
                jsonObj.put("StartTime", rs.getString("StartTime"));
                session.setAttribute("StartTime",rs.getString("StartTime"));
                jsonObj.put("EndTime", rs.getString("EndTime"));
                session.setAttribute("EndTime",rs.getString("EndTime"));
                jsonObj.put("QTypeOrder", rs.getString("QTypeOrder"));
                session.setAttribute("QTypeOrder", rs.getString("QTypeOrder"));
                jsonObj.put("Score", rs.getString("Score"));
                jsonArr.add(0, jsonObj);
                return jsonArr.toString();
            }
        }
        return "";
    }

    @GetMapping("/viewMyGrades")
    public void viewStudentGrades(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect ("/ViewMyGrades.html?Year=" + request.getParameter("year"));
    }
}
