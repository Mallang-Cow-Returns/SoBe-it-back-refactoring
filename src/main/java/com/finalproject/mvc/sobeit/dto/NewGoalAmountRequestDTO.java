package com.finalproject.mvc.sobeit.dto;

import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewGoalAmountRequestDTO {
    private String title;
    private Long goalAmount;
    private LocalDate startDate; // 도전과제 시작일
    private LocalDate endDate; // 도전과제 종료일
    private int routine;
}
