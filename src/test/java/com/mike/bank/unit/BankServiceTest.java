package com.mike.bank.unit;
import static org.junit.jupiter.api.Assertions.*;

import com.mike.bank.*;
import org.junit.jupiter.api.*;
import java.util.Optional;

public class BankServiceTest {

    private BankService bankService;

    @BeforeEach
    void setUp(){

        AccountRepository repository = new InMemoryAccountRepository();

        bankService = new BankService(repository);
        bankService.addAccount(
                new SavingsAccount("ACC1001", 1000, "Mike")
        );
        bankService.addAccount(
                new SavingsAccount("ACC1002", 1000, "Mike")
        );
    }

    @Test
    void withdrawShouldThrowExceptionWhenAccountDoesNotExist(){
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> bankService.withdraw("ACC9099", 100)
        );

        assertEquals("ACC9099", exception.getAccountNumber());
    }

    @Test
    void withdrawShouldDecreaseBalance(){
        bankService.withdraw("ACC1001", 300);
        BankAccount account = bankService.findAccount("ACC1001").get();
        assertEquals(700, account.getBalance(), 0.0001);
    }

    @Test
    void depositShouldIncreaseBalance(){
        bankService.deposit("ACC1001", 500);
        BankAccount account = bankService.findAccount("ACC1001").get();
        assertEquals(1500, account.getBalance(), 0.0001);
    }

    @Test
    @DisplayName("Should return account when account exists")
    void findAccountShouldReturnAccountWhenAccountExists(){

        Optional <BankAccount> result =
                bankService.findAccount("ACC1001");

        assertTrue(result.isPresent());
        assertEquals("ACC1001", result.get().getAccountNumber());
    }

    @Test
    @DisplayName("Should return empty Optional when account does not exist")
    void findAccountShouldReturnEmptyOptionalWhenAccountDoesNotExist(){

        Optional <BankAccount> result =
                bankService.findAccount("ACC1099");

        assertTrue(result.isEmpty());
        //assertFalse(result.isPresent());

    }

}
