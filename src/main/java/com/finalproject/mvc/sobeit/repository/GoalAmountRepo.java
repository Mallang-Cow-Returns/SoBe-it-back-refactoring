package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.GoalAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface GoalAmountRepo extends JpaRepository<GoalAmount, Long> {
    @Query("select a from GoalAmount a where a.user.userId = ?1 order by a.startDate desc")
    List<GoalAmount> findGoalAmountByUserId(String userId);

    @Query("select a.goalAmountSeq from GoalAmount a where a.user.userId = ?1 and a.isSuccess = 2")
    List<Long> findGoalAmountSeqSuccess(String userId);

    @Query("select a.goalAmountSeq from GoalAmount a where a.user.userId = ?1")
    List<Long> findGoalAmountSeq(String userId);

    @Query("select a.goalAmountSeq from GoalAmount a where a.user.userSeq = :userSeq and a.isSuccess = 1 order by a.goalAmountSeq desc")
    List<Long> findGoalAmountSeqList(Long userSeq);
}
