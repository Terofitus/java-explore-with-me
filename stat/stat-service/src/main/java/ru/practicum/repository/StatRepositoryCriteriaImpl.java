package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatRepositoryCriteriaImpl implements StatRepositoryCriteria {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Hit> getStats(String start, String end, List<String> uris) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Hit> criteriaQuery = criteriaBuilder.createQuery(Hit.class);
        Root<Hit> root = criteriaQuery.from(Hit.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Predicate predicateTime = criteriaBuilder.between(root.get("timestamp"),
                LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter));
        if (uris != null && !uris.isEmpty()) {
            Predicate predicate = null;
            for (String uri : uris) {
                Predicate predicateUri = criteriaBuilder.equal(root.get("uri"), uri);
                if (predicate != null) {
                    predicate = criteriaBuilder.or(predicate, predicateUri);
                } else {
                    predicate = predicateUri;
                }
            }
            predicateTime = criteriaBuilder.and(predicateTime, predicate);
        }
        criteriaQuery.where(predicateTime);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
