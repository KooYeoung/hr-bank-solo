package com.kooyeoung.hrbank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

/**
 * - **{이름}**
 * - **{설명}**
 * - **{설립일}**
 */

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Department {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private LocalDate establishedDate;

    public boolean isNameChanged(String name){
        return !this.name.equals(name);
    }

    public Department(String name, String description, LocalDate establishedDate){
        this.name =name;
        this.description = description;
        this.establishedDate = establishedDate;
    }

    public void updateInfo(String name, String description, LocalDate establishedDate){
        this.name = name;
        this.description = description;
        this.establishedDate = establishedDate;
    }

    /**
     ***정보**
     *
     * **부서 등록**
     *
     * - **{이름}**, **{설명}**, **{설립일}**을 입력해 부서를 등록할 수 있습니다.
     *     - **{이름}**은 중복될 수 없습니다.
     *
     * **부서 수정**
     *
     * - **{이름}**, **{설명}**, **{설립일}**을 **** 수정할 수 있습니다.
     *     - **{이름}**은 중복될 수 없습니다.
     *
     * **부서 삭제**
     *
     * - 소속된 직원이 없는 경우에만 부서를 삭제할 수 있습니다.
     *
     * **부서 목록 조회**
     *
     * - **{이름 또는 설명}**으로 부서 목록을 조회할 수 있습니다.
     *     - **{이름 또는 설명}**는 부분 일치 조건입니다.
     *     - 조회 조건이 여러 개인 경우 모든 조건을 만족한 결과로 조회합니다.
     * - **{이름}**, **{설립일}**로 정렬 및 페이지네이션을 구현합니다.
     *     - 여러 개의 정렬 조건 중 선택적으로 1개의 정렬 조건만 가질 수 있습니다.
     *     - 정확한 페이지네이션을 위해 **{이전 페이지의 마지막 요소 ID}**를 활용합니다.
     */

}
