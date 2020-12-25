package com.official.OfficialProject.service;

import com.official.OfficialProject.config.TableNamesConfig;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class EditQuestionService {
    public static String getExamInfo(DataSource dataSource, String examSelect, HttpSession session, String action) throws SQLException {
        String query = "SELECT * FROM " + TableNamesConfig.getExamInfoTable() + " WHERE ExamName = ? AND StaffNum = " + session.getAttribute("StaffNum");
        Connection conn = dataSource.getConnection();
        PreparedStatement preStmt = conn.prepareStatement(query);
        preStmt.setString(1, examSelect);
        ResultSet rs = preStmt.executeQuery();
        String examID;
        String examName="";
        String examType ="";
        String courseID ="";
        String startTime="";
        String endTime="";
        while (rs.next()){
            examID = rs.getString("ExamID");
            examName = rs.getString("ExamName");
            examType = rs.getString("ExamType");
            courseID = rs.getString("Course");
            startTime = rs.getString("StartTime");
            endTime = rs.getString("EndTime");
            session.setAttribute("ExamID",examID);
            session.setAttribute("CourseID",courseID);
            Optional<Object> qTypeOrder = Optional.ofNullable(rs.getString("QTypeOrder"));
            session.setAttribute("QTypeOrder",qTypeOrder.isPresent() ? qTypeOrder.get().toString() : "");
        }
        rs.close();
        preStmt.close();
        conn.close();
        if (action.equals("create")){
            return "/CreateExam.html?ExamName="+examName+"&ExamType="+examType+"&Course="+courseID+"&StartTime="+startTime+"&EndTime="+endTime;
        }
        else { // view exam
            return "/ViewSubmittedExam.html?ExamName="+examName+"&ExamType="+examType+"&Course="+courseID+"&StartTime="+startTime+"&EndTime="+endTime;
        }
    }

    public static String getQTypeTable(String QTypeID){
        switch(QTypeID){
            case "M":
                return TableNamesConfig.getMCQTable();
            case "T":
                return TableNamesConfig.getTFQTable();
            case "R":
                return TableNamesConfig.getMRQTable();
            case "F":
                return TableNamesConfig.getFBQTable();
            case "N":
                return TableNamesConfig.getNQTable();
        }
        return "N/A";
    }

    public static String getPoolTable(String QTypeID){
        switch(QTypeID){
            case "M":
                return TableNamesConfig.getMCQPoolTable();
            case "T":
                return TableNamesConfig.getTFQPoolTable();
            case "R":
                return TableNamesConfig.getMRQPoolTable();
            case "F":
                return TableNamesConfig.getFBQPoolTable();
            case "N":
                return TableNamesConfig.getNQPoolTable();
        }
        return "N/A";
    }

    public static String getAnswerTable(String QTypeID){
        switch(QTypeID){
            case "M":
                return TableNamesConfig.getMCQAnswerTable();
            case "T":
                return TableNamesConfig.getTFQAnswerTable();
            case "R":
                return TableNamesConfig.getMRQAnswerTable();
            case "F":
                return TableNamesConfig.getFBQAnswerTable();
            case "N":
                return TableNamesConfig.getNQAnswerTable();
        }
        return "N/A";
    }


}


