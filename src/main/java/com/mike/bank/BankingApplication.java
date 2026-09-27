package com.mike.bank;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingApplication {

    public static void main(String[] args) {

        SpringApplication.run(BankingApplication.class, args);

    }

    @Bean
    CommandLineRunner loadData(BankService bankService){
        return args -> {

            bankService.addAccount(
                    new SavingsAccount("ACC1001", 1000, "Mike")
            );

            bankService.addAccount(
                    new SavingsAccount("ACC1002", 500, "John")
            );
        };
    }
}
