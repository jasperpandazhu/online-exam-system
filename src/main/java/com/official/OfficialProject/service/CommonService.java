package com.official.OfficialProject.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class CommonService {

    public static int stringToInt(String str) {
        int intToReturn = 0;
        try {
            intToReturn = Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return intToReturn;
    }

    public static Map<String,String> getParamsMap(HttpServletRequest request){
        Enumeration paramNames = request.getParameterNames();
        Map params = new HashMap();
        while(paramNames.hasMoreElements()) {
            String paramKey = (String) paramNames.nextElement();
            String paramValue = request.getParameter(paramKey);
            params.put(paramKey,paramValue);
        }
        return params;
    }

    public static String queryWithParams(Connection conn, String query, String[] params, String[] columnLabels, String[] columnTypes) throws SQLException {
        JSONArray jsonArr = new JSONArray();
        PreparedStatement preStmt = conn.prepareStatement(query);
        for( int i = 0; i < params.length; i++){
            preStmt.setString(i+1, params[i]);
        }
        ResultSet rs = preStmt.executeQuery();
        // Extract data from result set
        int i = 0;
        while (rs.next()){
            // new jsonObject for each row in database
            JSONObject jsonObj = new JSONObject();
            for ( int j = 0; j < columnLabels.length; j++){
                if (columnTypes[j].equals("int")){
                    jsonObj.put(columnLabels[j], rs.getInt(columnLabels[j]));
                }
                else{
                    jsonObj.put(columnLabels[j], rs.getString(columnLabels[j]));
                }
            }
            // add jsonObject to jsonArray
            jsonArr.add(i, jsonObj);
            i++;
        }
        // Clean-up environment
        rs.close();
        preStmt.close();
        conn.close();
        // return jsonArray
        return (jsonArr.toString());
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0|| null == name || name.trim() == "") {
            return null;
        }
        return Stream.of(cookies).filter(c -> c.getName().equals(name)).findAny().orElse(null);
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();
        for( Cookie cookie : cookies ){
            if (cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String name, String value){
        Cookie cookie = new Cookie(name, value);
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        if (null == name) {
            return;
        }
        Cookie cookie = getCookie(request, name);
        if(null != cookie){
            cookie.setPath(request.getContextPath());
            cookie.setValue("");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
