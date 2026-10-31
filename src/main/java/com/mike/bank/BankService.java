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

    public void transfer(String fromAccount,
                         String toAccount, double amount){

        if(!Double.isFinite(amount)){
            throw new IllegalArgumentException(
                    "Transfer amount must be finite"
            );
        }

        if(amount <= 0){
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than 0"
            );
        }

        if(fromAccount.equals(toAccount)){
            throw new IllegalArgumentException(
                    "Cannot transfer to the same account"
            );
        }

        BankAccount sender = getAccountOrThrow(fromAccount);
        BankAccount receiver = getAccountOrThrow(toAccount);

        if(amount > sender.getBalance()){
            throw new InsufficientFundsException(sender.getBalance(), amount);
        }

        sender.withdraw(amount);
        receiver.deposit(amount);

    }
}
