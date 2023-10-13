package ru.practicum.client;

import client.StatClient;
import dto.EndpointHit;
import dto.ViewStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class Client {
    private final StatClient statClient;

    @Autowired
    public Client(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        statClient = new StatClient(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> addHit(HttpServletRequest request) {
        return statClient.addHit(new EndpointHit(null, "ewm-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now().toString()));
    }

    public List<ViewStats> gets(List<String> uris, Boolean unique) {
        return statClient.getStats(LocalDateTime.MIN, LocalDateTime.now(), uris, unique);
    }
}
