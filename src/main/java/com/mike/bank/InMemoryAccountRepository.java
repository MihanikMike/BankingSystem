package com.mike.bank;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, BankAccount> accountsMap = new HashMap<>();

    @Override
    public void save(BankAccount account){
        accountsMap.put(account.getAccountNumber(), account);
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber){
        return Optional.ofNullable(accountsMap.get(accountNumber));
    }

    @Override
    public void remove(String accountNumber){
        accountsMap.remove(accountNumber);
    }

    @Override
    public List<BankAccount> findAll(){
        return new ArrayList<>(accountsMap.values());
    }

}
