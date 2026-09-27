package com.mike.bank.unit;

import com.mike.bank.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankServiceMockitoTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private BankService bankService;

    @Test
    void findAccountShouldReturnAccountWhenRepositoryFindsIt(){

        SavingsAccount savingsAccount = new SavingsAccount("ACC1001", 1000, "Mike");

        when(repository.findByAccountNumber("ACC1001")).
                thenReturn(Optional.of(savingsAccount));

        Optional <BankAccount> result =
                bankService.findAccount("ACC1001");

        assertTrue(result.isPresent());
        assertEquals("ACC1001", result.get().getAccountNumber());
        verify(repository).findByAccountNumber("ACC1001");
    }

    @Test
    void findAccountShouldReturnEmptyWhenRepositoryDoesNotFindAccount(){

        when(repository.findByAccountNumber("ACC9999"))
                .thenReturn(Optional.empty());

        Optional <BankAccount> result =
                bankService.findAccount("ACC9999");

        assertTrue(result.isEmpty());
        verify(repository).findByAccountNumber("ACC9999");
    }

    @Test
    void addAccountShouldSaveAccountToRepository(){

        SavingsAccount savingsAccount =
                new SavingsAccount("ACC1002", 500, "John");

        bankService.addAccount(savingsAccount);

        verify(repository).save(savingsAccount);
    }

    @Test
    void removeShouldThrowWhenAccountDoesNotExist(){

        when(repository.findByAccountNumber("ACC9999"))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> bankService.remove("ACC9999"));

        verify(repository, never()).remove("ACC9999");
        verify(repository).findByAccountNumber("ACC9999");

        verifyNoMoreInteractions(repository);
    }

    @Test
    void removeShouldRemoveAccountWhenAccountExists(){

        SavingsAccount savingsAccount =
                new SavingsAccount("ACC1000", 1000, "Mike");

        when(repository.findByAccountNumber("ACC1000"))
                .thenReturn(Optional.of(savingsAccount));

        bankService.remove("ACC1000");

        verify(repository).findByAccountNumber("ACC1000");
        verify(repository).remove("ACC1000");

        verifyNoMoreInteractions(repository);
    }

    @Test
    void addAccountShouldPassCorrectAccountToRepository(){

        SavingsAccount savingsAccount = new SavingsAccount("ACC1003", 750, "Alex");

        bankService.addAccount(savingsAccount);

        ArgumentCaptor<BankAccount> captor =
                ArgumentCaptor.forClass(BankAccount.class);

        verify(repository).save(captor.capture());

        BankAccount capturedAccount =
                captor.getValue();

        assertEquals("ACC1003", capturedAccount.getAccountNumber());
        assertEquals(750.0, capturedAccount.getBalance());
        assertEquals("Alex", capturedAccount.getOwner());

    }

    @Test
    void removeShouldPropagateExceptionWhenRepositoryRemoveFails(){

        SavingsAccount savingsAccount =
                new SavingsAccount("ACC1000", 1000, "Mike");

        when(repository.findByAccountNumber("ACC1000"))
                .thenReturn(Optional.of(savingsAccount));

        doThrow(new RuntimeException("Database error"))
                .when(repository)
                .remove("ACC1000");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bankService.remove("ACC1000"));

        assertEquals("Database error", exception.getMessage());

        verify(repository).findByAccountNumber("ACC1000");
        verify(repository).remove("ACC1000");

    }

    @Test
    void removeShouldNotCallRemoveWhenRepositorySearchFails(){

        when(repository.findByAccountNumber("ACC1004"))
                .thenThrow(new RuntimeException("Database connection failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankService.remove("ACC1004")
        );

        assertEquals("Database connection failed",
                exception.getMessage());

        verify(repository).findByAccountNumber("ACC1004");
        verify(repository, never()).remove("ACC1004");

        verifyNoMoreInteractions(repository);
    }

}
