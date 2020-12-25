package com.official.OfficialProject.controller.general;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.CommonService;
import com.official.OfficialProject.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class StudentLoginController {

    @Autowired
    LoginService loginService;

    @PostMapping("/loginStudent")
    public String loginStudent(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String result = loginService.login(TableNamesConfig.getStudentTable(), "StudentNum", CommonService.getParamsMap(request));
        if (result.equals("error")){
            return result;
        }
        else{
            session.setAttribute("StudentNum", request.getParameter("username"));
            CommonService.setCookie(response,"name", result);
            return "/StudentHome.html";
        }
    }

    @GetMapping("/studentLogout")
    public void logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        session.removeAttribute("StudentNum");
        CommonService.removeCookie(request,response,"name");
        response.sendRedirect("/StudentLogin.html");
    }
}
