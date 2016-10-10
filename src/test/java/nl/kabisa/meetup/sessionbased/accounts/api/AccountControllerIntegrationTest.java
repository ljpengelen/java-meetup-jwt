package nl.kabisa.meetup.sessionbased.accounts.api;

import nl.kabisa.meetup.sessionbased.IntegrationTest;
import nl.kabisa.meetup.sessionbased.sessions.api.LoginRequest;
import nl.kabisa.meetup.sessionbased.sessions.api.LoginResponse;
import nl.kabisa.meetup.sessionbased.sessions.api.LoginStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

public class AccountControllerIntegrationTest extends IntegrationTest {

    @Test
    @DirtiesContext
    public void createValidAccount() {
        ResponseEntity<SanitizedAccountDto> account = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, account.getStatusCode());
        Assert.assertNotNull(account.getBody().getId());
        Assert.assertEquals("username", account.getBody().getUsername());
    }

    @Test
    public void createNullAccount() {
        ResponseEntity<AccountDto> account = testRestTemplate.postForEntity(getAccountUri(), null, AccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, account.getStatusCode());
    }

    @Test
    public void createAccountWithInvalidUsername() {
        ResponseEntity<AccountDto> account = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("  ", "password"), AccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, account.getStatusCode());
    }

    @Test
    public void createAccountWithInvalidPassword() {
        ResponseEntity<AccountDto> account = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "   "), AccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, account.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void createDuplicateAccount() {
        ResponseEntity<SanitizedAccountDto> firstAccount = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), SanitizedAccountDto.class);
        ResponseEntity<SanitizedAccountDto> secondAccount = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, firstAccount.getStatusCode());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, secondAccount.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void getAccount() {
        ResponseEntity<SanitizedAccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        ResponseEntity<SanitizedAccountDto> retrievedAccount = testRestTemplate.getForEntity(getAccountUri(), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, retrievedAccount.getStatusCode());
        Assert.assertNotNull(retrievedAccount.getBody().getId());
        Assert.assertEquals("username", retrievedAccount.getBody().getUsername());
    }

    @Test
    @DirtiesContext
    public void deleteAccount() {
        ResponseEntity<AccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), AccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        testRestTemplate.delete(getAccountUri());
    }

    @Test
    @DirtiesContext
    public void updateAccountWithValidUsername() {
        ResponseEntity<SanitizedAccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        testRestTemplate.put(getAccountUri(), new AccountDto("new username", "new password"));

        ResponseEntity<SanitizedAccountDto> retrievedAccount = testRestTemplate.getForEntity(getAccountUri(), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, retrievedAccount.getStatusCode());
        Assert.assertEquals("new username", retrievedAccount.getBody().getUsername());
    }

    @Test
    @DirtiesContext
    public void onlyUpdatePassword() {
        ResponseEntity<SanitizedAccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        testRestTemplate.put(getAccountUri(), new AccountDto("username", "new password"));

        ResponseEntity<SanitizedAccountDto> retrievedAccount = testRestTemplate.getForEntity(getAccountUri(), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, retrievedAccount.getStatusCode());
        Assert.assertEquals("username", retrievedAccount.getBody().getUsername());
    }

    @Test
    @DirtiesContext
    public void updateAccountWithExistingUsername() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username1", "password"), SanitizedAccountDto.class);
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username2", "password"), SanitizedAccountDto.class);

        testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username1", "password"), LoginResponse.class);

        ResponseEntity<SanitizedAccountDto> response = testRestTemplate.exchange(getAccountUri(), HttpMethod.PUT, new HttpEntity<AccountDto>(new AccountDto("username2", "password")), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
