package nl.kabisa.meetup.sessionbased.accounts.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountRepositoryIntegrationTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DirtiesContext
    public void findExistingAccountByUsername() {
        Account accountToCreate = new Account(USERNAME, PASSWORD);
        accountRepository.save(accountToCreate);

        Account foundAccount = accountRepository.findByUsername(USERNAME);
        Assert.assertEquals(accountToCreate, foundAccount);
    }

    @Test
    public void findNonExistingAccountByUsername() {
        Account account = accountRepository.findByUsername(USERNAME);
        Assert.assertNull(account);
    }
}
