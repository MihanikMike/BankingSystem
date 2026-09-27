package com.mike.bank;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    private final AccountRepository repository;

    public BankService(AccountRepository repository){
        this.repository = repository;
    }

    private BankAccount getAccountOrThrow(String accountNumber){
        return findAccount(accountNumber).orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public void addAccount(BankAccount account){
        repository.save(account);
    }

    public Optional<BankAccount> findAccount(String accountNumber) {
        return repository.findByAccountNumber(accountNumber);
    }

    public void deposit(String accountNumber, double amount){
        BankAccount account = getAccountOrThrow(accountNumber);
        account.deposit(amount);
    }

    public void withdraw(String accountNumber, double amount){
        BankAccount account = getAccountOrThrow(accountNumber);
        account.withdraw(amount);

    }

    public void remove(String accountNumber){
        getAccountOrThrow(accountNumber);
        repository.remove(accountNumber);
    }

    public List<BankAccount> getAllAccounts(){
        return repository.findAll();
    }

}
