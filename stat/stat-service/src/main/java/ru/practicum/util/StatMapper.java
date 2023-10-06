package ru.practicum.util;

import dto.EndpointHit;
import dto.ViewStats;
import lombok.experimental.UtilityClass;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@UtilityClass
public class StatMapper {
    public List<ViewStats> toListViewNotUnique(List<Hit> hits, Boolean unique) {
        Map<String, Integer> urisStat = new HashMap<>();
        hits.forEach(hit -> urisStat.put(hit.getUri(), urisStat.getOrDefault(hit.getUri(), 0) + 1));
        List<ViewStats> viewList = new ArrayList<>();
        Set<String> uris = urisStat.keySet();
        int count = 0;
        if (unique != null && unique) {
            Set<String> ipStat = new HashSet<>();

            for (Hit hit : hits) {
                ipStat.add(hit.getIp());
            }
            count = ipStat.size();
        }
        for (String key: uris) {
            viewList.add(new ViewStats("ewm-main-service", key,
                    unique != null && unique? count: urisStat.get(key)));
        }
        return viewList;
    }

    public Hit toHit(EndpointHit hit) {
        return new Hit(null, hit.getApp(), hit.getUri(), hit.getIp(), LocalDateTime.parse(hit.getTimestamp(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss")));
    }
}
