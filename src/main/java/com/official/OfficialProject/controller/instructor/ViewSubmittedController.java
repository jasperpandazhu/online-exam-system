package com.official.OfficialProject.controller.instructor;

import com.official.OfficialProject.service.EditQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class ViewSubmittedController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/viewQuestions")
    public List<Map<String, Object>> loadQuestion(HttpSession session,
                                                  @RequestParam(value = "QTypeID") String QTypeID){
        String Table = EditQuestionService.getQTypeTable(QTypeID);
        String query = "SELECT * FROM " + Table + " WHERE ExamID=" + session.getAttribute("ExamID");
        return jdbcTemplate.queryForList(query);
    }
}
