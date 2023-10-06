package client;

import dto.EndpointHit;
import dto.ViewStats;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class StatClient {
    private final RestTemplate restTemplate;

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        HashMap<String, String> params = new HashMap<>();
        params.put("start", start.toString());
        params.put("end", end.toString());
        params.put("unique", unique.toString());
        String uri = "/stats?start={start}&end={end}&unique={unique}";
        if (!uris.isEmpty()) {
            StringBuilder urlBuilder = new StringBuilder(uri);
            for (String uri2 : uris) {
                urlBuilder.append("&uris=").append(uri2);
            }
            uri = urlBuilder.toString();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<ViewStats> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                List.class, params);

        return responseEntity.getBody();
    }

    public ResponseEntity<Object> addHit(EndpointHit hit) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(hit, headers);
        return restTemplate.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
    }
}
