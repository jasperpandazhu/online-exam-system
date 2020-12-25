package com.official.OfficialProject.controller.instructor;

import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.EditQuestionService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class QuestionPoolController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getMyPoolQuestions")
    public List<Map<String, Object>> getPoolQuestions(HttpSession session,
                                                      @RequestParam(value = "QTypeID") String QTypeID,
                                                      @RequestParam(value = "CourseID") String CourseID) {
        String Table = EditQuestionService.getPoolTable(QTypeID);
        String query = "SELECT * FROM " + Table + " WHERE CourseID='" + CourseID + "' AND StaffNum=" + session.getAttribute("StaffNum");
        return jdbcTemplate.queryForList(query);
    }

    @GetMapping("/loadPoolQuestion")
    public List<Map<String, Object>> loadPoolQuestion(@RequestParam(value = "QTypeID") String QTypeID,
                                                      @RequestParam(value = "CourseID") String CourseID,
                                                      @RequestParam(value = "id") String id){
        String Table = EditQuestionService.getPoolTable(QTypeID);
        String query = "SELECT * FROM " + Table + " WHERE CourseID='" + CourseID + "' AND QTypeID='" + QTypeID + "' AND id=" + id;
        return jdbcTemplate.queryForList(query);
    }

    @PostMapping("/editPoolQuestion")
    public String editPoolQuestion(HttpServletRequest request,
                                   HttpSession session,
                                   @RequestParam(value = "QTypeID") String QTypeID,
                                   @RequestParam(value = "CourseID") String CourseID,
                                   @RequestParam(value = "id") String id){
        String Table = EditQuestionService.getPoolTable(QTypeID);
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT TOP (1) * FROM " + Table);
        SqlRowSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder sql = new StringBuilder();
        if (id.equals("0")) { // add
            sql.append("INSERT INTO " + Table + " VALUES(@CourseID," + session.getAttribute("StaffNum") + ",@QTypeID,@");
            for (int i = 5; i < columnCount; i++ ) {
                sql.append(metaData.getColumnName(i) + ",@");
            }
            sql.append(metaData.getColumnName(columnCount) + ")");
        }
        else { //edit
            sql.append("UPDATE " + Table + " SET ");
            for (int i = 5; i < columnCount; i++ ) {
                sql.append(metaData.getColumnName(i) + "=@" + metaData.getColumnName(i) + ",");
            }
            sql.append(" " + metaData.getColumnName(columnCount) + "=@" + metaData.getColumnName(columnCount) + " WHERE id=@id");

        }
        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql.toString(), CommonService.getParamsMap(request));
        return "/MyQuestionPoolGrid.html?CourseID="+CourseID+"&QTypeID="+QTypeID;
    }

    @PostMapping("/deletePoolQuestion")
    public void deletePoolQuestion(HttpServletRequest request,
                                   @RequestParam(value = "QTypeID") String QTypeID){
        String Table = EditQuestionService.getPoolTable(QTypeID);
        String sql = "DELETE FROM " + Table + " WHERE id=@id";
        ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql, CommonService.getParamsMap(request));
    }
}
