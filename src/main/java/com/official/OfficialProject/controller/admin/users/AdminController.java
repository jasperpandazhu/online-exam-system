package com.official.OfficialProject.controller.admin.users;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.jqGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/getAdmin")
    public List<Map<String, Object>> getAdmin() {
        return jqGridService.getFullTable(jdbcTemplate, TableNamesConfig.getAdminTable());
    }

    @PostMapping("/editAdmin")
    public int editAdmin(HttpServletRequest request) {
//        String query;
//        if (oper.equals("edit")) {
//            query = "UPDATE " + TableNamesConfig.getAdminTable() + " SET FirstName = @FirstName, LastName = @LastName, Username = @Username, Password = @Password, UserLevel = @UserLevel WHERE id = @id";
//        }
//        else if (oper.equals("add")){
//            query = "INSERT INTO " + TableNamesConfig.getAdminTable() + " VALUES(@FirstName, @LastName, @Username, @Password, @UserLevel)";
//        }
//        else{
//            query = "DELETE FROM " + TableNamesConfig.getAdminTable() + " WHERE id = @id";
//        }
//        return ZZSDBService.InsertUpdateDelete(jdbcTemplate, query, CommonService.getParamsMap(request));
    return jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getAdminTable(), "id", true);
    }

}
