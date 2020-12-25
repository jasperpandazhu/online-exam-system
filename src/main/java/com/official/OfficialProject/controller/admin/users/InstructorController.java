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
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
public class InstructorController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${UpLoad.UpLoadPath}")
    private String uploadPath;

    @GetMapping("/getInstructor")
    public List<Map<String, Object>> getInstructor() {
        return jqGridService.getFullTable(jdbcTemplate, TableNamesConfig.getInstructorTable());
    }

    @PostMapping("/editInstructor")
    public int editInstructor(HttpServletRequest request) {
        return jqGridService.editGrid(jdbcTemplate, request, TableNamesConfig.getInstructorTable(), "id", true);
    }

    @PostMapping("/importInstructor")
    public void importInstructor(@RequestParam("excelFile") MultipartFile[] file, HttpServletResponse response, HttpSession session) throws IOException {

        if(file.length <= 0){
            throw new FileNotFoundException();
        }
        else{
            String[] tableColumns = Stream.of("StaffNum","FirstName","LastName","Gender","University","Faculty","Phone","Email","Password")
                    .toArray(String[]::new);
            String[] columnTypes = Stream.of("STRING","STRING","STRING","STRING","STRING","STRING","STRING","STRING","STRING")
                    .toArray(String[]::new);
            jqGridService.excelToSqlInsert(file[0], uploadPath, jdbcTemplate, TableNamesConfig.getInstructorTable(), tableColumns, columnTypes, session, response, "/InstructorGrid.html");

//            CommonService.writeFileToTemp(file[0], uploadPath);
//            String downloadedPath = uploadPath + "\\" + file[0].getOriginalFilename();
//            String[] tableColumns = new String[]{"StaffNum","FirstName","LastName","Gender","University","Faculty","Phone","Email","Password"};
//            String[] columnTypes = new String[]{"STRING","STRING","STRING","STRING","STRING","STRING","STRING","STRING","STRING"};
//            try {
//                String[] sqlStrings = MSExcelService.LJZgetExcelToMSSqlSqlArray(downloadedPath, TableNamesConfig.getInstructorTable(), tableColumns, columnTypes, tableColumns);
//                ZZSDBService.InsertUpdateDeleteForBatch(jdbcTemplate, sqlStrings);
//            }catch (InvalidFormatException | FieldMismatchException fmex){
//                session.setAttribute("error","fieldMismatch");
//            }
//            finally {
//                response.sendRedirect("/InstructorGrid.html");
//            }

        }
    }

}