package com.official.OfficialProject.controller.general;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.LoginService;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class ManagementLoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/loginAdmin")
    public String loginAdmin(HttpServletRequest request, HttpServletResponse response, HttpSession session)  {
        String query = "SELECT * FROM " + TableNamesConfig.getAdminTable() + " WHERE Username = @username AND Password = @password";
        SqlRowSet rowSet = ZZSDBService.getSqlRowSet(jdbcTemplate, query, CommonService.getParamsMap(request));
        if (!rowSet.next()){
            return "error";
        }
        else{
            CommonService.setCookie(response,"name",rowSet.getString("FirstName"));
            CommonService.setCookie(response,"userLevel", rowSet.getString("UserLevel"));
            return "/ManagementHome.html";
        }
    }

    @PostMapping("/loginInstructor")
    public String loginInstructor(HttpServletRequest request, HttpServletResponse response, HttpSession session){
        String name = loginService.login(TableNamesConfig.getInstructorTable(), "StaffNum", CommonService.getParamsMap(request));
        if (name.equals("error")){
            return name;
        }
        else{
            session.setAttribute("StaffNum", request.getParameter("username"));
            CommonService.setCookie(response, "name", name);
            return "/InstructorHome.html";
        }
    }

    @GetMapping("/managementLogout")
    public void logout(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException {
        session.removeAttribute("StaffNum");
        CommonService.removeCookie(request,response,"name");
        CommonService.removeCookie(request,response,"userLevel");
        response.sendRedirect("/ManagementLogin.html");
    }

    @GetMapping("/getUserLevel")
    public String getUserLevel(HttpSession session){
        return session.getAttribute("userLevel").toString();
    }

}
