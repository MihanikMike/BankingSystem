package com.mike.bank;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    void save(BankAccount account);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    void remove(String accountNumber);

    List<BankAccount> findAll();


}
