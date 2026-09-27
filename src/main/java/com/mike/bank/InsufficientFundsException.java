package com.mike.bank;

public class InsufficientFundsException extends RuntimeException{

    private final double balance;
    private final double requestedAmount;

    public InsufficientFundsException(double balance, double requestedAmount){
        super("Insufficient funds. Balance: " + balance +
                ", requested: " + requestedAmount);
        this.balance = balance;
        this.requestedAmount = requestedAmount;
    }

    public double getBalance(){
        return balance;
    }
    public double getRequestedAmount(){
        return requestedAmount;
    }

}
