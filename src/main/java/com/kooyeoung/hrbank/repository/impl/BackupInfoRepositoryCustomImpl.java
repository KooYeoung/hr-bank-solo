package com.kooyeoung.hrbank.repository.impl;

import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSearchCondition;
import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSummery;
import com.kooyeoung.hrbank.entity.BackupStatus;
import com.kooyeoung.hrbank.repository.BackupInfoRepositoryCustom;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.kooyeoung.hrbank.entity.QBackupInfo.backupInfo;
import static com.kooyeoung.hrbank.entity.QFileInfo.fileInfo;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BackupInfoRepositoryCustomImpl implements BackupInfoRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BackupInfoSummery> searchBackupInfo(BackupInfoSearchCondition condition) {

        return jpaQueryFactory
                .select(Projections.constructor(
                        BackupInfoSummery.class,
                        backupInfo.id,
                        backupInfo.worker,
                        backupInfo.startedAt,
                        backupInfo.endedAt,
                        backupInfo.status,
                        backupInfo.backupFile.id
                ))
                .from(backupInfo)
                .leftJoin(backupInfo.backupFile, fileInfo)
                .where(
                        getStatusExpression(condition.status()),
                        getBetween(condition.startAtFrom(), condition.startAtTo()),
                        getContainsedIgnoreCase(condition.worker()),
                        cursorCondition(condition)
                )
                .orderBy(orderSpecifier(condition), idOrderSpecifier(condition))
                .limit(condition.size() + 1)
                .fetch();
    }

    @Override
    public Long countBackupInfo(BackupInfoSearchCondition condition) {

        Long l = jpaQueryFactory.select(backupInfo.id.count())
                .from(backupInfo)
                .where(
                        getStatusExpression(condition.status()),
                        getBetween(condition.startAtFrom(), condition.startAtTo()),
                        getContainsedIgnoreCase(condition.worker())
                )
                .fetchOne();

        return l == null ? 0L : l;
    }

    private OrderSpecifier<?> orderSpecifier(BackupInfoSearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;
        if ("endedAt".equals(condition.sortField())) {
            return new OrderSpecifier<>(order, backupInfo.endedAt, OrderSpecifier.NullHandling.NullsLast);
        }
        return new OrderSpecifier<>(order, backupInfo.startedAt);
    }

    private OrderSpecifier<Long> idOrderSpecifier(BackupInfoSearchCondition condition) {
        Order order = condition.isDesc() ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, backupInfo.id);
    }

    private BooleanExpression getContainsedIgnoreCase(String worker) {
        if (isBlank(worker)) return null;

        return backupInfo.worker.containsIgnoreCase(worker);
    }

    private boolean isBlank(String worker) {
        return worker == null || worker.isBlank();
    }

    private BooleanExpression getBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;
        if (from == null) return backupInfo.startedAt.loe(to);
        if (to == null) return backupInfo.startedAt.goe(from);

        return backupInfo.startedAt.between(from, to);
    }

    private BooleanExpression getStatusExpression(BackupStatus staus) {
        if (staus == null) {
            return null;
        }

        return backupInfo.status.eq(staus);
    }

    @Nullable
    private BooleanExpression cursorCondition(BackupInfoSearchCondition condition) {
        if (!condition.hasCursor()) {
            return null;
        }

        String sortField = condition.sortField();
        boolean desc = condition.isDesc();
        String cursor = condition.cursor();

        LocalDateTime localDate = LocalDateTime.parse(cursor);
        if ("startedAt".equals(sortField)) {

            if (desc) {
                return backupInfo.startedAt.lt(localDate)
                        .or(backupInfo.startedAt.eq(localDate)
                                .and(backupInfo.id.lt(condition.idAfter()))
                        );
            }

            return backupInfo.startedAt.gt(localDate)
                    .or(backupInfo.startedAt.eq(localDate)
                            .and(backupInfo.id.gt(condition.idAfter())));
        }


        if (desc) {
            return backupInfo.endedAt.lt(localDate)
                    .or(backupInfo.endedAt.eq(localDate)
                            .and(backupInfo.id.lt(condition.idAfter()))
                    );
        }

        return backupInfo.endedAt.gt(localDate)
                .or(backupInfo.endedAt.eq(localDate)
                        .and(backupInfo.id.gt(condition.idAfter())));
    }
}
