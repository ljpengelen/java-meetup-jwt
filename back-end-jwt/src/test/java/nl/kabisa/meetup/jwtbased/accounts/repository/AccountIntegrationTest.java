package nl.kabisa.meetup.jwtbased.accounts.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountIntegrationTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DirtiesContext
    public void nonBlankUsernameAndPassword() {
        accountRepository.save(new Account(USERNAME, PASSWORD));
    }

    @Test(expected = TransactionSystemException.class)
    public void blankUsername() {
        accountRepository.save(new Account("  ", PASSWORD));
    }

    @Test(expected = TransactionSystemException.class)
    public void blankPassword() {
        accountRepository.save((new Account(USERNAME, " ")));
    }

    @Test(expected = DataIntegrityViolationException.class)
    @DirtiesContext
    public void duplicateUsername() {
        accountRepository.save(new Account(USERNAME, PASSWORD));
        accountRepository.save(new Account(USERNAME, PASSWORD));
    }
}
