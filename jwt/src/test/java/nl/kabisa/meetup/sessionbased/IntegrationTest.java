package nl.kabisa.meetup.sessionbased;

import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract public class IntegrationTest {

    @LocalServerPort
    private long port;

    private static final String ACCOUNT_PATH = "/account";
    private static final String SESSION_PATH = "/session";

    protected TestRestTemplate testRestTemplate;

    public IntegrationTest() {
        RestTemplate restTemplate = getRestTemplateWithInterceptor();
        testRestTemplate = new TestRestTemplate(restTemplate, null, null, TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
    }

    private RestTemplate getRestTemplateWithInterceptor() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Arrays.asList(new RequestInterceptor()));
        return restTemplate;
    }

    protected String getAccountUri() {
        return getUriForPath(ACCOUNT_PATH);
    }

    protected String getSessionUri() {
        return getUriForPath(SESSION_PATH);
    }

    private String getUriForPath(String path) {
        return "http://localhost:" + port + path;
    }
}
