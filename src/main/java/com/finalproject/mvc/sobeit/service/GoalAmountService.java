package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.GoalAmountCntDTO;
import com.finalproject.mvc.sobeit.dto.GoalAmountDTO;
import com.finalproject.mvc.sobeit.dto.GoalAmountResponseDTO;
import com.finalproject.mvc.sobeit.dto.NewGoalAmountRequestDTO;
import com.finalproject.mvc.sobeit.entity.GoalAmount;
import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface GoalAmountService {
    /**
     * 도전과제 정보 가져오기(성공한 도전과제 갯수,도전과제 갯수)
     * @param userId
     * @return 성공한 도전과제 갯수&도전과제 갯수
     **/
    GoalAmountCntDTO goalAmountCnt(String userId);

    /**
     * 도전과제 정보 가져오기(도전과제 리스트)
     * @param userId
     * @return 도전과제 목록
     **/
    List<GoalAmountResponseDTO> selectGoalAmount(Users user, String userId);

    /**
     * 도전과제 작성
     * @param user
     * @param
     * @return 작성된 도전과제
     **/
    GoalAmountDTO insertGoalAmount(Users user, NewGoalAmountRequestDTO newGoalAmountRequestDTO);

    /**
     * 도전과제 삭제
     * @param user
     * @param goalAmountSeq
     */
    void deleteGoalAmount(Users user, Long goalAmountSeq);

    /**
     * 사이드바 도전 과제 가져오기
     * : 로그인한 유저의 가장 최근 도전 과제(status = 진행중) 1개를 보여준다.
     * @param userSeq
     * @return GoalAmountResponseDTO
     * */
    GoalAmountResponseDTO findGoalAmountSeqList(Long userSeq);
}
