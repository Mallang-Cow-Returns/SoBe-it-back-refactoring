package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.GoalAmountCntDTO;
import com.finalproject.mvc.sobeit.dto.GoalAmountDTO;
import com.finalproject.mvc.sobeit.dto.GoalAmountResponseDTO;
import com.finalproject.mvc.sobeit.dto.NewGoalAmountRequestDTO;
import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.GoalAmount;
import com.finalproject.mvc.sobeit.entity.Users;
import com.finalproject.mvc.sobeit.repository.ArticleRepo;
import com.finalproject.mvc.sobeit.repository.GoalAmountRepo;
import com.finalproject.mvc.sobeit.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalAmountServiceImpl implements GoalAmountService{

    private final GoalAmountRepo goalAmountRep;
    private final UserRepo userRep;
    private final ArticleRepo articleRep;

    /**
     * 유저에 대한 성공한 도전과제 개수&도전과제 개수
     * @param userId
     * @return 성공한 도전과제 개수&도전과제 개수
     */
    @Override
    public GoalAmountCntDTO goalAmountCnt(String userId) {
        int successCnt = goalAmountRep.findGoalAmountSeqSuccess(userId).size();
        int AllCnt = goalAmountRep.findGoalAmountSeq(userId).size();

        Users user = userRep.findByUserId(userId);
        String currentTier = user.getUserTier();
        String nextTier = "";
        int leftCnt = 0;

        if (successCnt < 3) {
            // BRONZE TIER
            if (!Objects.equals(currentTier, "BRONZE")){
                user.setUserTier("BRONZE");
            }
            nextTier = "SILVER";
            leftCnt = 3-successCnt;
        } else if (successCnt < 7) {
            // SILVER TIER
            if (!Objects.equals(currentTier, "SILVER")){
                user.setUserTier("SILVER");
            }
            nextTier = "GOLD";
            leftCnt = 7-successCnt;

        } else if (successCnt > 7){
            // GOLD TIER
            if (!Objects.equals(currentTier, "BRONZE")){
                user.setUserTier("SILVER");
            }
            nextTier = "NONE";
        }

        GoalAmountCntDTO cntDTO = GoalAmountCntDTO.builder()
                .successGoalAmountCnt(successCnt)
                .goalAmountCnt(AllCnt)
                .nextTier(nextTier)
                .leftCnt(leftCnt)
                .build();

        return cntDTO;
    }

    public GoalAmount selectGoalAmountById(Long goalAmountSeq){
        return goalAmountRep.findById(goalAmountSeq).orElse(null);
    }

    public GoalAmountResponseDTO findGoalAmountResponse(Users user, String userId, Long goalAmountSeq){
        //보려는 도전과제 가져오기
        GoalAmount goalAmount = selectGoalAmountById(goalAmountSeq);
        Users byUserId = userRep.findByUserId(userId);

        Long consumption = 0L;// 기간동안 소비된 비용
        int isSuccess = 0;

        System.out.println("확인 전, " + goalAmount.getConsumption());

        if(goalAmount.getIsSuccess() == 1){

            List<Article> articleList = articleRep.findArticlesByUser(userId);
            LocalDate today = LocalDate.now();

            if (articleList == null){ // 게시글이 없는 경우
                throw new RuntimeException("소비한 비용이 없습니다.");
            }
            for (Article article : articleList){ // 소비금액 선정
                if (goalAmount.getRoutine()==1){ // 반복주기가 매일인 경우
                    if(today.isAfter(goalAmount.getStartDate()) && today.isBefore(goalAmount.getEndDate()) && today.isEqual(article.getConsumptionDate())){
                        consumption += article.getAmount();
                    }
                } else { // 반복주기가 전체인 경우
                    if (article.getConsumptionDate().isAfter(goalAmount.getStartDate()) && article.getConsumptionDate().isBefore(goalAmount.getEndDate())){
                        consumption += article.getAmount();
                    }
                }
            }
            goalAmount.setConsumption(consumption);

            System.out.println(consumption);
            System.out.println("확인 후, " + goalAmount.getConsumption());

            // 성공 여부 확인
            if(consumption > goalAmount.getGoalAmount()){ // 소비가 목표금액을 넘어갈 경우
                isSuccess = 3; // 실패로 표시
                goalAmount.setIsSuccess(3);
            } else { // 소비금액이 목표금액과 같거나 넘지않은 경우
                if(today.isAfter(goalAmount.getEndDate())){ // 오늘, 도전과제 마지막날을 넘어간 경우
                    isSuccess = 2; // 성공
                    goalAmount.setIsSuccess(2);
                } else { // 오늘, 도전과제 마지막날을 넘어가지 않은 경우
                    isSuccess = 1; // 진행중
                    goalAmount.setIsSuccess(1);
                }
            }
        }
        GoalAmountResponseDTO goalAmountResponseDTO = new GoalAmountResponseDTO();
        goalAmountResponseDTO.setGoalAmountSeq(goalAmountSeq);
        goalAmountResponseDTO.setGoalAmount(goalAmount.getGoalAmount());
        goalAmountResponseDTO.setTitle(goalAmount.getTitle());
        goalAmountResponseDTO.setUserId(userId);
        goalAmountResponseDTO.setStartDate(goalAmount.getStartDate());
        goalAmountResponseDTO.setEndDate(goalAmount.getEndDate());
        goalAmountResponseDTO.setIsSuccess(goalAmount.getIsSuccess());
        goalAmountResponseDTO.setRoutine(goalAmount.getRoutine());
        goalAmountResponseDTO.setConsumption(goalAmount.getConsumption());
        goalAmountResponseDTO.setProfileImg(byUserId.getProfileImageUrl());
        goalAmountResponseDTO.setNickName(byUserId.getNickname());
        goalAmountResponseDTO.setUserTier(byUserId.getUserTier());

        if (user.getUserId().equals(userId)){ // 1. 로그인한 유저가 자신의 도전과제를 확인하는 경우
            goalAmountResponseDTO.setStatus(1);
        } else { // 2. 로그인한 유저가 다른 유저의 도전과제를 확인하는 경우
            goalAmountResponseDTO.setStatus(2);
        }

        return goalAmountResponseDTO;
    }

    @Override
    public List<GoalAmountResponseDTO> selectGoalAmount(Users user, String userId) {
        List<Long> goalAmountSeqList = goalAmountRep.findGoalAmountSeq(userId);

        System.out.println("여긴가0");
        if (goalAmountSeqList == null || goalAmountSeqList.size() == 0) throw new RuntimeException("도전과제가 없습니다.");


        System.out.println("여긴가1");
        List<GoalAmountResponseDTO> goalAmountList = new ArrayList<>();
        goalAmountSeqList.forEach(g -> goalAmountList.add(findGoalAmountResponse(user, userId, g)));

        System.out.println("여긴가2");
        goalAmountList.sort(Comparator.comparing(GoalAmountResponseDTO::getGoalAmountSeq).reversed());
        return goalAmountList;
    }

    @Override
    public GoalAmountDTO insertGoalAmount(Users user, NewGoalAmountRequestDTO newGoalAmountRequestDTO) throws RuntimeException{
        // 요청 이용해 저장할 도전과제 생성
        Long consumption = articleRep
                .findSumOfAmountByUserAndConsumptionDate(user.getUserSeq(), newGoalAmountRequestDTO.getStartDate())
                .orElse(0L);


        System.out.println("서비스단");
        System.out.println(newGoalAmountRequestDTO);
        System.out.println("소비금액" + consumption);
        GoalAmount goalAmount = GoalAmount.builder()
                .user(user)
                .goalAmount(newGoalAmountRequestDTO.getGoalAmount())
                .title(newGoalAmountRequestDTO.getTitle())
                .startDate(newGoalAmountRequestDTO.getStartDate())
                .endDate(newGoalAmountRequestDTO.getEndDate())
                .routine(newGoalAmountRequestDTO.getRoutine())
                .isSuccess(1) // 처음은 무조건 '진행중'으로 작성
                .consumption(consumption)
                .build();

        System.out.println("ㅎㅇ");
        System.out.println(goalAmount);
        GoalAmount savedGoalAmount = goalAmountRep.save(goalAmount);

        GoalAmountDTO goalAmountDTO = GoalAmountDTO.builder()
                .goalAmountSeq(savedGoalAmount.getGoalAmountSeq())
                .goalAmount(savedGoalAmount.getGoalAmount())
                .title(savedGoalAmount.getTitle())
                .startDate(savedGoalAmount.getStartDate())
                .endDate(savedGoalAmount.getEndDate())
                .routine(savedGoalAmount.getRoutine())
                .isSuccess(savedGoalAmount.getIsSuccess())
                .build();
        return goalAmountDTO;
    }

    @Override
    public void deleteGoalAmount(Users user, Long goalAmountSeq) throws RuntimeException{
        GoalAmount foundGoalAmount = goalAmountRep.findById(goalAmountSeq).orElse(null);
        if (foundGoalAmount == null){ // 삭제할 도전과제가 없는 경우
            throw new RuntimeException("삭제할 도전과제가 없습니다.");
        }

        if (!Objects.equals(user.getUserSeq(), foundGoalAmount.getUser().getUserSeq())){ // 삭제 요청 유저가 작성자가 아닐 경우 예외 발생
            throw new RuntimeException("작성자가 아닙니다.");
        }
        goalAmountRep.deleteById(goalAmountSeq);
    }

    /**
     * 사이드바 도전 과제 가져오기
     * : 로그인한 유저의 가장 최근 도전 과제(status = 진행중) 1개를 보여준다.
     * @param userSeq
     * @return GoalAmountResponseDTO
     * */
    public GoalAmountResponseDTO findGoalAmountSeqList(Long userSeq){
        Users user = userRep.findById(userSeq).orElse(null);

        List<Long> goalAmountSeqList = goalAmountRep.findGoalAmountSeqList(userSeq);
        Long goalAmountSeq = goalAmountSeqList.get(0);

        return findGoalAmountResponse(user, user.getUserId(), goalAmountSeq);
    }
}
