package com.official.OfficialProject.controller.instructor;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.EditQuestionService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class ViewStudentGradesController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getPastExams")
    public List<Map<String, Object>> getPastExams(HttpServletRequest request, HttpSession session){
        String query = "SELECT ExamID, ExamName FROM " + TableNamesConfig.getExamInfoTable() + " WHERE StaffNum=" + session.getAttribute("StaffNum") + " AND Course=@CourseID AND Graded='Y' AND ExamType LIKE @Year ORDER BY EndTime DESC";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @GetMapping("/getStudentGrades")
    public List<Map<String, Object>> getStudentGrades(HttpServletRequest request){
        String query = "SELECT * FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID ORDER BY Score DESC";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @GetMapping("/getQSumScore")
    public List<Map<String, Object>> getQSumScore(HttpServletRequest request){
        String query = "SELECT QTypeID, QSumScore FROM " + TableNamesConfig.getExamQTypeInfoTable() + " WHERE ExamID=@ExamID";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @PostMapping("/editStudentGrades")
    public void editStudentGrades(HttpServletRequest request){
        String sql = "UPDATE " + TableNamesConfig.getStuExamInfoTable() + " SET MScore=@MScore, TScore=@TScore, RScore=@RScore, FScore=@FScore, NScore=@NScore, Score=@Score WHERE ExamID=@ExamID AND StudentNum=@id";
        ZZSDBService.InsertUpdateDelete(jdbcTemplate,sql,CommonService.getParamsMap(request));
    }

    @GetMapping("/getExamStats")
    public List<Map<String, Object>> getExamStats(HttpServletRequest request){
        String query = "SELECT Count(Score) AS count, CAST(AVG(Score) AS DECIMAL(10,2)) AS mean, CAST(STDEV(Score) AS DECIMAL(10,2)) AS std, MAX(Score) AS max, MIN(Score) AS min FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @GetMapping("/getScoreAndOrder")
    public List<Map<String, Object>> getScoreAndOrder(HttpServletRequest request){
        String query = "SELECT QTypeOrder, Score FROM " + TableNamesConfig.getExamInfoTable() + " WHERE ExamID=@ExamID";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

//    @GetMapping("/getPassed")
//    public String getPassed(HttpServletRequest request){
//        String query = "SELECT Count(Score) FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND Score > (SELECT Score/2 FROM " + TableNamesConfig.getExamInfoTable() + " WHERE ExamID=@ExamID )";
//        return ZZSDBService.GetSingleValue(jdbcTemplate,query,CommonService.getParamsMap(request));
//    }

    @GetMapping("/getScoreRangeCount")
    public List<Map<String, Object>> getScoreRangeCount(HttpServletRequest request){
        String query = "SELECT Count(Score) AS num FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND ( Score/@Score >= 0.9 ) UNION ALL\n" +
                "SELECT Count(Score) AS num FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND ( Score/@Score BETWEEN 0.8 AND 0.89 ) UNION ALL\n" +
                "SELECT Count(Score) AS num FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND ( Score/@Score BETWEEN 0.7 AND 0.79 ) UNION ALL\n" +
                "SELECT Count(Score) AS num FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND ( Score/@Score BETWEEN 0.6 AND 0.69 ) UNION ALL\n" +
                "SELECT Count(Score) AS num FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND ( Score/@Score BETWEEN 0.5 AND 0.59 ) UNION ALL\n" +
                "SELECT Count(Score) AS num FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND ( Score/@Score < 0.5 )";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @GetMapping("/getQCorrectRate")
    public List<Map<String, Object>> getQCorrectRate(HttpServletRequest request,
                                                     @RequestParam(value = "QTypeID") String QTypeID){
        String query = "SELECT Count(Score)*100/@Count AS qCorrectRate FROM " + EditQuestionService.getAnswerTable(QTypeID) + " WHERE ExamID=@ExamID AND Score > 0 GROUP BY QOrder";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }
}
