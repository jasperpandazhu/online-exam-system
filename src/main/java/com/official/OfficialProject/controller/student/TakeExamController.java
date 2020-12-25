package com.official.OfficialProject.controller.student;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.EditQuestionService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
public class TakeExamController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getEndTime")
    public String getEndTime(HttpSession session){
        return (String) session.getAttribute("EndTime");
    }

    @GetMapping("/getQuestionTypeInfo")
    public List<Map<String, Object>> getQuestionTypeInfo(HttpSession session, HttpServletRequest request) {
        String query = "SELECT QSumScore AS Total, QNum AS Number FROM " + TableNamesConfig.getExamQTypeInfoTable() + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QTypeID=@QTypeID";
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @PostMapping("/createAnswers")
    public String createAnswers(HttpSession session, HttpServletResponse response) throws ParseException {
        String ExamID = (String) session.getAttribute("ExamID");
        String StudentNum = (String) session.getAttribute("StudentNum");
        String QTypeOrder = (String) session.getAttribute("QTypeOrder");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if((sdf.parse(now).after(sdf.parse((String)session.getAttribute("StartTime")))) && (sdf.parse(now).before(sdf.parse((String)session.getAttribute("EndTime"))))){
            SqlRowSet rs = ZZSDBService.getSqlRowSet(jdbcTemplate, "SELECT * FROM " + TableNamesConfig.getStuExamInfoTable() + " WHERE ExamID=" + ExamID + "AND StudentNum=" + StudentNum);
            if (!rs.next()){
                String sql2 = "INSERT INTO " + TableNamesConfig.getStuExamInfoTable() + " (ExamID,StudentNum,ExamStart,MScore,TScore,RScore,FScore,NScore,Score) VALUES(" + ExamID + "," + StudentNum + ",'" + now + "',0,0,0,0,0,0)";
                ZZSDBService.InsertUpdateDelete(jdbcTemplate,sql2);
                for ( int i = 0; i < QTypeOrder.length(); i++ ){
                    String QTypeID = QTypeOrder.substring(i,i+1);
                    String QTable = EditQuestionService.getQTypeTable(QTypeID);
                    String ATable = EditQuestionService.getAnswerTable(QTypeID);
                    String sql1 = "INSERT INTO " + ATable + " (ExamID,StudentNum,AnswerDate,QTypeID,QOrder,Score) SELECT ExamID,'" + StudentNum + "','" + now + "','" + QTypeID +"',QOrder,0 FROM " + QTable + " WHERE ExamID=" + ExamID + " ORDER BY NEWID()";
                    ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql1);
                }
            }
            CommonService.setCookie(response,"QTypeOrder",QTypeOrder);
            return "ExamInProgress.html?ExamName="+session.getAttribute("ExamName");
        }
        return "#";
    }

    @GetMapping("/getQTypeScore")
    public String getQTypeScore(HttpServletRequest request, HttpSession session){
        String query = "SELECT QSumScore FROM " + TableNamesConfig.getExamQTypeInfoTable() + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QTypeID=@QTypeID";
        return ZZSDBService.GetSingleValue(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    @GetMapping("/getQuestionsRandom")
    public List<Map<String, Object>> getQuestionsRandom(HttpSession session,
                                                        @RequestParam(value = "QTypeID") String QTypeID){
        String Table = EditQuestionService.getAnswerTable(QTypeID);
        String query = "SELECT id, QOrder, ISNULL(Answered,0) AS Answered FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND StudentNum='" + session.getAttribute("StudentNum") + "' ORDER BY id ASC";
        return jdbcTemplate.queryForList(query);
    }

    @GetMapping("/loadExamQuestion")
    public List<Map<String, Object>> loadQuestion(HttpServletRequest request, HttpSession session){
        String QTypeID = CommonService.getCookieValue(request, "QTypeID");
        String QNum = CommonService.getCookieValue(request, "QOrder");
        String Table = EditQuestionService.getQTypeTable(QTypeID);
        String query;
        if ( QTypeID.equals("M") || QTypeID.equals("R") ){
            query = "SELECT Score, Question, ChoiceA, ChoiceB, ChoiceC, ChoiceD FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QTypeID='" + QTypeID + "' AND QOrder=" + CommonService.stringToInt(QNum);
        }
        else {
            query = "SELECT Score, Question FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QTypeID='" + QTypeID + "' AND QOrder=" + CommonService.stringToInt(QNum);
        }
        return jdbcTemplate.queryForList(query);
    }

    @PostMapping("/answerQuestion")
    public void answerQuestion(HttpServletRequest request,
                               HttpSession session,
                               @RequestParam(value = "QTypeID") String QTypeID){
        String Table = EditQuestionService.getAnswerTable(QTypeID);
        String sql;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);
        if ( QTypeID.equals("R") || QTypeID.equals("F") ){
            sql = "UPDATE " + Table + " SET AnswerDate='" + now + "', Answer1=@Answer1, Answer2=@Answer2, Answer3=@Answer3, Answer4=@Answer4, Answered=1 WHERE ExamID=" + session.getAttribute("ExamID") + " AND StudentNum='" + session.getAttribute("StudentNum") + "' AND QTypeID=@QTypeID AND QOrder=@QOrder";
        }
        else {
            sql = "UPDATE " + Table + " SET AnswerDate='" + now + "', Answer=@Answer, Answered=1 WHERE ExamID=" + session.getAttribute("ExamID") + " AND StudentNum='" + session.getAttribute("StudentNum") + "' AND QTypeID=@QTypeID AND QOrder=@QOrder";
        }
        ZZSDBService.LJZInsertUpdateDelete(jdbcTemplate, sql, CommonService.getParamsMap(request));
    }

    @GetMapping("/loadAnswer")
    public List<Map<String, Object>> loadAnswer(HttpServletRequest request,
                                                HttpSession session,
                                                @RequestParam(value = "QTypeID") String QTypeID){
        String Table = EditQuestionService.getAnswerTable(QTypeID);
        String query;
        if ( QTypeID.equals("R") || QTypeID.equals("F") ){
            query = "SELECT Answer1, Answer2, Answer3, Answer4, Answered FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND StudentNum='" + session.getAttribute("StudentNum") + "' AND QTypeID=@QTypeID AND QOrder=@QOrder";
        }
        else {
            query = "SELECT Answer, Answered FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND StudentNum='" + session.getAttribute("StudentNum") + "' AND QTypeID=@QTypeID AND QOrder=@QOrder";
        }
        return ZZSDBService.getQueryListMap(jdbcTemplate,query,CommonService.getParamsMap(request));
    }

    // submit exam and grade automatically
    @PostMapping("/studentSubmitExam")
    public String submitExam(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now().format(formatter);
        String stuExamInfoTable = TableNamesConfig.getStuExamInfoTable();
        String sql1 = "UPDATE " + stuExamInfoTable + " SET ExamEnd='" + now + "', Submitted=1 WHERE ExamID=" + session.getAttribute("ExamID") + " AND StudentNum='" + session.getAttribute("StudentNum") + "'";
        ZZSDBService.InsertUpdateDelete(jdbcTemplate,sql1);
        String ExamID = (String) session.getAttribute("ExamID");
        String QTypeOrder = (String) session.getAttribute("QTypeOrder");
        // automatic grading calculate each question type score
        for ( int i = 0; i < QTypeOrder.length(); i++ ){
            String QTypeID = QTypeOrder.substring(i,i+1);
            String QTable = EditQuestionService.getQTypeTable(QTypeID);
            String ATable = EditQuestionService.getAnswerTable(QTypeID);
            String sql2="";
            if ( QTypeID.equals("R") ){
                sql2 = "UPDATE " + ATable + " SET " + ATable + ".Score=" + QTable + ".Score FROM " + ATable + " INNER JOIN " + QTable + " ON " + ATable + ".ExamID=" + QTable + ".ExamID AND " + ATable + ".QOrder=" + QTable + ".QOrder WHERE " + ATable + ".ExamID=" + ExamID + " AND " + ATable + ".Answer1=" + QTable + ".Answer1 AND " + ATable + ".Answer2=" + QTable + ".Answer2 AND "  + ATable + ".Answer3=" + QTable + ".Answer3 AND " + ATable + ".Answer4=" + QTable + ".Answer4";
            }
            if( QTypeID.equals("M") || QTypeID.equals("T") || QTypeID.equals("N") ) {
                sql2 = "UPDATE " + ATable + " SET " + ATable + ".Score=" + QTable + ".Score FROM " + ATable + " INNER JOIN " + QTable + " ON " + ATable + ".ExamID=" + QTable + ".ExamID AND " + ATable + ".QOrder=" + QTable + ".QOrder WHERE " + ATable + ".ExamID=" + ExamID + " AND " + ATable + ".Answer=" + QTable + ".Answer";
            }
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2);
            // update total question type score
            String sql3 = "UPDATE " + stuExamInfoTable + " SET " + QTypeID + "Score=tempTable.Score FROM (SELECT SUM(Score) AS Score FROM " + ATable + " WHERE ExamID=" + ExamID + " AND StudentNum='" + session.getAttribute("StudentNum") + "') AS tempTable WHERE ExamID=" + ExamID + " AND StudentNum='" + session.getAttribute("StudentNum") + "'";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql3);
        }
        // update total exam score other than fill in the blank questions
        String sql4 = "UPDATE " + stuExamInfoTable + " SET Score = MScore+TScore+RScore+NScore WHERE ExamID=" + ExamID + " AND StudentNum='" + session.getAttribute("StudentNum") + "'";
        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql4);
        // if there are no fill in the blank questions, mark as graded
        if ( QTypeOrder.indexOf("F") < 0 ) {
            String sql5 = "UPDATE " + TableNamesConfig.getExamInfoTable() + " SET Graded='Y' WHERE ExamID=" + ExamID;
            ZZSDBService.InsertUpdateDelete(jdbcTemplate,sql5);
        }
        CommonService.removeCookie(request, response,"QTypeOrder");
        CommonService.removeCookie(request, response,"QTypeID");
        CommonService.removeCookie(request, response,"QOrder");
        CommonService.removeCookie(request, response,"QNum");
        CommonService.removeCookie(request, response,"name");
        return "StudentHome.html";
    }
}
