package com.official.OfficialProject.service;

import com.official.OfficialProject.exception.FieldMismatchException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;

@Service
public class MSExcelService {

    public static String[] getExcelToOracleSqlArray(String file,String tableName,String[] dbFields,String[] dbFieldsType,String[] sCols) throws Exception{
    	File excel = new File(file);
        InputStream fis = new FileInputStream(excel);
        
        Workbook wbook = WorkbookFactory.create(fis); //new XSSFWorkbook(fis);//
        Sheet wsheet = wbook.getSheetAt(0);
        wsheet.setForceFormulaRecalculation(true);

        int rowNum = wsheet.getLastRowNum() + 1;
        int colNum = wsheet.getRow(0).getLastCellNum();
        int[] indexCol = new int[sCols.length]; 
        //Read the headers first. Locate the ones you need
        Row rowHeader = wsheet.getRow(0);
        for (int j = 0; j < colNum; j++) {
        	Cell cell = rowHeader.getCell(j);
            String cellValue = cellToString(cell);
            for (int k=0; k<sCols.length;k++){
            	if(sCols[k].equalsIgnoreCase(cellValue)){
            		indexCol[k] = j;
            		break;
            	}
            }
        }
        String[] sqlList = new String[rowNum - 1];
        String sqlConst = "",valueConst="";//"Insert Into " + tableName + " (";
        for (int i=0;i<dbFields.length - 1;i++){
        	sqlConst += dbFields[i] + ",";
        }
        sqlConst += dbFields[dbFields.length - 1];
        sqlConst = "Insert Into " + tableName + " ("+ sqlConst + ") Values(";
        
        for (int i = 1; i < rowNum; i++) {
        	valueConst = "";
        	Row row = wsheet.getRow(i);
            String[] colValue = new String[sCols.length];
            for (int j=0;j<sCols.length;j++){
            	try{
            		colValue[j] = cellToString(row.getCell(indexCol[j]));
            	}catch(Exception e){
            		colValue[j] = "";
            	}

				if (dbFieldsType[j].equalsIgnoreCase("STRING")){
					valueConst += ("'" + colValue[j]+"',");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("NUMBER")) {
					valueConst += (colValue[j]+",");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("DATE")) {
					valueConst += ("TO_DATE('" + colValue[j]+"','yyyy-mm-dd'),");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("DATETIME")) {
					valueConst += ("TO_DATE('" + colValue[j]+"','yyyy-mm-dd hh24:mi:ss'),");
				}
				else{
					valueConst += ("'" + colValue[j]+"',");
				}

            }
            if (valueConst.charAt(valueConst.length() - 1) == ',') {
            	valueConst = valueConst.substring(0, valueConst.length() - 1);
            }
            sqlList[i - 1] = sqlConst + valueConst+")";
        }
		fis.close();
		try{
			wbook.close();
		}catch(Exception e){wbook = null;}

		return sqlList;
    } 
    
    public static String[] getExcelToOracleSqlArray(String file,String tableName,String[] dbFields,String[] dbFieldsType,String[] sCols,String[] addFeilds,String[] addValues,String[] addTypes) throws Exception{
    	File excel = new File(file);
        FileInputStream fis = new FileInputStream(excel);
        Workbook wbook = WorkbookFactory.create(fis); //new XSSFWorkbook(fis);//
        Sheet wsheet = wbook.getSheetAt(0);
        wsheet.setForceFormulaRecalculation(true);

        int rowNum = wsheet.getLastRowNum() + 1;
        int colNum = wsheet.getRow(0).getLastCellNum();
        int[] indexCol = new int[sCols.length]; 
        //Read the headers first. Locate the ones you need
        Row rowHeader = wsheet.getRow(0);
        for (int j = 0; j < colNum; j++) {
            Cell cell = rowHeader.getCell(j);
            String cellValue = cellToString(cell);
            for (int k=0; k<sCols.length;k++){
            	if(sCols[k].equalsIgnoreCase(cellValue)){
            		indexCol[k] = j;
            		break;
            	}
            }
        }
        String[] sqlList = new String[rowNum - 1];
        String sqlConst = "",valueConst="";//"Insert Into " + tableName + " (";

        for (int i=0;i<dbFields.length - 1;i++){
        	sqlConst += dbFields[i] + ",";
        }
        sqlConst += dbFields[dbFields.length - 1];
        if (addFeilds.length > 0)
        	for (int i = 0;i<addFeilds.length;i++)
        		sqlConst += (","+ addFeilds[i]);
        
        sqlConst = "Insert Into " + tableName + " ("+ sqlConst + ") Values(";
        
        for (int i = 1; i < rowNum; i++) {
        	valueConst = "";
            Row row = wsheet.getRow(i);
            String[] colValue = new String[sCols.length];
            for (int j=0;j<sCols.length;j++){
            	try{
            		colValue[j] = cellToString(row.getCell(indexCol[j]));
            	}catch(Exception e){
            		colValue[j] = "";
            	}

				if (dbFieldsType[j].equalsIgnoreCase("STRING")){
					valueConst += ("'" + colValue[j]+"',");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("NUMBER")) {
					valueConst += (colValue[j]+",");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("DATE")) {
					valueConst += ("TO_DATE('" + colValue[j]+"','yyyy-mm-dd'),");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("DATETIME")) {
					valueConst += ("TO_DATE('" + colValue[j]+"','yyyy-mm-dd hh24:mi:ss'),");
				}
				else{
					valueConst += ("'" + colValue[j]+"',");
				}
            }
            if (valueConst.charAt(valueConst.length() - 1) == ',') {
            	valueConst = valueConst.substring(0, valueConst.length() - 1);
            }
            if (addFeilds.length > 0){
            	for (int j=0;j<addFeilds.length;j++){
            		if (addTypes[j].equalsIgnoreCase("STRING")){
    	            		valueConst += (",'" + addValues[j]+"'");
                	}
                	else if (addTypes[j].equalsIgnoreCase("NUMBER")) {
    	            		valueConst += (","+ addValues[j]);
    				}
                	else if (addTypes[j].equalsIgnoreCase("DATE")) {
    	            		valueConst += (",TO_DATE('" + addValues[j]+"','yyyy-mm-dd')");
    				}
                	else if (addTypes[j].equalsIgnoreCase("DATETIME")) {
                			valueConst += (",TO_DATE('" + addValues[j]+"','yyyy-mm-dd hh24:mi:ss')");
    	        	}
                	else{
    	            		valueConst += (",'" + addValues[j]+"'");
                	}
            	}
            }
            sqlList[i - 1] = sqlConst + valueConst+")";
        }
        fis.close(); 
        try{
        	wbook.close();
        }catch(Exception e){wbook = null;}
		return sqlList;
    }

	public static String[] LJZgetExcelToMSSqlSqlArray(String file,String tableName,String[] dbFields,String[] dbFieldsType,String[] sCols) throws FieldMismatchException, IOException, InvalidFormatException {
		File excel = new File(file);
		InputStream fis = new FileInputStream(excel);

		Workbook wbook = WorkbookFactory.create(fis); //new XSSFWorkbook(fis);//
		Sheet wsheet = wbook.getSheetAt(0);
		wsheet.setForceFormulaRecalculation(true);

		int rowNum = wsheet.getLastRowNum() + 1;
		int colNum = wsheet.getRow(0).getLastCellNum();
		int[] indexCol = new int[sCols.length];
		//Read the headers first. Locate the ones you need
		Row rowHeader = wsheet.getRow(0);
		for (int j = 0; j < colNum; j++) {
			Cell cell = rowHeader.getCell(j);
			String cellValue = cellToString(cell);
			//LJZ added
			if (!cellValue.equals(dbFields[j])){
				throw new FieldMismatchException();
			}
			for (int k=0; k<sCols.length;k++){
				if(sCols[k].equalsIgnoreCase(cellValue)){
					indexCol[k] = j;
					break;
				}
			}
		}
		String[] sqlList = new String[rowNum - 1];
		String sqlConst = "",valueConst="";  //"Insert Into " + tableName + " (";
		for (int i=0;i<dbFields.length - 1;i++){
			sqlConst += dbFields[i] + ",";
		}
		sqlConst += dbFields[dbFields.length - 1];
		sqlConst = "Insert Into " + tableName + " ("+ sqlConst + ") Values(";

		for (int i = 1; i < rowNum; i++) {
			valueConst="";
			Row row = wsheet.getRow(i);
			String[] colValue = new String[sCols.length];
			for (int j=0;j<sCols.length;j++){
				try{
					colValue[j] = cellToString(row.getCell(indexCol[j]));
				}catch(Exception e){
					colValue[j] = "";
				}

				if (dbFieldsType[j].equalsIgnoreCase("STRING")){
					valueConst += ("'" + colValue[j]+"',");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("NUMBER")) {
					valueConst += (colValue[j]+",");
				}
				else{
					valueConst += ("'" + colValue[j]+"',");
				}

			}
			if (valueConst.charAt(valueConst.length() - 1) == ',') {
				valueConst = valueConst.substring(0, valueConst.length() - 1);
			}
			sqlList[i - 1] = sqlConst + valueConst+")";
		}
		fis.close();
		try{
			wbook.close();
		}catch(Exception e){wbook = null;}
		return sqlList;
	}
	public static String[] getExcelToMSSqlSqlArray(String file,String tableName,String[] dbFields,String[] dbFieldsType,String[] sCols) throws Exception{
    	File excel = new File(file);
        InputStream fis = new FileInputStream(excel);
        
        Workbook wbook = WorkbookFactory.create(fis); //new XSSFWorkbook(fis);//
        Sheet wsheet = wbook.getSheetAt(0);
        wsheet.setForceFormulaRecalculation(true);

        int rowNum = wsheet.getLastRowNum() + 1;
        int colNum = wsheet.getRow(0).getLastCellNum();
        int[] indexCol = new int[sCols.length]; 
        //Read the headers first. Locate the ones you need
        Row rowHeader = wsheet.getRow(0);
        for (int j = 0; j < colNum; j++) {
            Cell cell = rowHeader.getCell(j);
            String cellValue = cellToString(cell);
            for (int k=0; k<sCols.length;k++){
            	if(sCols[k].equalsIgnoreCase(cellValue)){
            		indexCol[k] = j;
            		break;
            	}
            }
        }
        String[] sqlList = new String[rowNum - 1];
        String sqlConst = "",valueConst="";  //"Insert Into " + tableName + " (";
        for (int i=0;i<dbFields.length - 1;i++){
        	sqlConst += dbFields[i] + ",";
        }
        sqlConst += dbFields[dbFields.length - 1];
        sqlConst = "Insert Into " + tableName + " ("+ sqlConst + ") Values(";
        
        for (int i = 1; i < rowNum; i++) {
        	valueConst="";
            Row row = wsheet.getRow(i);
            String[] colValue = new String[sCols.length];
            for (int j=0;j<sCols.length;j++){
            	try{
            		colValue[j] = cellToString(row.getCell(indexCol[j]));
            	}catch(Exception e){
            		colValue[j] = "";
            	}

				if (dbFieldsType[j].equalsIgnoreCase("STRING")){
					valueConst += ("'" + colValue[j]+"',");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("NUMBER")) {
					valueConst += (colValue[j]+",");
				}
				else{
					valueConst += ("'" + colValue[j]+"',");
				}

            }
            if (valueConst.charAt(valueConst.length() - 1) == ',') {
            	valueConst = valueConst.substring(0, valueConst.length() - 1);
            }
            sqlList[i - 1] = sqlConst + valueConst+")";
        }
        fis.close();
        try{
        	wbook.close();
        }catch(Exception e){wbook = null;}
		return sqlList;
    } 
   
    public static String[] getExcelToMSSqlSqlArray(String file,String tableName,String[] dbFields,String[] dbFieldsType,String[] sCols,String[] addFields,String[] addValues,String[] addTypes) throws Exception{
    	File excel = new File(file);
        InputStream fis = new FileInputStream(excel);
        Workbook wbook = WorkbookFactory.create(fis); //new XSSFWorkbook(fis);//
        Sheet wsheet = wbook.getSheetAt(0);
        wsheet.setForceFormulaRecalculation(true);

        int rowNum = wsheet.getLastRowNum() + 1;
        int colNum = wsheet.getRow(0).getLastCellNum();
        int[] indexCol = new int[sCols.length]; 
        //Read the headers first. Locate the ones you need
        Row rowHeader = wsheet.getRow(0);
        for (int j = 0; j < colNum; j++) {
            Cell cell = rowHeader.getCell(j);
            String cellValue = cellToString(cell);
            for (int k=0; k<sCols.length;k++){
            	if(sCols[k].equalsIgnoreCase(cellValue)){
            		indexCol[k] = j;
            		break;
            	}
            }
        }
        String[] sqlList = new String[rowNum - 1];
        String sqlConst = "",valueConst="";//"Insert Into " + tableName + " (";

        for (int i=0;i<dbFields.length - 1;i++){
        	sqlConst += dbFields[i] + ",";
        }
        sqlConst += dbFields[dbFields.length - 1];
        if (addFields.length > 0)
        	for (int i = 0;i<addFields.length;i++)
        		sqlConst += (","+ addFields[i]);
        
        sqlConst = "Insert Into " + tableName + " ("+ sqlConst + ") Values(";
        
        for (int i = 1; i < rowNum; i++) {
        	valueConst = "";
            Row row = wsheet.getRow(i);
            String[] colValue = new String[sCols.length];
            for (int j=0;j<sCols.length;j++){
            	try{
            		colValue[j] = cellToString(row.getCell(indexCol[j]));
            	}catch(Exception e){
            		colValue[j] = "";
            	}

				if (dbFieldsType[j].equalsIgnoreCase("STRING")){
					valueConst += ("'" + colValue[j]+"',");
				}
				else if (dbFieldsType[j].equalsIgnoreCase("NUMBER")) {
					valueConst += (colValue[j]+",");
				}
				else{
					valueConst += ("'" + colValue[j]+"',");
				}

            }
            if (valueConst.charAt(valueConst.length() - 1) == ',') {
            	valueConst = valueConst.substring(0, valueConst.length() - 1);
            }
            if (addFields.length > 0){
            	for (int j=0;j<addFields.length;j++){
            		if (addTypes[j].equalsIgnoreCase("STRING")){
    	            		valueConst += (",'" + addValues[j]+"'");
                	}
                	else if (addTypes[j].equalsIgnoreCase("NUMBER")) {
    	            		valueConst += (","+ addValues[j]);
    				}
                	else{
    	            		valueConst += (",'" + addValues[j]+"'");
                	}
            	}
            }
            sqlList[i - 1] = sqlConst + valueConst+")";
        }

        fis.close();
        
        try{
        	wbook.close();
        }catch(Exception e){wbook = null;}
	   return sqlList;
  } 
   
    private static String cellToString(Cell cell){
	    Object value = null;
	    DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
	    DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字
	    switch (cell.getCellTypeEnum()) {
	        case STRING:
	            value = cell.getRichStringCellValue().getString();
	            break;
	        case NUMERIC:
	            if("General".equals(cell.getCellStyle().getDataFormatString())) {
	                value = df.format(cell.getNumericCellValue());
	            } else {
	                value = df2.format(cell.getNumericCellValue());
	            }
	            break;
	        case BOOLEAN:
	            value = cell.getBooleanCellValue();
	            break;
	        case BLANK:
	            value = "";
	            break;
	        default:
	            break;
	    }
	    try{
	        return value.toString();
	    }catch (Exception e) {
	    	return "";
		}
	}

	public static void zzsWriteToNewExcel(String file,String[] sTitle, String[][] valueList,String[] valueType) throws FileNotFoundException{
		Workbook wb = new HSSFWorkbook();
		    //Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet("newSheet");

		    // Create a row and put some cells in it. Rows are 0 based. set Colomntitle
		Row row = sheet.createRow((short)0);
		for (int i=0;i<sTitle.length;i++){
			row.createCell(i).setCellValue(sTitle[i]);
		}
		for (int i=0;i<valueList.length;i++){
			row = sheet.createRow(i+1);
			for (int j=0;j<sTitle.length;j++){
				if (valueType[j].equalsIgnoreCase("STRING"))
					row.createCell(j).setCellValue(createHelper.createRichTextString(valueList[i][j]));
				else 
					row.createCell(j).setCellValue(valueList[i][j]);
			}
		}

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream(file);
	    try {
			wb.write(fileOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			wb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void zzsFillDataToExcel(String file, String[][] valueList) throws IOException{
		InputStream fis = new FileInputStream(file);
        Workbook wb = null;
		try {
			wb = WorkbookFactory.create(fis);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sheet sheet = wb.getSheetAt(0);

		for (int i=0;i<valueList.length;i++){
			 Row row = sheet.getRow(Integer.parseInt(valueList[i][0]));
			 row.getCell(Integer.parseInt(valueList[i][1])).setCellValue(valueList[i][2]);
		}

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream(file);
	    try {
			wb.write(fileOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			wb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static void zzsSqlRowSetToExcelFile(String file, SqlRowSet rs){
	       Connection con=null;
	       PreparedStatement pstam = null;
	       Workbook wb = null;
	       FileOutputStream fileOut = null;
		   try{
			   wb = new HSSFWorkbook();
			   //CreationHelper createHelper = wb.getCreationHelper();
			   Sheet sheet = wb.createSheet("Sheet1");
			   Row row = sheet.createRow((short)0);
	           SqlRowSetMetaData rsmd =  rs.getMetaData();
	           int icol = rsmd.getColumnCount();
	           for (int i=0;i<icol;i++){
	       			row.createCell(i).setCellValue(rsmd.getColumnName(i + 1));
	       	   }
	           int i=1;
	           while (rs.next()) {
	        	   row = sheet.createRow(i);
	        	   for (int j=0;j<icol;j++){
	        		   try{
		       			row.createCell(j).setCellValue(rs.getString(j + 1).toString().trim());	  
	        		   }catch (Exception e) {
						// TODO: handle exception
	        		   }
	       			}
	        	   i++;
	           }
	           
	           fileOut = new FileOutputStream(file);
	           try {
	       			wb.write(fileOut);
	       	   } catch (IOException e) {
	       		// TODO Auto-generated catch block
	       		   e.printStackTrace();
	       	   }
	           
			   
		   }catch (Exception e) {
			// TODO: handle exception
		   }finally {
			   if (null != fileOut){
				    try {
						fileOut.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				    fileOut = null;
			   }
			   if (null != wb){
				    try {
						wb.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				    wb = null;
			   }
		}
    }
}
