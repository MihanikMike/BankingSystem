package com.mike.bank.DB;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.math.BigDecimal;


public class DatabaseHelper {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHelper(){

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setUrl("jdbc:postgresql://localhost:5432/banking_db");
        dataSource.setUsername("banking_user");
        dataSource.setPassword(System.getenv().getOrDefault(
                "DB_PASSWORD", "banking_dev_password"
        ));

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public BigDecimal getAccountBalance(String accountNumber){

        String sql = """
                SELECT balance
                FROM accounts
                WHERE account_number = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                accountNumber
        );
    }

}
