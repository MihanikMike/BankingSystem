package com.mike.bank;

public class SavingsAccount extends BankAccount {

    public SavingsAccount(String accountNumber, double balance, String owner){
        super(accountNumber, balance, owner);
    }

    public void addInterest(){
        double interest = getBalance() * 0.05;
        deposit(interest);
    }

    @Override
    public double calculateMonthlyFee(){
        return 5.0;
    }

}
