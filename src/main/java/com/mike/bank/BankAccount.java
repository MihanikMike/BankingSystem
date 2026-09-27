package com.mike.bank;

public abstract class BankAccount {

    private String owner;
    private double balance;
    private String accountNumber;

    public BankAccount(String accountNumber, double balance, String owner){
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = balance;
    }

    public String getOwner(){
        return owner;
    }
    public double getBalance(){
        return balance;
    }
    public String getAccountNumber(){
        return accountNumber;
    }

    public void deposit(double amount){
        if(amount > 0){
            balance += amount;
        }else{
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        }
    }

    public void withdraw(double amount){
        if(amount <= 0){
            throw new IllegalArgumentException(
                    "Withdraw amount must be greater than 0");
        }else if(amount > balance){
            throw new InsufficientFundsException(balance, amount);
        }else{
            balance -= amount;
        }
    }

    public abstract double calculateMonthlyFee();

    public void chargeMonthlyFee(){
        double fee = calculateMonthlyFee();
        withdraw(fee);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(!(obj instanceof BankAccount)){
            return false;
        }
        BankAccount other = (BankAccount) obj;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode(){
        return accountNumber.hashCode();
    }

}
