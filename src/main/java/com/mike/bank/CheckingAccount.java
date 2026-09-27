package com.mike.bank;

public class CheckingAccount extends BankAccount {

    public CheckingAccount(String accountNumber, double balance, String owner){
        super(accountNumber, balance, owner);
    }

    @Override
    public void withdraw(double amount){
        System.out.println("Checking account withdrawal");
        super.withdraw(amount);
    }

    @Override
    public double calculateMonthlyFee(){
        return 10.0;
    }

}
