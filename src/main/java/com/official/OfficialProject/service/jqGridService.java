package com.official.OfficialProject.service;

import com.official.OfficialProject.exception.FieldMismatchException;
import com.official.OfficialProject.service.dbhelper.ZZSDBService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class jqGridService {
    public static void excelToSqlInsert(MultipartFile file, String uploadPath, JdbcTemplate jdbcTemplate, String tableName, String[] tableColumns, String[] columnTypes, HttpSession session, HttpServletResponse response, String redirectURL) throws IOException {
        // write file to temp
        String tempPath = uploadPath + "\\" + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(tempPath);
        BufferedInputStream bis = new BufferedInputStream(inputStream); //wrapped
        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        int len;
        while( (len = bis.read()) != -1 ){ // reads 8192 bytes to buffer (default)
            bos.write(len); // writes 8192 bytes from buffer
        }
        bis.close();
        bos.close();

        // convert excel to sql and insert
        try {
            String[] sqlStrings = MSExcelService.LJZgetExcelToMSSqlSqlArray(tempPath, tableName, tableColumns, columnTypes, tableColumns);
            ZZSDBService.InsertUpdateDeleteForBatch(jdbcTemplate, sqlStrings);
        }catch (InvalidFormatException | FieldMismatchException fmex){
            session.setAttribute("error","fieldMismatch");
        }
        finally {
            response.sendRedirect(redirectURL);
        }
    }

    public static List<Map<String, Object>> getFullTable(JdbcTemplate jdbcTemplate, String tableName){
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
    }

    public static int editGrid(JdbcTemplate jdbcTemplate, HttpServletRequest request, String tableName, String idColumn, Boolean selfIncrement){
        String oper = request.getParameter("oper");
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT TOP (1) * FROM " + tableName);
        SqlRowSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder query = new StringBuilder();
        if (oper.equals("edit")) {
            query.append("UPDATE " + tableName + " SET ");
            if (selfIncrement) {
                for (int i = 2; i < columnCount; i++ ) {
                    query.append(metaData.getColumnName(i) + "=@" + metaData.getColumnName(i) + ",");
                }
            }
            else {
                for (int i = 1; i < columnCount; i++ ) {
                    query.append(metaData.getColumnName(i) + "=@" + metaData.getColumnName(i) + ",");
                }
            }
            query.append(" " + metaData.getColumnName(columnCount) + "=@" + metaData.getColumnName(columnCount) + " WHERE " + idColumn + "=@id");
        }
        else if (oper.equals("add")){
            query.append("INSERT INTO " + tableName + " VALUES(@");
            if (selfIncrement) {
                for (int i = 2; i < columnCount; i++ ) {
                    query.append(metaData.getColumnName(i) + ",@");
                }
            }
            else {
                for (int i = 1; i < columnCount; i++ ) {
                    query.append(metaData.getColumnName(i) + ",@");
                }
            }
            query.append(metaData.getColumnName(columnCount) + ")");
        }
        else {
            query.append("DELETE FROM " + tableName + " WHERE " + idColumn + " = @id");
        }
        return ZZSDBService.InsertUpdateDelete(jdbcTemplate, query.toString(), CommonService.getParamsMap(request));
    }
}
