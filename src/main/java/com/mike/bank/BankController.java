package com.mike.bank;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public List<BankAccount> getAllAccounts(){
        return bankService.getAllAccounts();
    }

    @GetMapping("/{accountNumber}")

    public BankAccount getAccount(
            @PathVariable String accountNumber){

        return bankService.findAccount(accountNumber)
                .orElseThrow(
                        () -> new AccountNotFoundException(accountNumber)
                );

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccount createAccount(
            @RequestBody CreateAccountRequest request){

        SavingsAccount account = new SavingsAccount(
                request.getAccountNumber(),
                request.getBalance(),
                request.getOwner()
        );

        bankService.addAccount(account);

        return account;
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String accountNumber){

        bankService.remove(accountNumber);

        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<BankAccount> deposit(
            @PathVariable String accountNumber,
            @RequestBody AmountRequest request) {

        bankService.deposit(accountNumber, request.getAmount());

        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable String accountNumber,
            @RequestBody AmountRequest request) {

        bankService.withdraw(accountNumber, request.getAmount());

        return ResponseEntity.noContent().build();

    }

}
