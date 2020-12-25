package com.official.OfficialProject.controller.admin.users;

import com.official.OfficialProject.config.TableNamesConfig;
import com.official.OfficialProject.service.jqGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
public class StudentController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${UpLoad.UpLoadPath}")
    private String uploadPath;

    @GetMapping("/getStudent")
    public List<Map<String, Object>> getStudent() {
        return jqGridService.getFullTable(jdbcTemplate, TableNamesConfig.getStudentTable());
    }

    @PostMapping("/editStudent")
    public int editStudent(HttpServletRequest request) {
        return jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getStudentTable(), "id", true);
    }

    @PostMapping("/importStudent")
    public void importStudent(@RequestParam("excelFile") MultipartFile[] file, HttpServletResponse response, HttpSession session) throws IOException {

        if(file.length <= 0){
            throw new FileNotFoundException();
        }
        else{
            String[] tableColumns = Stream.of("StudentNum","FirstName","LastName","Gender","University","Faculty","Major","Year","Phone","Email","Password","Courses")
                    .toArray(String[]::new);
            String[] columnTypes = Stream.of("STRING","STRING","STRING","STRING","STRING","STRING","STRING","NUMBER","STRING","STRING","STRING","STRING")
                    .toArray(String[]::new);
            jqGridService.excelToSqlInsert(file[0], uploadPath, jdbcTemplate, TableNamesConfig.getStudentTable(), tableColumns, columnTypes, session, response, "/StudentGrid.html");
        }
    }


}