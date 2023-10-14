package ru.practicum.util;

import dto.EndpointHit;
import dto.ViewStats;
import lombok.experimental.UtilityClass;
import ru.practicum.model.Hit;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class StatMapper {
    public List<ViewStats> toListView(List<Hit> hits) {
        Map<String, Map<String, Long>> groups = hits.stream()
                .collect(Collectors.groupingBy(Hit::getApp,
                        Collectors.groupingBy(Hit::getUri, Collectors.counting())));

        List<ViewStats> result = new ArrayList<>();
        for (String appName : groups.keySet()) {
            Map<String,Long> appUriCollection = groups.get(appName);
            for (String uriPath : appUriCollection.keySet()) {
                result.add(new ViewStats(appName, uriPath, Math.toIntExact(appUriCollection.get(uriPath))));
            }
        }
        return result;
    }

    public Hit toHit(EndpointHit hit) {
        return new Hit(null, hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }
}
