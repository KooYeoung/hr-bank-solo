package com.kooyeoung.hrbank.repository.impl;

import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySearchCondition;
import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySummary;
import com.kooyeoung.hrbank.entity.HistoryType;
import com.kooyeoung.hrbank.repository.EmployeeHistoryRepositoryCustom;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.kooyeoung.hrbank.entity.QEmployeeHistory.employeeHistory;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeHistoryRepositoryCustomImpl implements EmployeeHistoryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<EmployeeHistorySummary> searchEmployeeHistory(EmployeeHistorySearchCondition condition) {


        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                EmployeeHistorySummary.class
                                ,employeeHistory.id
                                ,employeeHistory.type.stringValue()
                                ,employeeHistory.memo
                                ,employeeHistory.ipAddress
                                ,employeeHistory.createdAt
                        )
                ).from(employeeHistory)
                .where(
                        getStringPathPredicate(employeeHistory.employeeNumber,condition.employeeNumber())
                        ,getStringPathPredicate(employeeHistory.memo,condition.memo())
                        ,getStringPathPredicate(employeeHistory.ipAddress,condition.ipAddress())
                        ,getHistoryTypeEqPredicate(condition.type())
                        ,getFilterCreatedAt(condition.atFrom(), condition.atTo())
                        ,cursorCondition(condition)
                )
                .orderBy(
                        orderSpecifier(condition)
                        ,idOrderSpecifier(condition)
                )
                .limit(condition.size() + 1)
                .fetch();

    }

    private OrderSpecifier<?> orderSpecifier(EmployeeHistorySearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;
        if("ipAddress".equals(condition.sortField())){
            return new OrderSpecifier<>(order, employeeHistory.ipAddress);
        }
        return new OrderSpecifier<>(order, employeeHistory.createdAt);
    }


    @NonNull
    private OrderSpecifier<Long> idOrderSpecifier(EmployeeHistorySearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, employeeHistory.id);
    }

    private BooleanExpression getFilterCreatedAt(LocalDateTime atFrom, LocalDateTime atTo) {
        if(atFrom ==null && atTo == null) return  null;
        if(atFrom ==null ) return employeeHistory.createdAt.loe(atTo);
        if(atTo ==null) return employeeHistory.createdAt.goe(atFrom);

        return employeeHistory.createdAt.between(atFrom, atTo);
    }

    private  BooleanExpression getHistoryTypeEqPredicate(String type) {
       if(isBlank(type)) return null;

       return employeeHistory.type.eq(HistoryType.valueOf(type));
    }

    private BooleanExpression getStringPathPredicate(StringPath path, String value) {
        if(isBlank(value)) return null;

        return path.containsIgnoreCase(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }


    @Nullable
    private BooleanExpression cursorCondition(EmployeeHistorySearchCondition condition) {
        if(!condition.hasCursor()){
            return null;
        }

        String sortField = condition.sortField();
        boolean desc = condition.isDesc();
        String cursor = condition.cursor();

        if("at".equals(sortField)){
            LocalDateTime localDate = LocalDateTime.parse(cursor);

            if(desc){
                return employeeHistory.createdAt.lt(localDate)
                        .or(employeeHistory.createdAt.eq(localDate)
                                .and(employeeHistory.id.lt(condition.idAfter()))
                        );
            }

            return employeeHistory.createdAt.gt(localDate)
                    .or(employeeHistory.createdAt.eq(localDate)
                            .and(employeeHistory.id.gt(condition.idAfter())));
        }

        StringPath path = employeeHistory.ipAddress;
        if(desc){
            return path.lt(cursor)
                    .or(path.eq(cursor)
                            .and(employeeHistory.id.lt(condition.idAfter())));
        }

        return path.gt(cursor)
                .or(path.eq(cursor)
                        .and(employeeHistory.id.gt(condition.idAfter())));
    }

    @Override
    public long countEmployeeHistory(EmployeeHistorySearchCondition condition) {
        Long employeeHistoryCount = jpaQueryFactory
                .select(employeeHistory.id.count())
                .from(employeeHistory)
                .where(
                        getStringPathPredicate(employeeHistory.employeeNumber, condition.employeeNumber())
                        , getStringPathPredicate(employeeHistory.memo, condition.memo())
                        , getStringPathPredicate(employeeHistory.ipAddress, condition.ipAddress())
                        , getHistoryTypeEqPredicate(condition.type())
                        , getFilterCreatedAt(condition.atFrom(), condition.atTo())
                ).fetchOne();

        return employeeHistoryCount == null ?  0 : employeeHistoryCount;
    }
}
