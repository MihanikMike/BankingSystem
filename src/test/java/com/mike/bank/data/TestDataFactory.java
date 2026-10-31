package com.mike.bank.data;

import com.mike.bank.AmountRequest;
import com.mike.bank.CreateAccountRequest;

import java.util.UUID;

public class TestDataFactory {

    public static String generateAccountNumber(){

        return "ACC-" + UUID.randomUUID()
                .toString()
                .substring(0,8);
    }

    public static CreateAccountRequest createAccountRequest (
            String accountNumber,
            double balance,
            String owner){

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountNumber(accountNumber);
        request.setBalance(balance);
        request.setOwner(owner);

        return request;
    }

    public static AmountRequest createAmountRequest(double amount){
        AmountRequest request = new AmountRequest();
        request.setAmount(amount);

        return request;
    }

}
