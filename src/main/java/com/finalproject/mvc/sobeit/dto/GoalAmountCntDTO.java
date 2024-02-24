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
public class GoalAmountCntDTO {
    private int successGoalAmountCnt;
    private int goalAmountCnt;
    private String nextTier;
    private int leftCnt;
}
