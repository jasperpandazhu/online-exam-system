package com.official.OfficialProject.controller.general;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
public class CommonController {
    // not used anymore
    @GetMapping("/getUserName")
    public String getUserName(HttpSession session){
        Optional<Object> name = Optional.ofNullable(session.getAttribute("name"));
        return name.isPresent() ? name.get().toString() : "testing";
    }

    @GetMapping("/checkErrors")
    public String getErrors(HttpSession session){
        Optional<Object> error = Optional.ofNullable(session.getAttribute("error"));
        if (error.isPresent()) {
            session.removeAttribute("error");
            return error.get().toString();
        }
        else {
            return "noErrors";
        }
    }

}
