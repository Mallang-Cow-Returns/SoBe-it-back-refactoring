package com.finalproject.mvc.sobeit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject.mvc.sobeit.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalAmountDTO {
    private Long goalAmountSeq;
    private Long goalAmount;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private int isSuccess;
    private int routine;

}
