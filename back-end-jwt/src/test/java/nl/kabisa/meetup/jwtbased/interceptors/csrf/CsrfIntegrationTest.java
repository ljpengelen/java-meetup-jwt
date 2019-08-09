package nl.kabisa.meetup.jwtbased.interceptors.csrf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CsrfIntegrationTest {

    private static final String SESSION_PATH = "/session";
    public static final String ORIGIN = "http://localhost";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void noHeaders() {
        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void requestedWithAndOrigin() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Requested-With", "XMLHttpRequest");
        headers.add("Origin", ORIGIN);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void requestedWithAndReferer() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Requested-With", "XMLHttpRequest");
        headers.add("Referer", CsrfIntegrationTest.ORIGIN);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void allHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Requested-With", "XMLHttpRequest");
        headers.add("Origin", ORIGIN);
        headers.add("Referer", ORIGIN);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(SESSION_PATH, HttpMethod.DELETE, entity, Void.class);
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
