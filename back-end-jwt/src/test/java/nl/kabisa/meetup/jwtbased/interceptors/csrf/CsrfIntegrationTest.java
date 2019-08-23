package nl.kabisa.meetup.jwtbased.interceptors.csrf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import nl.kabisa.meetup.jwtbased.sessions.api.StatusResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CsrfIntegrationTest {

    @Value("${csrf.target}")
    private String origin;

    @LocalServerPort
    private long port;

    private TestRestTemplate testRestTemplate;

    public CsrfIntegrationTest() {
        testRestTemplate = new TestRestTemplate(new RestTemplate(), null, null, TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
    }

    private String getSessionUri() {
        return "http://localhost:" + port + "/session";
    }

    @Test
    public void noHeadersNoToken() {
        ResponseEntity<Void> response = testRestTemplate.exchange(getSessionUri(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void tokenAndOrigin() {
        ResponseEntity<StatusResponse> sessionStatusResponse = testRestTemplate.getForEntity(getSessionUri(), StatusResponse.class);
        String token = sessionStatusResponse.getHeaders().getFirst(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME, token);
        headers.add("Origin", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(getSessionUri(), HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void tokenAndReferer() {
        ResponseEntity<StatusResponse> sessionStatusResponse = testRestTemplate.getForEntity(getSessionUri(), StatusResponse.class);
        String token = sessionStatusResponse.getHeaders().getFirst(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME, token);
        headers.add("Referer", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> logOutResponse = testRestTemplate.exchange(getSessionUri(), HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, logOutResponse.getStatusCode());
    }

    @Test
    public void originWithoutToken() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Origin", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(getSessionUri(), HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void refererWithoutToken() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Referer", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(getSessionUri(), HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void wrongToken() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME, "BS");
        headers.add("Origin", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(getSessionUri(), HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
