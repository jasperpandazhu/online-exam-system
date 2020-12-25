package com.official.OfficialProject.service;

import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Service
public class LoginService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public String login(String tableName, String usernameColumn, Map params) {
        String query = "SELECT * FROM " + tableName + " WHERE " + usernameColumn + " = @username AND Password = @password";
        SqlRowSet rowSet = ZZSDBService.getSqlRowSet(jdbcTemplate, query, params);
        if (!rowSet.next()){
            return "error";
        }
        else{
            return rowSet.getString("FirstName");
        }
    }
}
