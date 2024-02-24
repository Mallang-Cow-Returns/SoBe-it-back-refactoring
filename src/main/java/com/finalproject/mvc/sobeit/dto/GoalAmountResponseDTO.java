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
public class GoalAmountResponseDTO {
    private Long goalAmountSeq;
    private Long goalAmount;
    private String title;
    private String userId;
    private String nickName;
    private String profileImg;
    private String userTier;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private int isSuccess; // 도전과제 성공 여부(1=진행중, 2=성공, 3=실패)
    private int routine; // 도전과제 반복 주기(1=매일, 2=전체기간)

    private Long consumption;

    private int status; // 유저구분을 위한 상태: 1 = 자신의 도전과제, 2 = 다른 사용자의 도전과제
}
