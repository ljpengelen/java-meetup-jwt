package nl.kabisa.meetup.jwtbased;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

import nl.kabisa.meetup.jwtbased.interceptors.csrf.CsrfInterceptor;

public class RequestInterceptor implements ClientHttpRequestInterceptor {

    private String previousToken;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set("Origin", "http://localhost:8000");

        request.getHeaders().set(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME, previousToken);

        ClientHttpResponse response = execution.execute(request, body);
        previousToken = response.getHeaders().getFirst(CsrfInterceptor.CSRF_TOKEN_HEADER_NAME);

        return response;
    }
}
