package nl.kabisa.meetup.sessionbased.interceptors.authentication;

import nl.kabisa.meetup.sessionbased.IntegrationTest;
import nl.kabisa.meetup.sessionbased.accounts.api.AccountDto;
import nl.kabisa.meetup.sessionbased.sessions.api.LoginRequest;
import nl.kabisa.meetup.sessionbased.sessions.api.LoginResponse;
import nl.kabisa.meetup.sessionbased.sessions.api.LoginStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

public class AuthenticationIntegrationTest extends IntegrationTest {

    @Test
    public void missingRequiredSession() {
        ResponseEntity<AccountDto> response = testRestTemplate.getForEntity(getAccountUri(), AccountDto.class);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void requiredSessionPresent() {
        ResponseEntity<AccountDto> accountToCreate = testRestTemplate.postForEntity(getAccountUri(), new AccountDto("username", "password"), AccountDto.class);
        Assert.assertEquals(HttpStatus.OK, accountToCreate.getStatusCode());

        ResponseEntity<LoginResponse> loginResponse = testRestTemplate.postForEntity(getSessionUri(), new LoginRequest("username", "password"), LoginResponse.class);
        Assert.assertEquals(LoginStatus.LOGGED_IN, loginResponse.getBody().getStatus());

        ResponseEntity<AccountDto> retrievedAccount = testRestTemplate.getForEntity(getAccountUri(), AccountDto.class);
        Assert.assertEquals(HttpStatus.OK, retrievedAccount.getStatusCode());
    }
}
