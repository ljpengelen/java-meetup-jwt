package nl.kabisa.meetup.sessionbased.interceptors.csrf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CsrfIntegrationTest {

    private static final String SESSION_PATH = "/session";

    @Value("${csrf.target}")
    private String origin;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void noHeaders() {
        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void originHeader() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Requested-With", "XMLHttpRequest");
        headers.add("Origin", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void refererHeader() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Referer", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void allHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Origin", origin);
        headers.add("Referer", origin);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
