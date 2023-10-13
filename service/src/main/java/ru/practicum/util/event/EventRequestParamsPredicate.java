package ru.practicum.util.event;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.experimental.UtilityClass;
import ru.practicum.model.EventRequestParam;
import ru.practicum.model.QEvent;
import ru.practicum.util.QPredicates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@UtilityClass
public class EventRequestParamsPredicate {
    public Predicate getPredicate(EventRequestParam params) {
        List<Predicate> predicates = new ArrayList<>();

        if (params.getText() != null) {
            String text = params.getText();
            BooleanExpression predicate = QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text));
            predicates.add(predicate);
        }

        if (params.getCategories() != null) {
            Set<Integer> categories = params.getCategories();
            BooleanExpression predicate = QEvent.event.category.id.in(categories);
            predicates.add(predicate);
        }

        if (params.getPaid() != null) {
            boolean paid = params.getPaid();
            BooleanExpression predicate = QEvent.event.paid.eq(paid);
            predicates.add(predicate);
        }

        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            LocalDateTime start = params.getRangeStart();
            LocalDateTime end = params.getRangeEnd();
            BooleanExpression predicate = QEvent.event.eventDate.between(start, end);
            predicates.add(predicate);
        } else {
            BooleanExpression predicate = QEvent.event.eventDate.after(LocalDateTime.now());
            predicates.add(predicate);
        }

        if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
            BooleanExpression predicate = QEvent.event.participantLimit.gt(QEvent.event.participants.size());
            predicates.add(predicate);
        }
        return QPredicates.builder().addAll(predicates).buildAnd();
    }
}
