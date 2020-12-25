package com.official.OfficialProject.service.dbhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public abstract class ZZSDBService {

    //private static final Logger LOGGER = LoggerFactory.getLogger(ZZSDBService.class);
    public ZZSDBService() {
    }

    ///
    //Simple sql access using spring JdbcTemplate
    // zzs 2019-05-01
    ///

    public static int InsertUpdateDelete(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql) {
        return jdbcTmp.update(sql);
    }

    public static String GetSingleValue(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql) {
        try {
            List<String> vt = jdbcTmp.query(sql, (rs, rowNumber) -> rs.getString(1));
            return vt.get(0);
        }catch (Exception e){;}
        return null;
    }

    public static Long GetInsertKeyValue(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql,String sKey) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTmp.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{sKey});
                    return ps;
                }, holder);
        // jdbcTmp.update(sql,holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }


    public static SqlRowSet getSqlRowSet(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql){
        return jdbcTmp.queryForRowSet(sql);
    }

    public static List<Map<String, Object>> getListMap(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql){
        return jdbcTmp.queryForList(sql);
    }

    public static int[] InsertUpdateDeleteForBatch(@NotNull JdbcTemplate jdbcTmp, @NotNull String[] sqls) {
        return jdbcTmp.batchUpdate(sqls);
    }

    public static int[] InsertUpdateDeleteForTran(@NotNull JdbcTemplate jdbcTmp, @NotNull String[] sqls) throws SQLException {
        Connection con = jdbcTmp.getDataSource().getConnection();
        con.setAutoCommit(false);
        Statement[] astm = new Statement[sqls.length];
        int[] retInt = new int[sqls.length];
        try {
            for (int i = 0; i < sqls.length; i++) {
                astm[i] = con.createStatement();
                retInt[i] = astm[i].executeUpdate(sqls[i]);
            }
            con.commit();
            return retInt;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            for (int i = 0; i < sqls.length; i++) {
                try {
                    if (astm[i] != null) {
                        astm[i].close();
                        astm[i] = null;
                    }
                }catch (SQLException qe){

                }
            }
            con.close();
        }
    }

    /// --
    //Sql with parameters by using spring JdbcTemplate -zzs 2019-04-09
    ///--

    public static int InsertUpdateDelete(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, String sParasStr){
        DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return jdbcTmp.update(sNewSql,params);
    }
    public static int InsertUpdateDelete(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara){
        DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return jdbcTmp.update(sNewSql,params);
    }
    public static int LJZInsertUpdateDelete(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara){
        DataStruc thisStru = LJZMakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return jdbcTmp.update(sNewSql,params);
    }

    public static String GetSingleValue(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql, String sParasStr) {
        DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        try {
            List<String> vt = jdbcTmp.query(sNewSql, params, (rs, rowNumber) -> rs.getString(1));
            return vt.get(0);
        }catch (Exception e){;}
        return null;
    }

    public static String GetSingleValue(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        try {
            List<String> vt = jdbcTmp.query(sNewSql, params, (rs, rowNumber) -> rs.getString(1));
            return vt.get(0);
        }catch (Exception e){;}
        return null;
    }

    public static Long GetInsertKeyValue(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, String sParasStr,String sKey) throws RuntimeException {
        DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        final Object[]  params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTmp.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sNewSql, new String[]{sKey});
                    for (int j = 0; j < params.length; j++) {
                        if (params[j].equals(null) || params[j].equals(""))
                            ps.setNull(j + 1, Types.NULL);
                        else
                            ps.setObject(j + 1, params[j]);
                    }
                    return ps;
                }, holder);

        //bcTmp.update(sNewSql,params,holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }

    public static Long GetInsertKeyValue(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara,String sKey) throws RuntimeException {
        DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTmp.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sNewSql, new String[]{sKey});
                    for (int j = 0; j < params.length; j++) {
                        if (params[j].equals(null) || params[j].equals(""))
                            ps.setNull(j + 1, Types.NULL);
                        else
                            ps.setObject(j + 1, params[j]);
                    }
                    return ps;
                }, holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }


    public static SqlRowSet getSqlRowSet(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, String sParasStr) {
        DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return jdbcTmp.queryForRowSet(sNewSql,params);
    }
    public static SqlRowSet getSqlRowSet(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return jdbcTmp.queryForRowSet(sNewSql,params);
    }

    public static List<Map<String, Object>> getQueryListMap(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, String sParasStr) {
        DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return jdbcTmp.queryForList(sNewSql,params);
    }
    public static List<Map<String, Object>> getQueryListMap(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return jdbcTmp.queryForList(sNewSql,params);
    }

    public static int[] InsertUpdateDeleteForTran(@NotNull JdbcTemplate jdbcTmp,@NotNull String[] sqls, String sParasStr) throws SQLException {
        Connection con = jdbcTmp.getDataSource().getConnection();
        con.setAutoCommit(false);
        PreparedStatement[] apstm = new PreparedStatement[sqls.length];
        int[] retInt = new int[sqls.length];
        try {
            for (int i = 0; i < sqls.length; i++) {
                DataStruc thisStru = MakesqlParasObj(sParasStr, sqls[i]);
                Object[] params = thisStru.getParaObj();
                String sNewSql = thisStru.getSqlStr();
                thisStru = null;
                apstm[i] = con.prepareStatement(sNewSql);
                if (params == null) {
                    params = new Object[0];
                }
                for (int j = 0; j < params.length; j++) {
                    if (params[j].equals(null) || params[j].equals(""))
                        apstm[i].setNull(j + 1, Types.NULL);
                    else
                        apstm[i].setObject(j + 1, params[j]);
                }
                retInt[i] = apstm[i].executeUpdate();
            }
            con.commit();
            return retInt;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            for (int i = 0; i < sqls.length; i++) {
                try {
                    if (apstm[i] != null) {
                        apstm[i].close();
                        apstm[i] = null;
                    }
                }catch (SQLException qe){
                }
            }
            con.close();
        }
    }
    public static int[] InsertUpdateDeleteForTran(@NotNull JdbcTemplate jdbcTmp,@NotNull String[] sqls, Map<String,String> mapPara) throws SQLException {
        Connection con = jdbcTmp.getDataSource().getConnection();
        con.setAutoCommit(false);
        PreparedStatement[] apstm = new PreparedStatement[sqls.length];
        int[] retInt = new int[sqls.length];
        try {
            for (int i = 0; i < sqls.length; i++) {
                DataStruc thisStru = MakesqlParasObj(mapPara, sqls[i]);
                Object[] params = thisStru.getParaObj();
                String sNewSql = thisStru.getSqlStr();
                thisStru = null;
                apstm[i] = con.prepareStatement(sNewSql);
                if (params == null) {
                    params = new Object[0];
                }
                for (int j = 0; j < params.length; j++) {
                    if (params[j].equals(null) || params[j].equals(""))
                        apstm[i].setNull(j + 1, Types.NULL);
                    else
                        apstm[i].setObject(j + 1, params[j]);
                }
                retInt[i] = apstm[i].executeUpdate();
            }
            con.commit();
            return retInt;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            for (int i = 0; i < sqls.length; i++) {
                try {
                    if (apstm[i] != null) {
                        apstm[i].close();
                        apstm[i] = null;
                    }
                }catch (SQLException qe){
                }
            }
            con.close();
        }
    }

    public static  Map<String, Object> callNoQueryProcedure(@NotNull JdbcTemplate jdbcTmp,String proName, Map<String,Object> paraMap) throws SQLException{
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTmp).withProcedureName(proName);
        Map<String, Object> inParamMap = new HashMap<>();
        paraMap.forEach((k,v)->{
            inParamMap.put(k,v);
        });
        SqlParameterSource sqlPs = new MapSqlParameterSource(inParamMap);
        return jdbcCall.execute(sqlPs);
    }

    ///
    // Sql with CLOB AND BLOB Access by using spring JdbcTemplate 2019-04-04
    ///

    // sql--> CLOB Varable must put the first parameter
    public static long InsertUpdateCLOB(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql, Reader reader, String sParasStr) throws SQLException {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTmp.update((Connection conn)-> {
                PreparedStatement ps = conn.prepareStatement(sql.toString(),
                        Statement.RETURN_GENERATED_KEYS);
                ps.setClob(1, reader);
                DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
                Object[] params = thisStru.getParaObj();
                String sNewSql = thisStru.getSqlStr();
                thisStru = null;
                if (params == null) {
                    params = new Object[0];
                }
                for (int j = 0; j < params.length; j++) {
                    if (params[j].equals(null) || params[j].equals(""))
                        ps.setNull(j + 2, Types.NULL);
                    else
                        ps.setObject(j + 2, params[j]);
                }
                return ps;
        }, holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }
    public static long InsertUpdateCLOB(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql, Reader reader, Map<String,String> mapPara) throws SQLException {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTmp.update((Connection conn)-> {
            PreparedStatement ps = conn.prepareStatement(sql.toString(),
                    Statement.RETURN_GENERATED_KEYS);
            ps.setClob(1, reader);
            DataStruc thisStru = MakesqlParasObj(mapPara, sql);
            Object[] params = thisStru.getParaObj();
            String sNewSql = thisStru.getSqlStr();
            thisStru = null;
            if (params == null) {
                params = new Object[0];
            }
            for (int j = 0; j < params.length; j++) {
                if (params[j].equals(null) || params[j].equals(""))
                    ps.setNull(j + 2, Types.NULL);
                else
                    ps.setObject(j + 2, params[j]);
            }
            return ps;
        }, holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }

    public static Reader GetCLOBReader(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql, String sParasStr) throws RuntimeException {
        DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        List<Reader> readers = jdbcTmp.query(sNewSql,params,(resultSet, i) -> resultSet.getCharacterStream(1));
        if (readers.size() == 1)
            return readers.get(0);
        throw new RuntimeException("No CLob Data Found.");
    }
    public static Reader GetCLOBReader(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql, Map<String,String> mapPara) throws RuntimeException {
        DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        List<Reader> readers = jdbcTmp.query(sNewSql,params,(resultSet, i) -> resultSet.getCharacterStream(1));
        if (readers.size() == 1)
            return readers.get(0);
        throw new RuntimeException("No CLob Data Found.");
    }

    //sql--> BLOB Varable must put the first parameter
    public static Long InsertUpdateBLOB(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, InputStream ips, String sParasStr) {
        KeyHolder holder = new GeneratedKeyHolder();
        final DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        String sNewSql = thisStru.getSqlStr();
        jdbcTmp.update((Connection conn)-> {
            PreparedStatement ps = conn.prepareStatement(sNewSql.toString(),
                    Statement.RETURN_GENERATED_KEYS);
            ps.setBlob(1, ips);
            Object[] params = thisStru.getParaObj();
            if (params == null) {
                params = new Object[0];
            }
            for (int j = 0; j < params.length; j++) {
                if (params[j].equals(null) || params[j].equals(""))
                    ps.setNull(j + 2, Types.NULL);
                else
                    ps.setObject(j + 2, params[j]);
            }
            return ps;
        }, holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }
    public static Long InsertUpdateBLOB(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, InputStream ips, Map<String,String> mapPara) {
        KeyHolder holder = new GeneratedKeyHolder();
        final DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        String sNewSql = thisStru.getSqlStr();
        jdbcTmp.update((Connection conn)-> {
            PreparedStatement ps = conn.prepareStatement(sNewSql.toString(),
                    Statement.RETURN_GENERATED_KEYS);
            ps.setBlob(1, ips);
            Object[] params = thisStru.getParaObj();
            if (params == null) {
                params = new Object[0];
            }
            for (int j = 0; j < params.length; j++) {
                if (params[j].equals(null) || params[j].equals(""))
                    ps.setNull(j + 2, Types.NULL);
                else
                    ps.setObject(j + 2, params[j]);
            }
            return ps;
        }, holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }

    public static InputStream GetBLOBStream(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql, String sParasStr) throws RuntimeException {
        DataStruc thisStru = MakesqlParasObj(sParasStr, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        List<InputStream> istram = jdbcTmp.query(sNewSql,params,(resultSet, i) -> resultSet.getBinaryStream(1));
        if (istram.size() == 1)
            return istram.get(0);
        throw new RuntimeException("No BLob Data Found.");
    }
    public static InputStream GetBLOBStream(@NotNull JdbcTemplate jdbcTmp, @NotNull String sql, Map<String,String> mapPara) throws RuntimeException {
        DataStruc thisStru = MakesqlParasObj(mapPara, sql);
        Object[] params = thisStru.getParaObj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        List<InputStream> istram = jdbcTmp.query(sNewSql,params,(resultSet, i) -> resultSet.getBinaryStream(1));
        if (istram.size() == 1)
            return istram.get(0);
        throw new RuntimeException("No BLob Data Found.");
    }

    ///
    // zzs JSON Related Access 2019-04-12
    ///

    /*
     * Convert ResultSet to a common JSON Object array
     * Result is like: [{"ID":"1","NAME":"Tom","AGE":24}, {"ID":"2","NAME":"Bob","AGE":26}, ...]
     */
    public static ArrayList<JSONObject> getFormattedJSONResult(SqlRowSet rs) {
        ArrayList<JSONObject> resList = new ArrayList<JSONObject>();
        try {
            SqlRowSetMetaData rsMeta = rs.getMetaData();
            int columnCnt = rsMeta.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (String s : rsMeta.getColumnNames()) {
                columnNames.add(s);
            }
            while (rs.next()) { // convert each object to an human readable JSON object
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= columnCnt; i++) {
                    String key = columnNames.get(i - 1);
                    String value = rs.getString(i);
                    if (value != null && !value.isEmpty())
                        obj.put(key, value);
                    else
                        obj.put(key, JSONObject.NULL);
                }
                resList.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    public static String GetJSONString(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql) {

        try {
            SqlRowSet rs = getSqlRowSet(jdbcTmp,sql);
            return getFormattedJSONResult(rs).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static String GetJSONString(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql,String sParasStr) {

        try {
            SqlRowSet rs = getSqlRowSet(jdbcTmp,sql,sParasStr);
            return getFormattedJSONResult(rs).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static String GetJSONString(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql,Map<String,String> mapPara) {
        try {
            SqlRowSet rs = getSqlRowSet(jdbcTmp,sql,mapPara);
            return getFormattedJSONResult(rs).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static JSONArray GetJSONArray(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, String sParasStr) {
        try {
            SqlRowSet rs = getSqlRowSet(jdbcTmp,sql,sParasStr);
            return new JSONArray(getFormattedJSONResult(rs));
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static JSONArray GetJSONArray(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        try {
            SqlRowSet rs = getSqlRowSet(jdbcTmp,sql,mapPara);
            return new JSONArray(getFormattedJSONResult(rs));
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static JSONObject GetJSONObject(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, String sParasStr) {
        try {
            SqlRowSet rSet = getSqlRowSet(jdbcTmp,sql,sParasStr);
            SqlRowSetMetaData rsMeta = rSet.getMetaData();
            int columnCnt = rsMeta.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (String s : rsMeta.getColumnNames()) {
                columnNames.add(s);
            }
            if (rSet.next()) { // convert each object to an human readable JSON object
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= columnCnt; i++) {
                    String key = columnNames.get(i - 1);
                    String value = rSet.getString(i);
                    if (value != null && !value.isEmpty())
                        obj.put(key, value);
                    else
                        obj.put(key, JSONObject.NULL);
                }
                return obj;
            }
            return null;
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static JSONObject GetJSONObject(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        try {
            SqlRowSet rSet = getSqlRowSet(jdbcTmp,sql,mapPara);
            SqlRowSetMetaData rsMeta = rSet.getMetaData();
            int columnCnt = rsMeta.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (String s : rsMeta.getColumnNames()) {
                columnNames.add(s);
            }
            if (rSet.next()) { // convert each object to an human readable JSON object
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= columnCnt; i++) {
                    String key = columnNames.get(i - 1);
                    String value = rSet.getString(i);
                    if (value != null && !value.isEmpty())
                        obj.put(key, value);
                    else
                        obj.put(key, JSONObject.NULL);
                }
                return obj;
            }
            return null;
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static String GetJSONString2(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql) throws SQLException, JSONException {
        try {
            SqlRowSet rSet = getSqlRowSet(jdbcTmp,sql);
            return SqlRowSetConvertJSONArray(rSet).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static String GetJSONString2(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, String sParasStr) throws SQLException, JSONException {
        try {
            SqlRowSet rSet = getSqlRowSet(jdbcTmp, sql, sParasStr);
            return SqlRowSetConvertJSONArray(rSet).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static String GetJSONString2(@NotNull JdbcTemplate jdbcTmp,@NotNull String sql, Map<String,String> mapPara) throws SQLException, JSONException {
        try {
            SqlRowSet rSet = getSqlRowSet(jdbcTmp, sql, mapPara);
            return SqlRowSetConvertJSONArray(rSet).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }


    public static JSONArray SqlRowSetConvertJSONArray(SqlRowSet rs) throws SQLException, JSONException {
        JSONArray json = new JSONArray();
        SqlRowSetMetaData rsmd = rs.getMetaData();
        try {
            while (rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();

                for (int i = 1; i < numColumns + 1; i++) {
                    String column_name = rsmd.getColumnName(i);

                    switch (rsmd.getColumnType(i)) {
//                        case Types.ARRAY:
//                            obj.put(column_name, rs.getArray(column_name).equals(null) ? JSONObject.NULL : rs.getArray(column_name));
//                            break;
                        case Types.BIGINT:
                            obj.put(column_name, rs.getInt(column_name));
                            break;
                        case Types.BOOLEAN:
                            obj.put(column_name, rs.getBoolean(column_name));
                            break;
//                        case Types.BLOB:
//                            obj.put(column_name, rs.getBlob(column_name).equals(null) ? JSONObject.NULL : rs.getBlob(column_name));
//                            break;
                        case Types.DOUBLE:
                            obj.put(column_name, rs.getDouble(column_name));
                            break;
                        case Types.FLOAT:
                            obj.put(column_name, rs.getFloat(column_name));
                            break;
                        case Types.INTEGER:
                            obj.put(column_name, rs.getInt(column_name));
                            break;
                        case Types.NVARCHAR:
                            obj.put(column_name, Objects.isNull(rs.getString(column_name)) ? JSONObject.NULL : rs.getString(column_name));
                            break;
                        case Types.VARCHAR:
                            obj.put(column_name, Objects.isNull(rs.getString(column_name)) ? JSONObject.NULL : rs.getNString(column_name));
                            break;
                        case Types.TINYINT:
                            obj.put(column_name, rs.getInt(column_name));
                            break;
                        case Types.SMALLINT:
                            obj.put(column_name, rs.getInt(column_name));
                            break;
                        case Types.DATE:
                            obj.put(column_name, Objects.isNull(rs.getDate(column_name)) ? JSONObject.NULL : rs.getDate(column_name));
                            break;
                        case Types.TIMESTAMP:
                            obj.put(column_name, Objects.isNull(rs.getTimestamp(column_name)) ? JSONObject.NULL : rs.getTimestamp(column_name));
                            break;
                        default:
                            obj.put(column_name, Objects.isNull(rs.getObject(column_name)) ? JSONObject.NULL : rs.getObject(column_name));
                            break;
                    }
                }
                json.put(obj);
            }
            return json;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            if (null != rsmd)
                rsmd = null;
        }
    }

    /// --
    //Sql with parameters by using NamedParameterJdbcTemplate -zzs 2019-04-10
    ///--

    public static int InsertUpdateDelete(@NotNull NamedParameterJdbcTemplate npjdbcTmp, @NotNull String sql, String sParasStr){
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(sParasStr, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return npjdbcTmp.update(sNewSql,mapParams);
    }
    public static int InsertUpdateDelete(@NotNull NamedParameterJdbcTemplate npjdbcTmp, @NotNull String sql, Map<String,String> mapPara){
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(mapPara, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return npjdbcTmp.update(sql,mapPara);
    }

    public static String GetSingleValue(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, String sParasStr) {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(sParasStr, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        try {
            List<String> vt = npjdbcTmp.query(sNewSql, mapParams, (rs, rowNumber) -> rs.getString(1));
            return vt.get(0);
        }catch (Exception e){;}
        return null;
    }
    public static String GetSingleValue(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(mapPara, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        try{ List<String> vt =
            npjdbcTmp.query(sNewSql,mapParams,(rs,rowNumber) ->rs.getString(1));
            return vt.get(0);
        } catch (Exception e){;};
        return null;
    }

    public static Long GetInsertKeyValue(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, String sParasStr) throws RuntimeException {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(sParasStr, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        KeyHolder holder = new GeneratedKeyHolder();
        npjdbcTmp.update(sNewSql, (SqlParameterSource) mapParams,holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }
    public static Long GetInsertKeyValue(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, Map<String,String> mapPara) throws RuntimeException {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(mapPara, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        KeyHolder holder = new GeneratedKeyHolder();
        npjdbcTmp.update(sNewSql, (SqlParameterSource) mapParams,holder);
        Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }

    public static SqlRowSet getSqlRowSet(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, String sParasStr) {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(sParasStr, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return npjdbcTmp.queryForRowSet(sNewSql,mapParams);
    }
    public static SqlRowSet getSqlRowSet(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(mapPara, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return npjdbcTmp.queryForRowSet(sNewSql,mapParams);
    }

    public static List<Map<String, Object>> getQueryListMap(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, String sParasStr) {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(sParasStr, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return npjdbcTmp.queryForList(sNewSql,mapParams);
    }
    public static List<Map<String, Object>> getQueryListMap(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(mapPara, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        thisStru = null;
        return npjdbcTmp.queryForList(sNewSql,mapParams);
    }

    public static  Map<String, Object> callNoQueryProcedure(@NotNull NamedParameterJdbcTemplate npjdbcTmp,String proName, Map<String,Object> paraMap) throws SQLException{
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(npjdbcTmp.getJdbcTemplate()).withProcedureName(proName);
        Map<String, Object> inParamMap = new HashMap<>();
        paraMap.forEach((k,v)->{
            inParamMap.put(k,v);
        });
        SqlParameterSource sqlPs = new MapSqlParameterSource(inParamMap);
        return jdbcCall.execute(sqlPs);
    }

    public static Reader GetCLOBReader(@NotNull NamedParameterJdbcTemplate npjdbcTmp, @NotNull String sql, String sParasStr) throws RuntimeException {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(sParasStr, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        List<Reader> readers = npjdbcTmp.query(sNewSql,mapParams,(resultSet, i) -> resultSet.getCharacterStream(1));
        if (readers.size() == 1)
            return readers.get(0);
        throw new RuntimeException("No CLob Data Found.");
    }
    public static Reader GetCLOBReader(@NotNull NamedParameterJdbcTemplate npjdbcTmp, @NotNull String sql, Map<String,String> mapPara) throws RuntimeException {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(mapPara, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        List<Reader> readers = npjdbcTmp.query(sNewSql,mapParams,(resultSet, i) -> resultSet.getCharacterStream(1));
        if (readers.size() == 1)
            return readers.get(0);
        throw new RuntimeException("No CLob Data Found.");
    }

    public static InputStream GetBLOBStream(@NotNull NamedParameterJdbcTemplate npjdbcTmp, @NotNull String sql, String sParasStr) throws RuntimeException {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(sParasStr, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        List<InputStream> istram = npjdbcTmp.query(sNewSql,mapParams,(resultSet, i) -> resultSet.getBinaryStream(1));
        if (istram.size() == 1)
            return istram.get(0);
        throw new RuntimeException("No BLob Data Found.");
    }
    public static InputStream GetBLOBStream(@NotNull NamedParameterJdbcTemplate npjdbcTmp, @NotNull String sql, Map<String,String> mapPara) throws RuntimeException {
        DataStrucNamedPara thisStru = MakesqlParasObjForNamedPara(mapPara, sql);
        Map<String,String> mapParams = thisStru.getMapobj();
        String sNewSql = thisStru.getSqlStr();
        List<InputStream> istram = npjdbcTmp.query(sNewSql,mapParams,(resultSet, i) -> resultSet.getBinaryStream(1));
        if (istram.size() == 1)
            return istram.get(0);
        throw new RuntimeException("No BLob Data Found.");
    }

    public static String GetJSONString(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql,String sParasStr) {
        try {
            SqlRowSet rs = getSqlRowSet(npjdbcTmp,sql,sParasStr);
            return getFormattedJSONResult(rs).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static String GetJSONString(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql,Map<String,String> mapPara) {
        try {
            SqlRowSet rs = getSqlRowSet(npjdbcTmp,sql,mapPara);
            return getFormattedJSONResult(rs).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static JSONArray GetJSONArray(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, String sParasStr) {
        try {
            SqlRowSet rs = getSqlRowSet(npjdbcTmp,sql,sParasStr);
            return new JSONArray(getFormattedJSONResult(rs));
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static JSONArray GetJSONArray(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        try {
            SqlRowSet rs = getSqlRowSet(npjdbcTmp,sql,mapPara);
            return new JSONArray(getFormattedJSONResult(rs));
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static JSONObject GetJSONObject(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, String sParasStr) {
        try {
            SqlRowSet rSet = getSqlRowSet(npjdbcTmp,sql,sParasStr);
            SqlRowSetMetaData rsMeta = rSet.getMetaData();
            int columnCnt = rsMeta.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (String s : rsMeta.getColumnNames()) {
                columnNames.add(s);
            }
            if (rSet.next()) { // convert each object to an human readable JSON object
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= columnCnt; i++) {
                    String key = columnNames.get(i - 1);
                    String value = rSet.getString(i);
                    if (value != null && !value.isEmpty())
                        obj.put(key, value);
                    else
                        obj.put(key, JSONObject.NULL);
                }
                return obj;
            }
            return null;
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static JSONObject GetJSONObject(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, Map<String,String> mapPara) {
        try {
            SqlRowSet rSet = getSqlRowSet(npjdbcTmp,sql,mapPara);
            SqlRowSetMetaData rsMeta = rSet.getMetaData();
            int columnCnt = rsMeta.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (String s : rsMeta.getColumnNames()) {
                columnNames.add(s);
            }
            if (rSet.next()) { // convert each object to an human readable JSON object
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= columnCnt; i++) {
                    String key = columnNames.get(i - 1);
                    String value = rSet.getString(i);
                    if (value != null && !value.isEmpty())
                        obj.put(key, value);
                    else
                        obj.put(key, JSONObject.NULL);
                }
                return obj;
            }
            return null;
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    public static String GetJSONString2(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, String sParasStr) throws SQLException, JSONException {
        try {
            SqlRowSet rSet = getSqlRowSet(npjdbcTmp, sql, sParasStr);
            return SqlRowSetConvertJSONArray(rSet).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }
    public static String GetJSONString2(@NotNull NamedParameterJdbcTemplate npjdbcTmp,@NotNull String sql, Map<String,String> mapPara) throws SQLException, JSONException {
        try {
            SqlRowSet rSet = getSqlRowSet(npjdbcTmp, sql, mapPara);
            return SqlRowSetConvertJSONArray(rSet).toString();
        } catch (Exception e) {
            return null;
            // TODO: handle exception
        }
    }

    ///
    // zzs Common DB Service
    ///

    public static DataStruc MakesqlParasObj(String sParasStr, String sqlText) {
        if (sParasStr == null || sParasStr.trim() == "" || sqlText == null || sqlText.trim() == "")
            return null;
        Map<String, String> mapParams = Arrays.asList(sParasStr.split("&"))
                .stream()
                .map(elem -> elem.split("="))
                .filter(elem -> elem.length == 2)
                .collect(Collectors.toMap(e -> e[0], e -> e[1])); //zzs 2018-04-29
        return MakesqlParasObj(mapParams, sqlText);
    }

    public static Object[] MakesqlObjectPara(String sParasStr, String sqlText){
        DataStruc dataStruc = MakesqlParasObj(sParasStr,sqlText);
        return dataStruc.obj;
    }
    private static DataStruc MakesqlParasObj(Map<String,String> mapParams, String sqlText) {
        if (mapParams == null || mapParams.size() == 0 || sqlText == null || sqlText.trim() == "")
            return null;
        List<String> lVarables = new ArrayList<>();
        Matcher matcher = Pattern.compile("[@:]\\s*(\\w+)").matcher(sqlText);
        while (matcher.find()) {
            lVarables.add(matcher.group(1));
        }
        List<String> ilistStr = lVarables.stream()
                .map(s -> mapParams.containsKey(s) ? mapParams.get(s) : "")
                .collect(Collectors.toList());

        String sNewSql = sqlText.replaceAll("[@:]\\s*(\\w+)","?");

        DataStruc retStru = new DataStruc();
        Object[] obj = ilistStr.toArray();
        retStru.setParaObj(obj);
        retStru.setSqlStr(sNewSql);
        return retStru;
    }
    private static DataStruc LJZMakesqlParasObj(Map<String,String> mapParams, String sqlText) {
        if (mapParams == null || mapParams.size() == 0 || sqlText == null || sqlText.trim() == "")
            return null;
        List<String> lVarables = new ArrayList<>();
        Matcher matcher = Pattern.compile("[@]\\s*(\\w+)").matcher(sqlText);
        while (matcher.find()) {
            lVarables.add(matcher.group(1));
        }
        List<String> ilistStr = lVarables.stream()
                .map(s -> mapParams.containsKey(s) ? mapParams.get(s) : "")
                .collect(Collectors.toList());

        String sNewSql = sqlText.replaceAll("[@]\\s*(\\w+)","?");

        DataStruc retStru = new DataStruc();
        Object[] obj = ilistStr.toArray();
        retStru.setParaObj(obj);
        retStru.setSqlStr(sNewSql);
        return retStru;
    }

    public static class DataStruc {
        private Object[] obj = null;
        private String sqlStr = "";

        public DataStruc() {
        }

        public Object[] getParaObj() {
            return obj;
        }

        public void setParaObj(Object[] obj) {
            this.obj = obj;
        }

        public String getSqlStr() {
            return sqlStr;
        }

        public void setSqlStr(String sqlStr) {
            this.sqlStr = sqlStr;
        }
    }

    private static DataStrucNamedPara MakesqlParasObjForNamedPara(String sParasStr, String sqlText){
        if (sParasStr == null || sParasStr.trim() == "" || sqlText == null || sqlText.trim() == "")
            return null;
        List<String> lVarables = new ArrayList<>();
        Map<String, String> mapParams = Arrays.asList(sParasStr.split("&")).stream()
                .map(elem -> elem.split("="))
                .filter(elem -> elem.length == 2)
                .collect(Collectors.toMap(e -> e[0], e -> e[1])); //zzs 2018-04-29

        return MakesqlParasObjForNamedPara(mapParams,sqlText);
    }
    private static DataStrucNamedPara MakesqlParasObjForNamedPara(Map<String,String> mapParams, String sqlText){
        if (mapParams == null || mapParams.size() == 0 || sqlText == null || sqlText.trim() == "")
            return null;
        List<String> lVarables = new ArrayList<>();
        Matcher matcher = Pattern.compile("[@:]\\s*(\\w+)").matcher(sqlText);
        while (matcher.find()) {
            lVarables.add(matcher.group(1));
        }
        mapParams.entrySet().removeIf( e -> !lVarables.contains( e.getKey()));

        String sNewSql = sqlText.replaceAll("@",":");

        DataStrucNamedPara retStru = new DataStrucNamedPara();
        retStru.setMapobj(mapParams);
        retStru.setSqlStr(sNewSql);
        return retStru;
    }

    private static class DataStrucNamedPara {

        private Map<String, String> mapobj = null;
        private String sqlStr = "";

        public DataStrucNamedPara() {
        }
        public Map<String, String> getMapobj() {
            return mapobj;
        }

        public void setMapobj(Map<String, String> mapobj) {
            this.mapobj = mapobj;
        }

        public String getSqlStr() {
            return sqlStr;
        }

        public void setSqlStr(String sqlStr) {
            this.sqlStr = sqlStr;
        }
    }

    private static int IndexOfAny(String valueStr, char[] cArr) {
        int thisIndex = -1, cIndex = -1;
        for (int i = 0; i < cArr.length; i++) {
            cIndex = valueStr.indexOf(cArr[i]);
            if (cIndex > -1) {
                if (thisIndex == -1)
                    thisIndex = cIndex;
                else
                    thisIndex = thisIndex > cIndex ? cIndex : thisIndex;
            }
        }
        return thisIndex;
    }

    ///
    // zzs Common Application Utilities
    ///

    public static String GetSelectLinkStr(@NotNull JdbcTemplate jdbcTmp,@NotNull String strSql) {
        String sRtn = "";
        try {
            SqlRowSet rs = getSqlRowSet(jdbcTmp, strSql);
            SqlRowSetMetaData rsmd = rs.getMetaData();

            for(int icol = rsmd.getColumnCount(); rs.next(); sRtn = sRtn + rs.getString(icol).trim() + ";") {
                for(int j = 1; j < icol; ++j) {
                    String data = "";
                    if(rs.getString(j) != null)
                        data = rs.getString(j).trim();
                    else
                        data = "";
                    sRtn = sRtn + data+ ",";
                }
            }
            if (sRtn.length() > 1) {
                sRtn = sRtn.substring(0, sRtn.length() - 1);
            }
            return sRtn;
        } catch (Exception var11) {
            sRtn = "";
        }
        return sRtn;
    }

    public static String GetSelectLinkStr(@NotNull JdbcTemplate jdbcTmp,@NotNull String strSql, String sParas) {
        String sRtn = "";
        try {
            SqlRowSet rs = getSqlRowSet(jdbcTmp, strSql, sParas);
            SqlRowSetMetaData rsmd = rs.getMetaData();
            int icol = rsmd.getColumnCount();
            while (rs.next()) {
                for (int j = 1; j < icol; j++) {
                    sRtn += (rs.getString(j).trim() + ",");
                }
                sRtn += (rs.getString(icol).trim() + ";");
            }
            if (sRtn.length() > 1)
                sRtn = sRtn.substring(0, sRtn.length() - 1);
            return sRtn;
        } catch (Exception e) {
            return "";
        }
    }






}
