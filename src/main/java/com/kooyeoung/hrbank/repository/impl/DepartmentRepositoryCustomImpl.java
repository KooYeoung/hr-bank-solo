package com.kooyeoung.hrbank.repository.impl;

import com.kooyeoung.hrbank.dto.repository.department.DepartmentSearchCondition;
import com.kooyeoung.hrbank.dto.repository.department.DepartmentSummary;
import com.kooyeoung.hrbank.repository.DepartmentRepositoryCustom;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.kooyeoung.hrbank.entity.QDepartment.department;
import static com.kooyeoung.hrbank.entity.QEmployee.employee;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryCustomImpl implements DepartmentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DepartmentSummary> searchDepartment(DepartmentSearchCondition condition) {

        return buildDepartmentSummaryQuery(containsNameOrDescription(condition.keyword())
                , cursorCondition(condition))
                .orderBy(orderSpecifier(condition), idOrderSpecifier(condition))
                .limit(condition.size()+1)
                .fetch();
    }

    @Override
    public Optional<DepartmentSummary> findSummaryById(Long id) {
        return Optional
                .ofNullable(buildDepartmentSummaryQuery(department.id.eq(id))
                        .fetchOne()
                );
    }

    @Override
    public long countDepartment(DepartmentSearchCondition condition) {
        Long totalDepartments = jpaQueryFactory
                .select(department.id.count())
                .from(department)
                .where(containsNameOrDescription(condition.keyword()))
                .fetchOne();

        return totalDepartments == null ? 0 : totalDepartments;
    }

    private JPAQuery<DepartmentSummary> buildDepartmentSummaryQuery(Predicate... predicates) {
        return jpaQueryFactory.
                select(
                        Projections.constructor(
                                DepartmentSummary.class
                                , department.id
                                , department.name
                                , department.description
                                , department.establishedDate
                                , employee.id.count()
                        )
                )
                .from(department)
                .leftJoin(employee).on(employee.department.id.eq(department.id))
                .where(predicates)
                .groupBy(
                        department.id
                        ,department.name
                        ,department.description
                        ,department.establishedDate
                );
    }

    @NonNull
    private OrderSpecifier<?> orderSpecifier(DepartmentSearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;

        if("establishedDate".equals(condition.sortField())){
            return new OrderSpecifier<>(order, department.establishedDate);
        }
        return new OrderSpecifier<>(order, department.name);
    }

    @NonNull
    private OrderSpecifier<?> idOrderSpecifier(DepartmentSearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;

        return new OrderSpecifier<>(order, department.id);
    }

    @Nullable
    private BooleanExpression cursorCondition(DepartmentSearchCondition condition) {
        if(!condition.hasCursor()){
            return null;
        }

        String sortField = condition.sortField();
        boolean desc = condition.isDesc();
        String cursor = condition.cursor();

        if("establishedDate".equals(sortField)){
            LocalDate localDate = LocalDate.parse(cursor);

            if(desc){
                return department.establishedDate.lt(localDate)
                        .or(department.establishedDate.eq(localDate)
                                .and(department.id.lt(condition.idAfter()))
                        );
            }

            return department.establishedDate.gt(localDate)
                    .or(department.establishedDate.eq(localDate)
                            .and(department.id.gt(condition.idAfter())));
        }

        if(desc){
            return department.name.lt(cursor)
                    .or(department.name.eq(cursor)
                            .and(department.id.lt(condition.idAfter())));
        }

        return department.name.gt(cursor)
                .or(department.name.eq(cursor)
                        .and(department.id.gt(condition.idAfter())));
    }

    private BooleanExpression containsNameOrDescription(String keyword) {
        if(keyword  == null || keyword.isBlank()){
            return null;
        }

        return department.name.containsIgnoreCase(keyword)
                        .or(department.description.containsIgnoreCase(keyword));

    }
}
