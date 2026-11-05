package com.mike.bank;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class PostgresAccountRepository implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgresAccountRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(BankAccount account){

        String sql = """
                INSERT INTO accounts (account_number, owner, balance)
                VALUES (?,?,?)""";

        jdbcTemplate.update(
                sql,
                account.getAccountNumber(),
                account.getOwner(),
                account.getBalance()
        );
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber){

        String sql = """
                SELECT account_number, owner, balance
                FROM accounts
                WHERE account_number = ?
                """;

        List<BankAccount> accounts = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SavingsAccount(
                        rs.getString("account_number"),
                        rs.getDouble("balance"),
                        rs.getString("owner")
                ),
                accountNumber
        );
        return accounts.stream().findFirst();
    }

    @Override
    public void remove(String accountNumber){

        String sql = """
                DELETE FROM accounts
                WHERE account_number = ?
                """;

        jdbcTemplate.update(sql, accountNumber);
    }

    @Override
    public List<BankAccount> findAll(){

        String sql = """
                SELECT account_number, owner, balance
                FROM accounts
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SavingsAccount(
                        rs.getString("account_number"),
                        rs.getDouble("balance"),
                        rs.getString("owner")
                )
        );
    }

    @Override
    public void updateBalance(BankAccount account){

        String sql = """
                UPDATE accounts
                SET balance = ?
                WHERE account_number = ?
                """;

        jdbcTemplate.update(
                sql,
                account.getBalance(),
                account.getAccountNumber()
        );
    }
}
