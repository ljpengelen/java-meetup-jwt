package nl.kabisa.meetup.sessionbased;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

public class RequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set("X-Requested-With", "XMLHttpRequest");
        request.getHeaders().set("Origin", "http://localhost:8000");
        return execution.execute(request, body);
    }
}
