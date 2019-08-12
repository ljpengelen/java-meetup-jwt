package nl.kabisa.meetup.jwtbased.sessions.api;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.kabisa.meetup.jwtbased.IntegrationTest;
import nl.kabisa.meetup.jwtbased.accounts.api.AccountDto;

public class SessionControllerIntegrationTest extends IntegrationTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Test
    public void loginWithUnknownUsername() {
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LoginStatus.INVALID_CREDENTIALS, response.getBody().getStatus());
    }

    @Test
    public void loginWithValidCredentials() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), AccountDto.class);
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LoginStatus.LOGGED_IN, response.getBody().getStatus());

        // Assert that the user is indeed logged in

        ResponseEntity<AccountDto> getAccountResponse = testRestTemplate.getForEntity(getAccountUri(), AccountDto.class);
        Assert.assertEquals(HttpStatus.OK, getAccountResponse.getStatusCode());
        Assert.assertEquals(USERNAME, getAccountResponse.getBody().getUsername());
    }

    @Test
    public void loginWithInvalidPassword() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), AccountDto.class);
        ResponseEntity<LoginResponse> response = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, "wrong password"), LoginResponse.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(LoginStatus.INVALID_CREDENTIALS, response.getBody().getStatus());
    }

    @Test
    public void logoutWhileLoggedIn() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), AccountDto.class);
        testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);

        testRestTemplate.delete(getSessionUri());
    }

    @Test
    public void logoutWhileLoggedOut() {
        testRestTemplate.delete(getSessionUri());
    }

    @Test
    public void getStatusWhileLoggedIn() {
        testRestTemplate.postForEntity(getAccountUri(), new AccountDto(USERNAME, PASSWORD), AccountDto.class);
        testRestTemplate.postForEntity(getSessionUri(), new LoginRequest(USERNAME, PASSWORD), LoginResponse.class);

        ResponseEntity<StatusResponse> statusResponse = testRestTemplate.getForEntity(getSessionUri(), StatusResponse.class);
        Assert.assertEquals(SessionStatus.LOGGED_IN, statusResponse.getBody().getStatus());
    }

    @Test
    public void getStatusWhileLoggedOut() {
        ResponseEntity<StatusResponse> statusResponse = testRestTemplate.getForEntity(getSessionUri(), StatusResponse.class);
        Assert.assertEquals(SessionStatus.LOGGED_OUT, statusResponse.getBody().getStatus());
    }
}
