package com.official.OfficialProject.controller.student;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class ViewMyGradesController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getMyExams")
    public List<Map<String, Object>> getMyExams(HttpServletRequest request, HttpSession session){
        String StuTable = TableNamesConfig.getStuExamInfoTable();
        String ExamTable = TableNamesConfig.getExamInfoTable();
        String query = "SELECT " + StuTable + ".ExamID," + ExamTable + ".ExamName FROM " + StuTable + " INNER JOIN " + ExamTable + " ON " + StuTable + ".ExamID=" + ExamTable + ".ExamID WHERE " + ExamTable +".Graded='Y' AND " + ExamTable + ".ExamType LIKE @Year AND " + StuTable + ".StudentNum='" + session.getAttribute("StudentNum") + "' ORDER BY " + ExamTable + ".EndTime DESC";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query, CommonService.getParamsMap(request));
    }

    @GetMapping("/getExamInformation")
    public List<Map<String, Object>> getExamInformation(HttpServletRequest request){
        String query = "SELECT ExamName,Course,ExamType,Score FROM " + TableNamesConfig.getExamInfoTable() + " WHERE ExamID=@ExamID";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @GetMapping("/getQTypeFullMarks")
    public List<Map<String, Object>> getQTypeFullMarks(HttpServletRequest request){
        String query = "SELECT QTypeID,QSumScore FROM " + TableNamesConfig.getExamQTypeInfoTable() + " WHERE ExamID=@ExamID";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @GetMapping("/getMyMarks")
    public List<Map<String, Object>> getMyMarks(HttpServletRequest request, HttpSession session){
        String query = "SELECT MScore,TScore,RScore,FScore,NScore,Score FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=@ExamID AND StudentNum=" + session.getAttribute("StudentNum");
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }
}
