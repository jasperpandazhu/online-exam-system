package com.official.OfficialProject.controller.admin.system;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import com.official.OfficialProject.service.jqGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class SchoolController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getUniversities")
    public List<Map<String, Object>> getUniversities() {
        return jdbcTemplate.queryForList("SELECT University FROM " + TableNamesConfig.getUniversityTable());
    }

    @GetMapping("/getFaculties")
    public List<Map<String, Object>> getFaculties(HttpServletRequest request){
        String UTable = TableNamesConfig.getUniversityTable();
        String FTable = TableNamesConfig.getFacultyTable();
        String query = "SELECT Faculty FROM " + FTable + " LEFT JOIN " + UTable + " ON " + FTable +".Uid = " + UTable + ".Uid WHERE University = @university";
        List<Map<String, Object>> list = ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
        return list;
    }

    @GetMapping("/getMajors")
    public List<Map<String, Object>> getMajors(HttpServletRequest request){
        String UTable = TableNamesConfig.getUniversityTable();
        String FTable = TableNamesConfig.getFacultyTable();
        String MTable = TableNamesConfig.getMajorTable();
        String query = "SELECT DISTINCT Major FROM " + MTable + " LEFT JOIN " + FTable + " ON " + MTable +".Fid = " + FTable + ".Fid LEFT JOIN " + UTable + " ON " + MTable +".Uid = " + UTable + ".Uid WHERE Faculty = @faculty AND University = @university";
        List<Map<String, Object>> list = ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
        return list;
    }

    @GetMapping("/getUniversity")
    public List<Map<String, Object>> getUniversity() {
        return jqGridService.getFullTable(jdbcTemplate, TableNamesConfig.getUniversityTable());
    }

    @PostMapping("/editUniversity")
    public int editUniversity(HttpServletRequest request,
                              @RequestParam(value = "oper") String oper) {
        if (oper.equals("edit")) {
            String sql1 =  "UPDATE " + TableNamesConfig.getUniversityTable() + " SET Uid = @Uid, University = @University WHERE Uid = @id";
            String sql2 =  "UPDATE " + TableNamesConfig.getFacultyTable() + " SET Uid = @Uid WHERE Uid = @id";
            String sql3 =  "UPDATE " + TableNamesConfig.getMajorTable() + " SET Uid = @Uid WHERE Uid = @id";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql1, CommonService.getParamsMap(request));
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2, CommonService.getParamsMap(request));
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql3, CommonService.getParamsMap(request));
            return 1;
        }
        else if (oper.equals("add")){
            String query = "INSERT INTO " + TableNamesConfig.getUniversityTable() + " VALUES(@Uid, @University)";
            return ZZSDBService.InsertUpdateDelete(jdbcTemplate, query, CommonService.getParamsMap(request));
        }
        else{
            String sql1 = "DELETE FROM " + TableNamesConfig.getUniversityTable() + " WHERE Uid = @id";
            String sql2 = "DELETE FROM " + TableNamesConfig.getFacultyTable() + " WHERE Uid = @id";
            String sql3 = "DELETE FROM " + TableNamesConfig.getMajorTable() + " WHERE Uid = @id";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql1, CommonService.getParamsMap(request));
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2, CommonService.getParamsMap(request));
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql3, CommonService.getParamsMap(request));
            return 1;
        }
    }

//    @GetMapping("/getFaculty")
//    public String getFaculty(@RequestParam(value = "Uid") String Uid) throws SQLException {
//        JSONArray jsonArr = new JSONArray();
//        Connection conn = dataSource.getConnection();
//        String query = "SELECT * FROM " + TableNamesConfig.getFacultyTable() + " WHERE Uid = ?";
//        PreparedStatement preStmt = conn.prepareStatement(query);
//        preStmt.setString(1, Uid);
//        ResultSet rs = preStmt.executeQuery();
//        // Extract data from result set
//        int i = 0;
//        while (rs.next()){
//            int uid = rs.getInt("Uid");
//            int fid = rs.getInt("Fid");
//            String faculty = rs.getString("Faculty");
//            // new jsonObject for each row in database
//            JSONObject jsonObj = new JSONObject();
//            jsonObj.put("Uid", uid);
//            jsonObj.put("Fid", fid );
//            jsonObj.put("Faculty", faculty );
//            // add jsonObject to jsonArray
//            jsonArr.add(i, jsonObj);
//            i++;
//        }
//        // Clean-up environment
//        rs.close();
//        preStmt.close();
//        conn.close();
//        // return jsonArray
//        return (jsonArr.toString());
//    }

    @GetMapping("/getFaculty")
    public List<Map<String, Object>> getFaculty(HttpServletRequest request) {
        String query = "SELECT * FROM " + TableNamesConfig.getFacultyTable() + " WHERE Uid = @Uid";
        return ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
    }

    @PostMapping("/editFaculty")
    public int editFaculty(HttpServletRequest request,
                           @RequestParam(value = "oper") String oper) {
        if (oper.equals("edit")) {
            String sql1 =  "UPDATE " + TableNamesConfig.getFacultyTable() + " SET Fid = @Fid, Faculty = @Faculty WHERE Uid = @Uid AND Fid = @id";
            String sql2 =  "UPDATE " + TableNamesConfig.getMajorTable() + " SET Fid = @Fid WHERE Uid = @Uid AND Fid = @id";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql1, CommonService.getParamsMap(request));
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2, CommonService.getParamsMap(request));
            return 1;
        }
        else if (oper.equals("add")){
            String query = "INSERT INTO " + TableNamesConfig.getFacultyTable() + " VALUES(@Uid, @Fid, @Faculty)";
            return ZZSDBService.InsertUpdateDelete(jdbcTemplate, query, CommonService.getParamsMap(request));
        }
        else{
            String sql1 = "DELETE FROM " + TableNamesConfig.getFacultyTable() + " WHERE Uid = @Uid AND Fid = @id";
            String sql2 = "DELETE FROM " + TableNamesConfig.getMajorTable() + " WHERE Uid = @Uid AND Fid = @id";
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql1, CommonService.getParamsMap(request));
            ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql2, CommonService.getParamsMap(request));
            return 1;
        }
    }

//    @GetMapping("/getMajor")
//    public String getMajor(@RequestParam(value = "Uid") String Uid,
//                           @RequestParam(value = "Fid") String Fid) throws SQLException {
//        JSONArray jsonArr = new JSONArray();
//        Connection conn = dataSource.getConnection();
//        String query = "SELECT * FROM " + TableNamesConfig.getMajorTable() + " WHERE Uid = ? AND Fid = ?";
//        PreparedStatement preStmt = conn.prepareStatement(query);
//        preStmt.setInt(1, CommonService.stringToInt(Uid));
//        preStmt.setInt(2, CommonService.stringToInt(Fid));
//        ResultSet rs = preStmt.executeQuery();
//        // Extract data from result set
//        int i = 0;
//        while (rs.next()){
//            int uid = rs.getInt("Uid");
//            int fid = rs.getInt("Fid");
//            int mid = rs.getInt("Mid");
//            String major = rs.getString("Major");
//            // new jsonObject for each row in database
//            JSONObject jsonObj = new JSONObject();
//            jsonObj.put("Uid", uid);
//            jsonObj.put("Fid", fid );
//            jsonObj.put("Mid", mid);
//            jsonObj.put("Major", major );
//            // add jsonObject to jsonArray
//            jsonArr.add(i, jsonObj);
//            i++;
//        }
//        // Clean-up environment
//        rs.close();
//        preStmt.close();
//        conn.close();
//        // return jsonArray
//        return (jsonArr.toString());
//    }

    @GetMapping("/getMajor")
    public List<Map<String, Object>> getMajor(HttpServletRequest request) {
        String query = "SELECT * FROM " + TableNamesConfig.getMajorTable() + " WHERE Uid = @Uid AND Fid = @Fid";
        return ZZSDBService.getQueryListMap(jdbcTemplate, query, CommonService.getParamsMap(request));
    }

    @PostMapping("/editMajor")
    public int editMajor(HttpServletRequest request,
                           @RequestParam(value = "oper") String oper) {
        String sql;
        if (oper.equals("edit")) {
            sql =  "UPDATE " + TableNamesConfig.getMajorTable() + " SET Mid = @Mid, Major = @Major WHERE Uid = @Uid AND Fid = @Fid AND Mid=@id";
        }
        else if (oper.equals("add")){
            sql = "INSERT INTO " + TableNamesConfig.getMajorTable() + " VALUES(@Uid, @Fid, @Mid, @Major)";
        }
        else{
            sql = "DELETE FROM " + TableNamesConfig.getMajorTable() + " WHERE Uid = @Uid AND Fid = @Fid AND Mid=@id";
        }
        return ZZSDBService.InsertUpdateDelete(jdbcTemplate, sql, CommonService.getParamsMap(request));

    }
}
