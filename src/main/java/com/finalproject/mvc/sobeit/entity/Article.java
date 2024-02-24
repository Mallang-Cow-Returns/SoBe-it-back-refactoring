package com.finalproject.mvc.sobeit.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long articleSeq;

    @ManyToOne
    @JoinColumn(name = "userSeq", referencedColumnName = "userSeq", nullable = false)
    private Users user;

    @Column(nullable = false)
    private int status; // 1 : 전체 공개, 2 : 맞팔 공개, 3 : 비공개

    private String imageUrl;

//    @Column(nullable = false)
    private int expenditureCategory; // 1 ~ 6까지 뭘로 할 지 정하기

    @Column(nullable = false)
    private Long amount;

    private String financialText;

    @Column(nullable = false)
    private String articleText;

    @Column(nullable = false)
    private LocalDateTime writtenDate;

    private LocalDateTime editedDate;

    @Column(nullable = false)
    private int articleType; // 1 : 지출, 2 : 결재
    private LocalDate consumptionDate;

    @Column(nullable = false)
    private String isAllowed;
}
