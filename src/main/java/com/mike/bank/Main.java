package com.mike.bank;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        AccountRepository repository = new InMemoryAccountRepository();
       BankService bankService = new BankService(repository);

       bankService.addAccount(
               new SavingsAccount("ACC1001", 1000, "Mike")
       );
       bankService.addAccount(
               new CheckingAccount("ACC1002", 2000, "John")
       );
        bankService.addAccount(
                new SavingsAccount("ACC1003", 3000, "Igor")
        );

        List<BankAccount> accounts = bankService.getAllAccounts();

        for(BankAccount account : accounts){
            System.out.println(
                    account.getAccountNumber() + " " +
                            account.getOwner() + " " +
                            account.getBalance()
            );
        }

        try {
            bankService.withdraw("ACC1001", 1200);
            bankService.withdraw("ACC1099", 300);
            bankService.withdraw("ACC1001", -500);
        } catch (AccountNotFoundException e) {
            System.out.println("Account problem: " + e.getAccountNumber());

        } catch (InsufficientFundsException e) {
            System.out.println("Balance problem: " + e.getBalance());

        } catch (IllegalArgumentException e){
            System.out.println("Invalid input: " + e.getMessage());
        } finally {
            System.out.println("Transaction attempt finished");
        }

    }
}
