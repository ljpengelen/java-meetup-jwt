package nl.kabisa.meetup.jwtbased.accounts.api;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.*;

import nl.kabisa.meetup.jwtbased.IntegrationTest;
import nl.kabisa.meetup.jwtbased.sessions.api.*;

public class AccountControllerIntegrationTest extends IntegrationTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String OTHER_USERNAME = "new username";
    private static final String OTHER_PASSWORD = "new password";

    @Test
    public void createValidAccount() {
        ResponseEntity<SanitizedAccountDto> account = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, account.getStatusCode());
        Assert.assertNotNull(account.getBody().getId());
        Assert.assertEquals(USERNAME, account.getBody().getUsername());
    }

    @Test
    public void createNullAccount() {
        ResponseEntity<AccountDto> account = testRestTemplate.postForEntity(getAccountUri(), null, AccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, account.getStatusCode());
    }

    @Test
    public void createAccountWithInvalidUsername() {
        ResponseEntity<AccountDto> account = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("  ", PASSWORD), AccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, account.getStatusCode());
    }

    @Test
    public void createAccountWithInvalidPassword() {
        ResponseEntity<AccountDto> account = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, "   "), AccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, account.getStatusCode());
    }

    @Test
    public void createDuplicateAccount() {
        ResponseEntity<SanitizedAccountDto> firstAccount = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), SanitizedAccountDto.class);
        ResponseEntity<SanitizedAccountDto> secondAccount = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, firstAccount.getStatusCode());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, secondAccount.getStatusCode());
    }

    @Test
    public void getAccount() {
        ResponseEntity<SanitizedAccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        ResponseEntity<SanitizedAccountDto> retrievedAccount = testRestTemplate.getForEntity(getAccountUri(), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, retrievedAccount.getStatusCode());
        Assert.assertNotNull(retrievedAccount.getBody().getId());
        Assert.assertEquals(USERNAME, retrievedAccount.getBody().getUsername());
    }

    @Test
    public void deleteAccount() {
        ResponseEntity<AccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), AccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        testRestTemplate.delete(getAccountUri());
    }

    @Test
    public void updateAccountWithValidUsername() {
        ResponseEntity<SanitizedAccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        testRestTemplate.put(getAccountUri(), new AccountDto(OTHER_USERNAME, OTHER_PASSWORD));

        ResponseEntity<SanitizedAccountDto> retrievedAccount = testRestTemplate.getForEntity(getAccountUri(), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, retrievedAccount.getStatusCode());
        Assert.assertEquals(OTHER_USERNAME, retrievedAccount.getBody().getUsername());
    }

    @Test
    public void onlyUpdatePassword() {
        ResponseEntity<SanitizedAccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        testRestTemplate.put(getAccountUri(), new AccountDto(USERNAME, OTHER_PASSWORD));

        ResponseEntity<SanitizedAccountDto> retrievedAccount = testRestTemplate.getForEntity(getAccountUri(), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.OK, retrievedAccount.getStatusCode());
        Assert.assertEquals(USERNAME, retrievedAccount.getBody().getUsername());
    }

    @Test
    public void updateAccountWithExistingUsername() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), SanitizedAccountDto.class);
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto(OTHER_USERNAME, PASSWORD), SanitizedAccountDto.class);

        testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);

        ResponseEntity<SanitizedAccountDto> response = testRestTemplate
                .exchange(getAccountUri(), HttpMethod.PUT, new HttpEntity<>(new AccountDto(OTHER_USERNAME, PASSWORD)), SanitizedAccountDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
