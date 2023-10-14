package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StatRepositoryCriteriaImpl implements StatRepositoryCriteria {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Hit> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Hit> hitCriteriaQuery = criteriaBuilder.createQuery(Hit.class);
        Root<Hit> hitRoot = hitCriteriaQuery.from(Hit.class);

        Predicate criteria = criteriaBuilder.conjunction();

        Predicate predicateDateTime = criteriaBuilder.between(hitRoot.get("timestamp"), start, end);
        criteria = criteriaBuilder.and(criteria, predicateDateTime);

        if (uris != null && !uris.isEmpty()) {
            List<String> uriCollection = uris.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.toList());
            Predicate predicateUri = criteriaBuilder.isTrue(hitRoot.get("uri").in(uriCollection));
            criteria = criteriaBuilder.and(criteria, predicateUri);
        }

        hitCriteriaQuery.where(criteria);
        if (unique != null && unique) {
            hitCriteriaQuery.select(hitRoot.get("ip")).distinct(true);
        }
        TypedQuery<Hit> hitQuery = entityManager.createQuery(hitCriteriaQuery);
        return hitQuery.getResultList();
    }
}
