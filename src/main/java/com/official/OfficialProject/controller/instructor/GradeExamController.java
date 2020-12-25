package com.official.OfficialProject.controller.instructor;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class GradeExamController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/loadButtons")
    public String loadButtons(HttpSession session){
        String query = "SELECT TOP(1) QOrder FROM " + TableNamesConfig.getFBQTable() + " WHERE ExamID=" + session.getAttribute("ExamID") + " ORDER BY QOrder DESC";
        return ZZSDBService.GetSingleValue(jdbcTemplate, query);
    }

    @GetMapping("/loadQuestionToGrade")
    public List<Map<String, Object>> loadQuestionToGrade(HttpSession session, HttpServletRequest request){
        String query = "SELECT Question, Answer1, Answer2, Answer3, Answer4 FROM " + TableNamesConfig.getFBQTable() + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder=@QOrder";
        return ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
    }

    @GetMapping("/loadAnswers")
    public List<Map<String, Object>> loadAnswers(HttpSession session, HttpServletRequest request){
        String query = "SELECT StudentNum, Answer1, Answer2, Answer3, Answer4, Score FROM " + TableNamesConfig.getFBQAnswerTable() + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder=@QOrder";
        return ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
    }

    @PostMapping("/gradeQuestion")
    public void gradeQuestion(HttpServletRequest request,
                              HttpSession session,
                              @RequestParam(value = "StudentNums") String StudentNums,
                              @RequestParam(value = "correct") Boolean correct){
        String QOrder = CommonService.getCookieValue(request,"QOrder");
        String QTable = TableNamesConfig.getFBQTable();
        String ATable = TableNamesConfig.getFBQAnswerTable();
        String sql = "";
        if (correct){
            sql = "UPDATE " + ATable + " SET " + ATable + ".Score=" + QTable + ".Score FROM " + ATable + " INNER JOIN " + QTable + " ON " + ATable + ".ExamID=" + QTable + ".ExamID AND " + ATable + ".QOrder=" + QTable + ".QOrder WHERE " + ATable + ".ExamID=" + session.getAttribute("ExamID") + " AND " + ATable + ".QOrder=" + QOrder + " AND " + ATable + ".StudentNum IN " + StudentNums;
        }
        if (!correct){
            sql = "UPDATE " + ATable + " SET Score=0 WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder=" + QOrder + " AND StudentNum IN " + StudentNums;
        }
        ZZSDBService.InsertUpdateDelete(jdbcTemplate,sql);
    }

    @PostMapping("finishGrading")
    public String finishGrading(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        String ExamID = (String) session.getAttribute("ExamID");
        String stuExamInfoTable = TableNamesConfig.getStuExamInfoTable();
        String sql1 = "UPDATE " + stuExamInfoTable + " SET " + stuExamInfoTable + ".FScore = temp.Score FROM " + stuExamInfoTable + " INNER JOIN (SELECT StudentNum, SUM(Score) AS Score FROM " + TableNamesConfig.getFBQAnswerTable() + "  WHERE ExamID=" + ExamID + " GROUP BY StudentNum) AS temp ON StudentExamInfo.StudentNum = temp.StudentNum WHERE ExamID=" + ExamID;
        String sql2 = "UPDATE " + stuExamInfoTable + " SET Score = MScore+TScore+RScore+FScore+NScore WHERE ExamID=" + ExamID;
        String sql3 = "UPDATE " + TableNamesConfig.getExamInfoTable() + " SET Graded='Y' WHERE ExamID=" + ExamID;
        String[] sqls = {sql1,sql2,sql3};
        ZZSDBService.InsertUpdateDeleteForBatch(jdbcTemplate,sqls);
        session.removeAttribute("ExamID");
        CommonService.removeCookie(request, response,"TotalQuestions");
        CommonService.removeCookie(request, response,"QOrder");
        return "/InstructorHome.html";
    }
}
