package com.kooyeoung.hrbank.repository.impl;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSearchCondition;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSummary;
import com.kooyeoung.hrbank.entity.EmployeeStatus;
import com.kooyeoung.hrbank.repository.EmployeeRepositoryCustom;
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

import java.time.LocalDate;
import java.util.List;

import static com.kooyeoung.hrbank.entity.QDepartment.department;
import static com.kooyeoung.hrbank.entity.QEmployee.employee;
import static com.kooyeoung.hrbank.entity.QFileInfo.fileInfo;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<EmployeeSummary> searchEmployee(EmployeeSearchCondition condition) {
        return  jpaQueryFactory.select(
                        Projections.constructor(
                                EmployeeSummary.class
                                ,employee.id
                                ,employee.name
                                ,employee.email
                                ,employee.employeeNumber
                                ,department.id
                                ,department.name
                                ,employee.position
                                ,employee.hireDate
                                ,employee.status.stringValue()
                                ,fileInfo.id
                        )
                )
                .from(employee)
                .join(employee.department, department)
                .leftJoin(employee.profileImage, fileInfo)
                .where(
                        getNameOrEmailContainsIgnoreCase(condition.nameOrEmail())
                        ,getStringPathContainsIgnoreCase(employee.employeeNumber, condition.employeeNumber())
                        ,getStringPathContainsIgnoreCase(employee.position, condition.position())
                        ,getStringPathContainsIgnoreCase(department.name, condition.departmentName())
                        ,getHireDateFilter(condition.hireDateFrom(), condition.hireDateTo())
                        ,getEqEmployeeStatus(condition.status())
                        ,cursorCondition(condition)
                )
                .orderBy( orderSpecifier(condition), idOrderSpecifier(condition))
                .limit(condition.size() + 1)
                .fetch();

    }

    @Override
    public long countEmployee(EmployeeSearchCondition condition) {
        Long employeeCount = jpaQueryFactory
                .select(employee.id.count())
                .from(employee)
                .where(
                        getNameOrEmailContainsIgnoreCase(condition.nameOrEmail())
                        , getStringPathContainsIgnoreCase(employee.employeeNumber, condition.employeeNumber())
                        , getStringPathContainsIgnoreCase(employee.position, condition.position())
                        , getStringPathContainsIgnoreCase(department.name, condition.departmentName())
                        , getHireDateFilter(condition.hireDateFrom(), condition.hireDateTo())
                        , getEqEmployeeStatus(condition.status())
                )
                .fetchOne();
        return employeeCount == null ? 0 : employeeCount;
    }

    private OrderSpecifier<?> orderSpecifier(EmployeeSearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;
        if("hireDate".equals(condition.sortField())){
            return new OrderSpecifier<>(order, employee.hireDate);
        }
        if("employeeNumber".equals(condition.sortField())){
            return new OrderSpecifier<>(order, employee.employeeNumber);
        }
        return new OrderSpecifier<>(order, employee.name);
    }

    @NonNull
    private OrderSpecifier<Long> idOrderSpecifier(EmployeeSearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, employee.id);
    }

    private BooleanExpression getEqEmployeeStatus(String status) {
        if(isBlank(status)) return null;

        return employee.status.eq(EmployeeStatus.valueOf(status));
    }

    private static BooleanExpression getHireDateFilter(LocalDate hireDateFrom, LocalDate hireDateTo) {
        if(hireDateFrom == null && hireDateTo == null) return null;
        if(hireDateFrom == null ) return employee.hireDate.loe(hireDateTo);
        if(hireDateTo == null ) return employee.hireDate.goe(hireDateFrom);

        return employee.hireDate.between(hireDateFrom, hireDateTo);
    }

    private BooleanExpression getStringPathContainsIgnoreCase(StringPath path, String value){
        if(isBlank(value)) return null;

        return path.containsIgnoreCase(value);
    }

    private BooleanExpression getNameOrEmailContainsIgnoreCase(String nameOrEmail) {
        if (isBlank(nameOrEmail)) return null;

        return employee.name.containsIgnoreCase(nameOrEmail)
                .or(employee.email.containsIgnoreCase(nameOrEmail));
    }

    private  boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Nullable
    private BooleanExpression cursorCondition(EmployeeSearchCondition condition) {
        if(!condition.hasCursor()){
            return null;
        }

        String sortField = condition.sortField();
        boolean desc = condition.isDesc();
        String cursor = condition.cursor();

        if("hireDate".equals(sortField)){
            LocalDate localDate = LocalDate.parse(cursor);

            if(desc){
                return employee.hireDate.lt(localDate)
                        .or(employee.hireDate.eq(localDate)
                                .and(employee.id.lt(condition.idAfter()))
                        );
            }

            return employee.hireDate.gt(localDate)
                    .or(employee.hireDate.eq(localDate)
                            .and(employee.id.gt(condition.idAfter())));
        }

        StringPath path = "employeeNumber".equals(sortField) ? employee.employeeNumber : employee.name;

        if(desc){
            return path.lt(cursor)
                    .or(path.eq(cursor)
                            .and(employee.id.lt(condition.idAfter())));
        }

        return path.gt(cursor)
                .or(path.eq(cursor)
                        .and(employee.id.gt(condition.idAfter())));
    }
}
