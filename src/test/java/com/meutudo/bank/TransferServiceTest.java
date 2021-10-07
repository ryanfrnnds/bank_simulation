package com.meutudo.bank;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AccountServiceTest {

    @TestConfiguration
    static class AccountServiceTestConfiguration() {

        @Bean
        public AccountService accountService() {
            return new AccountService();
        }
    }

    @Autowired
    AccountService accountService;

}
