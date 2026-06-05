package com.kooyeoung.hrbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeNumberSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "yyyy_mm", nullable = false, unique = true, length = 6)
    private String yyyyMM;
    @Column(name = "last_sequence",nullable = false)
    private Long lastSequence;

    public EmployeeNumberSequence(String yyyyMM){
        this.yyyyMM = yyyyMM;
        this.lastSequence = 0L;
    }

    private Long nextSequence(){
        this.lastSequence+=1;
        return this.lastSequence;
    }

    public String issueEmployeeNumber(int sequenceWidth){
        Long nextEmployeeNumber = nextSequence();

        String paddedSequence = String.format("%0"+sequenceWidth+"d",nextEmployeeNumber);

        return yyyyMM + paddedSequence;
    }
}
