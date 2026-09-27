package com.mike.bank.unit;

import com.mike.bank.InsufficientFundsException;
import com.mike.bank.SavingsAccount;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SavingsAccountTest {

    private SavingsAccount savingsAccount;

    @BeforeEach
    void setUp(){
        savingsAccount =
                new SavingsAccount("ACC1000", 1000, "Mike");
    }

    @Nested
    @DisplayName("Deposit tests")
    class DepositTests{

        @Test
        void depositShouldIncreaseBalance(){
            savingsAccount.deposit(500);
            assertEquals(1500, savingsAccount.getBalance(), 0.0001);
        }

        @Test
        void depositShouldThrowExceptionForNegativeAmount(){

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> savingsAccount.deposit(-100)
            );
            assertEquals(
                    "Deposit amount must be greater than 0",
                    exception.getMessage()
            );
        }

        @ParameterizedTest
        @ValueSource(doubles = {-100, 0, -500, -0.01})
        void depositShouldRejectInvalidAmount(double amount){

            assertThrows(
                    IllegalArgumentException.class,
                    () -> savingsAccount.deposit(amount)
            );
        }


    }

    @Nested
    class WithdrawTests{

        @Test
        void withdrawShouldThrowExceptionWhenFundsAreInsufficient(){

            InsufficientFundsException exception = assertThrows(
                    InsufficientFundsException.class,
                    () -> savingsAccount.withdraw(1200)
            );
            assertEquals(1000, exception.getBalance(), 0.0001);
            assertEquals(1200, exception.getRequestedAmount(), 0.0001);
        }

    }

    @AfterEach
    void tearDown(){
        System.out.println("Test finished");
    }

    @Test
    void accountShouldHaveCorrectInitialData(){
        assertAll(
                () -> assertEquals(1000, savingsAccount.getBalance()),
                () -> assertEquals("Mike", savingsAccount.getOwner()),
                () -> assertEquals("ACC1000", savingsAccount.getAccountNumber())
        );
    }

}
