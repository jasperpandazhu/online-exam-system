package com.official.OfficialProject.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataBaseConfig {

    private final String url = "jdbc:sqlserver://";
    private final String serverName = "localhost";
    private final String portNumber = "1433";
    private final String databaseName = "OfficialProject";
    private final String userName = "root";
    private final String password = "12345678";
    private final String selectMethod = "cursor";

    @Bean(name = "dsSqlServer")
    @Primary
//    @ConfigurationProperties(prefix="spring.datasources.db1")
    public DataSource sqlServerDataSource() {
        final HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(10);
        ds.setDataSourceClassName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
//        ds.addDataSourceProperty("serverName",this.serverName);
//        ds.addDataSourceProperty("databaseName",this.databaseName);
        ds.addDataSourceProperty("url",this.getConnectionUrl());
//        ds.addDataSourceProperty("user",this.userName);
//        ds.addDataSourceProperty("password",this.password);
        ds.setInitializationFailFast(true);
        ds.setPoolName("dsSqlServer");
        return ds;
        //return DataSourceBuilder.create().build();
    }

    public String getConnectionUrl(){
        return url + this.serverName + ":" + this.portNumber + ";databaseName="+this.databaseName +";user="+this.userName + ";password=" + this.password + ";selectMethod="+ this.selectMethod + ";";
    }

//    @Bean(name = "dsMySql")
//    @ConfigurationProperties(prefix="spring.datasources.db2")
//    public DataSource mySqlDataSource() {
//        return DataSourceBuilder.create().build();
//    }

    @Bean(name = "jdbcSqlServer")
    public JdbcTemplate sqlServerJdbcTemplate(@Qualifier("dsSqlServer") DataSource dsSqlServer) {
        return new JdbcTemplate(dsSqlServer);
    }

//    @Bean(name = "jdbcMySql")
//    public JdbcTemplate mySqlJdbcTemplate(@Qualifier("dsMySql") DataSource dsMySql) {
//        return new JdbcTemplate(dsMySql);
//    }
    ///
    // #### simple way  In case needed
    ///
//    @Bean(name = "tmpSqlServer")
//    public JdbcTemplate sqlServerJdbcTemplate() {
//        return new JdbcTemplate(sqlServerDataSource());
//    }
//
//    @Bean(name = "tmpjMySql")
//    public JdbcTemplate mySqlJdbcTemplate() {
//        return new JdbcTemplate(mySqlDataSource());
//    }

    @Bean(name="NameParaTmpSqlServer")
    public NamedParameterJdbcTemplate sqlServerNamedParaJdbcTemplate(@Qualifier("dsSqlServer") DataSource dsSqlServer) {
        return new NamedParameterJdbcTemplate(dsSqlServer);
    }

//    @Bean(name="NameParaTmpMySql")
//    public NamedParameterJdbcTemplate mySqlNamedParaJdbcTemplate(@Qualifier("dsMySql") DataSource dsMySql) {
//        return new NamedParameterJdbcTemplate(dsMySql);
//    }
}
