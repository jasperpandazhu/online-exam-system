package com.official.OfficialProject.controller.instructor;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.EditQuestionService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class InstructorHomeController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @GetMapping("/getInstructorExams")
    public List<Map<String, Object>> getPendingExams(HttpSession session) {
        String query = "SELECT ExamName, Submitted, EndTime, Graded FROM " + TableNamesConfig.getExamInfoTable() + " WHERE StaffNum = " + session.getAttribute("StaffNum");
        return jdbcTemplate.queryForList(query);
    }

    @GetMapping("/getExam")
    public void getExam(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, SQLException {
        // create exam
        Optional<Object> optionalParam = Optional.ofNullable(request.getParameter("createExamSelect"));
        String createExamSelect = optionalParam.isPresent() ? optionalParam.get().toString() : "";
        if (createExamSelect.length() > 0){
            if (createExamSelect.equals("new")){
                response.sendRedirect("/CreateNewExam.html");
            }
            else{
                response.sendRedirect(EditQuestionService.getExamInfo(dataSource, createExamSelect, session, "create"));
            }
        }
        // view submitted exam
        else {
            String viewExamSelect = request.getParameter("viewExamSelect");
            String url = EditQuestionService.getExamInfo(dataSource, viewExamSelect, session, "view");
            // set a cookie to indicate the order of question types to be loaded
            String QTypeOrder = ZZSDBService.GetSingleValue(jdbcTemplate, "SELECT QTypeOrder FROM " + TableNamesConfig.getExamInfoTable() + " WHERE ExamID=" + session.getAttribute("ExamID"));
            CommonService.setCookie(response,"QTypeOrder", QTypeOrder);
            // set a cookie to indicate the full score of the exam
            String Score = ZZSDBService.GetSingleValue(jdbcTemplate, "SELECT Score FROM " + TableNamesConfig.getExamInfoTable() + " WHERE ExamID=" + session.getAttribute("ExamID"));
            CommonService.setCookie(response,"Score", Score);
            response.sendRedirect(url);
        }
    }

    @GetMapping("/gradeExam")
    public void gradeExam(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, SQLException {
        String gradeExamSelect = request.getParameter("gradeExamSelect");
        String query = "SELECT * FROM " + TableNamesConfig.getExamInfoTable() + " WHERE ExamName = ? AND StaffNum = " + session.getAttribute("StaffNum");
        Connection conn = dataSource.getConnection();
        PreparedStatement preStmt = conn.prepareStatement(query);
        preStmt.setString(1, gradeExamSelect);
        ResultSet rs = preStmt.executeQuery();
        String examID;
        String examName="";
        String examType ="";
        String courseID ="";
        while (rs.next()){
            examID = rs.getString("ExamID");
            examName = rs.getString("ExamName");
            examType = rs.getString("ExamType");
            courseID = rs.getString("Course");
            session.setAttribute("ExamID",examID);
        }
        rs.close();
        preStmt.close();
        conn.close();
        String url = "/GradeExam.html?ExamName="+examName+"&ExamType="+examType+"&Course="+courseID;
        response.sendRedirect(url);
    }

    @GetMapping("/viewStudentGrades")
    public void viewStudentGrades(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect ("/ViewStudentGrades.html?CourseID=" + request.getParameter("viewGradesSelect") + "&Year=" + request.getParameter("year"));
    }

    @GetMapping("/myQuestionPool")
    public void myQuestionPool(HttpServletResponse response) throws IOException {
        response.sendRedirect ("/MyQuestionPool.html");
    }
}
