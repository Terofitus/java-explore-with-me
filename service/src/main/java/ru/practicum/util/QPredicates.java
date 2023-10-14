package ru.practicum.util;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import dto.EventState;
import lombok.experimental.UtilityClass;
import ru.practicum.model.QCategory;
import ru.practicum.model.QEvent;
import ru.practicum.model.model_attribute.AdminEventSearchParam;
import ru.practicum.model.model_attribute.EventRequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class QPredicates {
    public Predicate eventRequestParamPredicate(EventRequestParam params) {
        List<Predicate> predicates = new ArrayList<>();

        if (params.getText() != null) {
            String text = params.getText();
            BooleanExpression predicate = QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text));
            predicates.add(predicate);
        }

        if (params.getCategories() != null) {
            List<Integer> categories = params.getCategories();
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
            BooleanExpression predicate2 = QEvent.event.participantLimit.eq(0);
            predicates.add(ExpressionUtils.anyOf(predicate, predicate2));
        }

        BooleanExpression predicate = QEvent.event.stateEnum.eq(EventState.PUBLISHED);
        predicates.add(predicate);

        return ExpressionUtils.allOf(predicates);
    }

    public Predicate deleteCategoryPredicate(Integer id) {
        BooleanExpression idPredicate = QCategory.category.id.eq(id);
        BooleanExpression noBindPredicate = QCategory.category.notIn(QEvent.event.category);
        return ExpressionUtils.allOf(idPredicate, noBindPredicate);
    }

    public Predicate adminEventSearchPredicate(AdminEventSearchParam params) {
        List<Predicate> predicates = new ArrayList<>();

        if (params.getUsers() != null) {
            BooleanExpression predicate = QEvent.event.initiator.id.in(params.getUsers());
            predicates.add(predicate);
        }

        if (params.getStates() != null) {
            BooleanExpression predicate = QEvent.event.stateEnum.in(params.getStates());
            predicates.add(predicate);
        }

        if (params.getCategories() != null) {
            BooleanExpression predicate = QEvent.event.category.id.in(params.getCategories());
            predicates.add(predicate);
        }

        if (params.getUsers() != null) {
            BooleanExpression predicate = QEvent.event.initiator.id.in(params.getUsers());
            predicates.add(predicate);
        }

        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            LocalDateTime start = params.getRangeStart();
            LocalDateTime end = params.getRangeEnd();
            BooleanExpression predicate = QEvent.event.eventDate.between(start, end);
            predicates.add(predicate);
        }

        return ExpressionUtils.allOf(predicates);
    }
}
