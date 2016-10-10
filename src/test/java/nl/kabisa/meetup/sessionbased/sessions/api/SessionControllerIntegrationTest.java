package nl.kabisa.meetup.sessionbased.sessions.api;

import nl.kabisa.meetup.sessionbased.IntegrationTest;
import nl.kabisa.meetup.sessionbased.accounts.api.AccountDto;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

public class SessionControllerIntegrationTest extends IntegrationTest {

    @Test
    public void loginWithUnknownUsername() {
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LoginStatus.INVALID_CREDENTIALS, response.getBody().getStatus());
    }

    @Test
    @DirtiesContext
    public void loginWithValidCredentials() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), AccountDto.class);
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LoginStatus.LOGGED_IN, response.getBody().getStatus());

        // Assert that the user is indeed logged in

        ResponseEntity<AccountDto> getAccountResponse = testRestTemplate.getForEntity(getAccountUri(), AccountDto.class);
        Assert.assertEquals(HttpStatus.OK, getAccountResponse.getStatusCode());
        Assert.assertEquals("username", getAccountResponse.getBody().getUsername());
    }

    @Test
    @DirtiesContext
    public void loginWithInvalidPassword() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), AccountDto.class);
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "wrong password"), LoginResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LoginStatus.INVALID_CREDENTIALS, response.getBody().getStatus());
    }

    @Test
    @DirtiesContext
    public void logoutWhileLoggedIn() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), AccountDto.class);
        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());
    }

    @Test
    public void logoutWhileLoggedOut() {
        testRestTemplate.delete(getSessionUri());
    }
}
