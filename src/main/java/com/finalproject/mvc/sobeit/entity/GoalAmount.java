package com.finalproject.mvc.sobeit.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GoalAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long goalAmountSeq; // 도전과제 고유번호

    @Column(nullable = false)
    private Long goalAmount; // 도전과제 목표금액

    @Column(nullable = false)
    private String title; // 도전과제 제목

    @ManyToOne
    @JoinColumn(name = "userSeq", referencedColumnName = "userSeq", nullable = false)
    private Users user; // 해당하는 도전과제를 도전하는 사용자

    @Column(nullable = false)
    private LocalDate startDate; // 도전과제 시작일

    @Column(nullable = false)
    private LocalDate endDate; // 도전과제 종료일

    @Column(nullable = false)
    private int isSuccess; // 도전과제 성공 여부(1=진행중, 2=성공, 3=실패)

    @Column(nullable = false)
    private int routine; // 도전과제 반복 주기(1=매일, 2=전체기간)

    @Column(nullable = false)
    private Long consumption; // 기간동안 소비된 비용
}
