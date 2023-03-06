package project.telegram.weatherbot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
@Slf4j
public class Sender {

    private static final String HTTPS = "https";

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public Sender(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public <T> ResponseEntity<T> sendWithParams(String host, String path, Map<String, String> params,
                                                Map<String, String> headers, HttpMethod method,
                                                Class<T> responseClass) throws Exception {
        SenderContext context = SenderContext.builder()
                .httpMethod(method)
                .host(host)
                .path(path)
                .params(params)
                .headers(headers)
                .build();
        return send(context, responseClass, null);
    }

    public <T, K> ResponseEntity<T> send(SenderContext senderContext, Class<T> responseClass, K requestBody) throws Exception {

        URIBuilder builder = new URIBuilder().setScheme(HTTPS)
                .setHost(senderContext.host)
                .setPath(senderContext.path);
        if (senderContext.port != null) {
            builder.setPort(senderContext.port);
        }
        addParams(builder, senderContext.params);
        URI uri = builder.build();
        HttpHeaders httpHeaders = new HttpHeaders();
        addHeaders(httpHeaders, senderContext.headers);
        try {
            if (requestBody != null) {
                HttpEntity<K> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
                return restTemplate.exchange(uri, senderContext.httpMethod, requestEntity, responseClass);
            } else {
                HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
                return restTemplate.exchange(uri, senderContext.httpMethod, requestEntity, responseClass);
            }
        }  catch (HttpStatusCodeException e) {
            log.warn(e.getMessage(), e);
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(mapper.readValue(e.getResponseBodyAsString(), responseClass));
        }
    }

    private void addHeaders(HttpHeaders httpHeaders, Map<String, String> headers){
        if (headers != null) {
            for (String name : headers.keySet()) {
                httpHeaders.add(name, headers.get(name));
            }
        }
    }

    private void addParams(URIBuilder builder, Map<String, String> params) {
        if (params != null) {
            for (String name : params.keySet()) {
                builder = builder.addParameter(name, params.get(name));
            }
        }
    }

    @Builder
    public static class SenderContext {
        private String host;
        private String path;
        private Integer port;
        private Map<String, String> params;
        private Map<String, String> headers;
        private HttpMethod httpMethod;
    }

}
