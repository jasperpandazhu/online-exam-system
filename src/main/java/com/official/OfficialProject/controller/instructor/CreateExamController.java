package com.official.OfficialProject.controller.instructor;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.EditQuestionService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import com.official.OfficialProject.service.jqGridService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
public class CreateExamController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getInstructorCourses")
    public List<Map<String, Object>> getInstructorCourses(HttpSession session) {
        String query = "SELECT CourseID, Instructor FROM " + TableNamesConfig.getCourseTable() + " WHERE Instructor = " + session.getAttribute("StaffNum");
        return jdbcTemplate.queryForList(query);
    }

    @PostMapping("/createNewExam")
    public String createNewExam(HttpServletRequest request, HttpSession session) {
        jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getExamInfoTable(), "ExamID", true);
        String examID = ZZSDBService.GetSingleValue(jdbcTemplate, "SELECT TOP(1) ExamID FROM " + TableNamesConfig.getExamInfoTable() + " ORDER BY ExamID DESC");
        String examName = request.getParameter("ExamName");
        String examType = request.getParameter("ExamType");
        String courseID = request.getParameter("Course");
        String startTime = request.getParameter("StartTime");
        String endTime = request.getParameter("EndTime");
        session.setAttribute("ExamID",examID);
        session.setAttribute("CourseID",courseID);
        session.setAttribute("QTypeOrder","");
        return "/CreateExam.html?ExamName="+examName+"&ExamType="+examType+"&Course="+courseID+"&StartTime="+startTime+"&EndTime="+endTime;
    }

    @GetMapping("/getQuestionTypes")
    public String getQuestionTypes(HttpServletResponse response, HttpSession session) throws UnsupportedEncodingException {
        JSONArray jsonArr = new JSONArray();
        String QTypeOrder = session.getAttribute("QTypeOrder").toString();
//        String[] qTypeOrder = QTypeOrder.split("");
        if (QTypeOrder.length() == 0){
            return (jsonArr.toString());
        }
        else {
            String[] qTypeOrder = QTypeOrder.split("");
            ArrayList<String> QTables = Arrays.stream(qTypeOrder)
                    .map(EditQuestionService::getQTypeTable)
                    .collect(Collectors.toCollection(ArrayList::new));
//            qTables.forEach(System.out::println);
//            ArrayList<String> QTables = new ArrayList<>();
//            for ( int k = 0; k < QTypeOrder.length(); k++ ){
//                QTables.add(EditQuestionService.getQTypeTable(QTypeOrder.substring(k,k+1)));
//            }
//            QTables.forEach(System.out::println);
            int i = 0;
//            for( int j = 0; j < QTables.size(); j++ ){
            for ( String qTable : QTables ) {
                String query = "SELECT * FROM (SELECT TOP(1) QTypeID, QOrder FROM " + qTable + " WHERE ExamID=" + session.getAttribute("ExamID") + " ORDER BY QOrder DESC) AS Table1 INNER JOIN " +
                        "(SELECT SUM(Score) AS ScoreSum FROM " + qTable + " WHERE ExamID=" + session.getAttribute("ExamID") + ") AS Table2 ON 1=1";
                SqlRowSet rs = jdbcTemplate.queryForRowSet(query);
                while (rs.next()){
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("QTypeID", rs.getString("QTypeID"));
                    jsonObj.put("QNum", rs.getString("QOrder"));
                    jsonObj.put("ScoreSum", rs.getString("ScoreSum"));
                    // add jsonObject to jsonArray
                    jsonArr.add(i, jsonObj);
                    i++;
                    CommonService.setCookie(response,"total"+rs.getString("QTypeID")+"Questions", URLEncoder.encode(rs.getString("QOrder"), "UTF-8"));
                }
            }
            return (jsonArr.toString());
        }
    }

    @PostMapping("/addDeleteQuestionType")
    public void addDeleteQuestionType(HttpSession session,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                  @RequestParam(value = "QNum") int QNum,
                                  @RequestParam(value = "QTypeID") String QTypeID,
                                  @RequestParam(value = "oper") String oper) {
        String QTypeOrder = session.getAttribute("QTypeOrder").toString();
        String Table = EditQuestionService.getQTypeTable(QTypeID);
        String sql;
        if (oper.equals("add")) {
            for (int i = 1; i <= QNum; i++) {
                sql = "INSERT INTO " + Table + " (ExamID, StaffNum, QTypeID, QOrder) VALUES(" + session.getAttribute("ExamID") + "," + session.getAttribute("StaffNum") + ",@QTypeID," + i + ")";
                ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql, CommonService.getParamsMap(request));
            }
            QTypeOrder += QTypeID;
        }
        else { //oper.equals("delete")
            sql = "DELETE FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QTypeID=@QTypeID";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql, CommonService.getParamsMap(request));
            CommonService.removeCookie(request,response,"total"+QTypeID+"Questions");
            QTypeOrder = QTypeOrder.replace(QTypeID,"");
        }
        // update QTypeOrder
        session.setAttribute("QTypeOrder",QTypeOrder);
        String sqlUpdateQTypeOrder = "UPDATE " + TableNamesConfig.getExamInfoTable() + " SET QTypeOrder='"+QTypeOrder + "' WHERE ExamID=" + session.getAttribute("ExamID");
        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlUpdateQTypeOrder);
    }

    @GetMapping("/getQuestionPage")
    public String getQuestionPage(@RequestParam(value = "QTypeID") String QTypeID,
                                  @RequestParam(value = "QNum") String QNum,
                                  HttpServletResponse response){
        CommonService.setCookie(response,"QTypeID", QTypeID);
        CommonService.setCookie(response,"QNum", QNum);
        String editPageUrl ="";
        switch(QTypeID){
            case "M":
                editPageUrl = "/EditMCQuestion.html";
                break;
            case "T":
                editPageUrl = "/EditTFQuestion.html";
                break;
            case "R":
                editPageUrl = "/EditMRQuestion.html";
                break;
            case "F":
                editPageUrl = "/EditFBQuestion.html";
                break;
            case "N":
                editPageUrl = "/EditNQuestion.html";
                break;
        }
        return editPageUrl;
    }

    @GetMapping("/loadQuestion")
    public List<Map<String, Object>> loadQuestion(HttpServletRequest request, HttpSession session){
        String QTypeID = CommonService.getCookieValue(request, "QTypeID");
        String QNum = CommonService.getCookieValue(request, "QNum");
        String Table = EditQuestionService.getQTypeTable(QTypeID);
        String query = "SELECT * FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QTypeID='" + QTypeID + "' AND QOrder=" + CommonService.stringToInt(QNum);
        return jdbcTemplate.queryForList(query);
    }

    @PostMapping("/editQuestion")
    public void editQuestion(HttpServletRequest request, HttpSession session,
                             @RequestParam(value = "QPool") String QPool) throws UnsupportedEncodingException {
        String QTypeID = URLDecoder.decode(CommonService.getCookieValue(request, "QTypeID"),"UTF-8");
        String QNum = URLDecoder.decode(CommonService.getCookieValue(request, "QNum"),"UTF-8");
        String Table = EditQuestionService.getQTypeTable(QTypeID);
        String PoolTable = EditQuestionService.getPoolTable(QTypeID);
//        StringBuilder sqlEdit = new StringBuilder("UPDATE " + Table + " SET Difficulty=@Difficulty,Score=@Score,Question=@Question");
        String sqlInit = "UPDATE " + Table + " SET Difficulty=@Difficulty,Score=@Score,Question=@Question";
        SqlRowSet rs = ZZSDBService.getSqlRowSet(jdbcTemplate, "SELECT TOP(1) * FROM " + Table);
        SqlRowSetMetaData metaData = rs.getMetaData();
        String sqlEdit = IntStream.range(9,metaData.getColumnCount())
                .boxed()
                .map(x->"," + metaData.getColumnName(x) + "=@" + metaData.getColumnName(x))
                .reduce(sqlInit, String::concat);
        sqlEdit += ",QPool=@QPool WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder=" + QNum;
//        for (int j = 9; j < metaData.getColumnCount(); j++){
//            sqlEdit.append("," + metaData.getColumnName(j) + "=@" + metaData.getColumnName(j));
//        }
//        sqlEdit.append(",QPool=@QPool WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder=" + QNum);
//        System.out.println(sqlEdit.toString());
//        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlEdit.toString(), CommonService.getParamsMap(request));
        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlEdit, CommonService.getParamsMap(request));

        // add or remove question from pool
        if (QPool.equals("true")){
            String query = "SELECT * FROM " + PoolTable + " WHERE Question=@Question AND CourseID='" + session.getAttribute("CourseID") + "' AND StaffNum=" + session.getAttribute("StaffNum");
            SqlRowSet rsPool = ZZSDBService.getSqlRowSet(jdbcTemplate, query, CommonService.getParamsMap(request));
            // if question is not in pool, add to pool
            if( !rsPool.next() ){
                String insertPoolInit = "INSERT INTO " + PoolTable + " VALUES('" + session.getAttribute("CourseID") + "'," + session.getAttribute("StaffNum") + ",'" + QTypeID + "',@Difficulty,@Score,@Question";
                String sqlInsertToPool = IntStream.range(9,metaData.getColumnCount())
                        .boxed()
                        .map(x->",@" + metaData.getColumnName(x))
                        .reduce(insertPoolInit, String::concat);
                sqlInsertToPool += ")";
                ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlInsertToPool, CommonService.getParamsMap(request));
//                StringBuilder sqlInsertToPool = new StringBuilder("INSERT INTO " + PoolTable + " VALUES('" + session.getAttribute("CourseID") + "'," + session.getAttribute("StaffNum") + ",'" + QTypeID + "',@Difficulty,@Score,@Question");
//                for (int k = 9; k < metaData.getColumnCount(); k++) {
//                    sqlInsertToPool.append(",@" + metaData.getColumnName(k));
//                }
//                sqlInsertToPool.append(")");
//                ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlInsertToPool.toString(), CommonService.getParamsMap(request));
            }
            // else question is already in pool, so update the question
            else {
                String updatePoolInit = "UPDATE " + PoolTable + " SET Difficulty=@Difficulty,Score=@Score";
                String sqlUpdateToPool = IntStream.range(9,metaData.getColumnCount())
                        .boxed()
                        .map(x->"," + metaData.getColumnName(x) + "=@" + metaData.getColumnName(x))
                        .reduce(updatePoolInit, String::concat);
                sqlUpdateToPool += " WHERE CourseID='" + session.getAttribute("CourseID") + "' AND StaffNum=" + session.getAttribute("StaffNum") + " AND QTypeID='" + QTypeID + "' AND Question=@Question";
                ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlUpdateToPool, CommonService.getParamsMap(request));
//                StringBuilder sqlUpdateToPool = new StringBuilder("UPDATE " + PoolTable + " SET Difficulty=@Difficulty,Score=@Score");
//                for (int k = 9; k < metaData.getColumnCount(); k++) {
//                    sqlUpdateToPool.append("," + metaData.getColumnName(k) + "=@" + metaData.getColumnName(k));
//                }
//                sqlUpdateToPool.append(" WHERE CourseID='" + session.getAttribute("CourseID") + "' AND StaffNum=" + session.getAttribute("StaffNum") + " AND QTypeID='" + QTypeID + "' AND Question=@Question");
//                ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlUpdateToPool.toString(), CommonService.getParamsMap(request));
            }
        }
        // QPool = false so delete from pool
        else {
            StringBuilder sqlDeleteFromPool = new StringBuilder(" DELETE FROM " + PoolTable + " WHERE CourseID='" + session.getAttribute("CourseID") + "' AND StaffNum=" + session.getAttribute("StaffNum") + " AND Question=@Question");
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlDeleteFromPool.toString(), CommonService.getParamsMap(request));
        }
    }

    @PostMapping("/insertDeleteQuestion")
    public void insertDeleteQuestion(HttpServletRequest request,
                               HttpServletResponse response,
                               HttpSession session,
                               @RequestParam(value = "oper") String oper) throws UnsupportedEncodingException {
        String QTypeID = URLDecoder.decode(CommonService.getCookieValue(request, "QTypeID"),"UTF-8");
        String QNum = URLDecoder.decode(CommonService.getCookieValue(request, "QNum"),"UTF-8");
        String Table = EditQuestionService.getQTypeTable(QTypeID);
        if (oper.equals("insert")){
            int newQNum = CommonService.stringToInt(QNum)+1;
            String sql = "UPDATE " + Table + " SET QOrder = QOrder+1 WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder >" + QNum;
            String sql2 = "INSERT INTO " + Table + " (ExamID, StaffNum, QTypeID, QOrder) VALUES(" + session.getAttribute("ExamID") + ", " + session.getAttribute("StaffNum") + ", '" + QTypeID + "', " + newQNum + ")";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql);
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2);
            Cookie[] cookies = request.getCookies();
            for( Cookie cookie : cookies ){
                if (cookie.getName().equals("total"+QTypeID+"Questions")){
                    cookie.setValue(String.valueOf(CommonService.stringToInt(cookie.getValue())+1));
                    response.addCookie(cookie);
                }
            }
        }
        else { //oper = delete
            String sql = "DELETE FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder=" + QNum;
            String sql2 = "UPDATE " + Table + " SET QOrder = QOrder-1 WHERE ExamID=" + session.getAttribute("ExamID") + " AND QOrder >=" + QNum;
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql);
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2);
            Cookie[] cookies = request.getCookies();
            for( Cookie cookie : cookies ){
                if (cookie.getName().equals("total"+QTypeID+"Questions")){
                    int totalQTypeNum = CommonService.stringToInt(cookie.getValue())-1;
                    if ( totalQTypeNum == 0 ){
                        String QTypeOrder = (String) session.getAttribute("QTypeOrder");
                        QTypeOrder = QTypeOrder.replace(QTypeID,"");
                        session.setAttribute("QTypeOrder",QTypeOrder);
                        String sqlUpdateQTypeOrder = "UPDATE " + TableNamesConfig.getExamInfoTable() + " SET QTypeOrder='"+QTypeOrder + "' WHERE ExamID=" + session.getAttribute("ExamID");
                        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlUpdateQTypeOrder);
                        CommonService.removeCookie(request,response,"total"+QTypeID+"Questions");
                    }
                    else {
                        cookie.setValue(String.valueOf(CommonService.stringToInt(cookie.getValue())-1));
                        response.addCookie(cookie);
                    }
                }
            }
        }
    }

    @GetMapping("/addFromQPool")
    public String addFromQPool(@RequestParam(value = "QTypeID") String QTypeID) {
        return "/AddFromQPool.html?QTypeID="+QTypeID;
    }

    @GetMapping("/getPoolQuestions")
    public List<Map<String, Object>> getPoolQuestions(HttpServletRequest request,
                                                      HttpSession session,
                                                      @RequestParam(value = "QTypeID") String QTypeID) {
        String Table = EditQuestionService.getPoolTable(QTypeID);
        String query = "SELECT * FROM " + Table + " WHERE CourseID='" + session.getAttribute("CourseID") + "' AND StaffNum=" + session.getAttribute("StaffNum");
        return ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
    }

    @PostMapping("/addPoolQuestions")
    public String addPoolQuestions(HttpSession session,
                                   @RequestParam(value = "QTypeID") String QTypeID,
                                   @RequestParam(value = "selected") String selected){
        // change the selected IDs from string into an array
        String[] selectedArr = selected.split(",");
        for (int i = 1; i <= selectedArr.length; i++) {
            String query = "SELECT * FROM " + EditQuestionService.getPoolTable(QTypeID) + " WHERE id=" + selectedArr[i-1];
            SqlRowSet rs = ZZSDBService.getSqlRowSet(jdbcTemplate, query);
            SqlRowSetMetaData metaData = rs.getMetaData();
            StringBuilder sql = new StringBuilder("INSERT INTO " + EditQuestionService.getQTypeTable(QTypeID) + " (ExamID,StaffNum,QTypeID,QOrder,");
            for (int j = 5; j <= metaData.getColumnCount(); j++){
                sql.append(metaData.getColumnName(j) + ",");
            }
            sql.append("QPool) VALUES(" + session.getAttribute("ExamID") + "," + session.getAttribute("StaffNum") + ",'" + QTypeID + "'," + i + ",");
            while (rs.next()){
                for (int j = 5; j <= metaData.getColumnCount(); j++){
                    String tempStr = rs.getString(j).replaceAll("'","''");
                    sql.append("'" + tempStr + "',");
                }
            }
            sql.append("1)");
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql.toString());
        }
        // update QTypeOrder
        String QTypeOrder = session.getAttribute("QTypeOrder").toString();
        QTypeOrder += QTypeID;
        session.setAttribute("QTypeOrder",QTypeOrder);
        String sqlUpdateQTypeOrder = "UPDATE " + TableNamesConfig.getExamInfoTable() + " SET QTypeOrder='"+QTypeOrder + "' WHERE ExamID=" + session.getAttribute("ExamID");
        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sqlUpdateQTypeOrder);
        return "/ExamLayout.html";
    }

    @PostMapping("/submitExam")
    public String submitExam(HttpSession session,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam(value = "fullScore") String fullScore){
        // check if there are any questions the instructor forgot to set
        String QTypeOrder = (String) session.getAttribute("QTypeOrder");
        if (QTypeOrder.equals("")){
            return "N/A";
        }
        for ( int i=0; i <QTypeOrder.length(); i++ ){
            String QTypeID = QTypeOrder.substring(i,i+1);
            String Table = EditQuestionService.getQTypeTable(QTypeID);
            String query = "SELECT Question,QOrder FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID");
            SqlRowSet rs = ZZSDBService.getSqlRowSet(jdbcTemplate, query);
            while( rs.next() ){
                Optional<Object> question = Optional.ofNullable(rs.getString("Question"));
                if (!question.isPresent()){
                    return "Error:" +QTypeID+ rs.getString("QOrder");
                }
                else {
                    if (question.get().equals("")){
                        return "Error:" +QTypeID+ rs.getString("QOrder");
                    }
                }
            }
        }
        // update ExamQTypeInfo table
        String ExamID = (String)session.getAttribute("ExamID");
        for ( int i=0; i <QTypeOrder.length(); i++ ){
            String QTypeID = QTypeOrder.substring(i,i+1);
            String QNum = CommonService.getCookieValue(request, "total"+QTypeID+"Questions");
            String scoreSumSql = "SELECT SUM(Score) FROM " + EditQuestionService.getQTypeTable(QTypeID) + " WHERE ExamID=" + ExamID;
            String Score = ZZSDBService.GetSingleValue(jdbcTemplate,scoreSumSql);
            String sql1 = "INSERT INTO " + TableNamesConfig.getExamQTypeInfoTable() + " VALUES('" + ExamID + "','" + QTypeID + "'," + QNum + "," + Score + ",'" + session.getAttribute("StaffNum") + "')";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate,sql1);
            CommonService.removeCookie(request,response,"total"+QTypeID+"Questions");
        }
        String sql2 = "UPDATE " + TableNamesConfig.getExamInfoTable() + " SET Submitted='Y',Graded='N', Score=" + CommonService.stringToInt(fullScore) + " WHERE ExamID=" + ExamID;
        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2);
        CommonService.removeCookie(request, response,"QTypeID");
        CommonService.removeCookie(request, response,"QNum");
        return "/InstructorHome.html";
    }
}
