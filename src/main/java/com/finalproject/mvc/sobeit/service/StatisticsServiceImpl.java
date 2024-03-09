package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.ExpenditureListResponseDTO;
import com.finalproject.mvc.sobeit.dto.ExpenditureResponseDTO;
import com.finalproject.mvc.sobeit.dto.StatisticsResponseDTO;
import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.Users;
import com.finalproject.mvc.sobeit.repository.ArticleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService{
    private final ArticleRepo articleRepo;

    /**
     * 월별 지출 내역 가져오기
     * @param user
     * @param year
     * @param month
     * @return Map<날짜, 지출 내역 리스트>
     */
    @Override
    public List<ExpenditureListResponseDTO> getExpenditure(Users user, int year, int month) {
        Long userSeq = user.getUserSeq();
        List<ExpenditureListResponseDTO> expList = new ArrayList<>();
        int lastday = getMonthLastDay(year, month);

        // 1일~말일 일별 지출 가져오기
        for(int i=lastday; i>0;i--) {
            LocalDate date = LocalDate.of(year, month, i);

            // 일별 지출 목록 생성
            List<ExpenditureResponseDTO> list = getExpenditureDay(userSeq, year, month, i);
            // 지출 목록이 없는 날이면 패스
            if (list==null || list.isEmpty()) continue;

            // 오늘 쓴 지출 금액 가져오기
            Long amount = articleRepo.findSumAmountByConsumptionDate(userSeq, date);

            // 날짜 + 지출목록 리스트를 가진 Response 객체 생성 후 리스트에 추가
            ExpenditureListResponseDTO resp = ExpenditureListResponseDTO.builder()
                    .date(date)
                    .amount(amount)
                    .list(list)
                    .build();
            expList.add(resp);
        }
        return expList;
    }

    /**
     * 일별 지출 내역 가져오기
     * @param userSeq
     * @param year
     * @param month
     * @param day
     * @return 해당 날짜에 쓴 지출 내역 리스트
     */
    List<ExpenditureResponseDTO> getExpenditureDay(Long userSeq, int year, int month, int day){
        if (getMonthLastDay(year, month) < day) return null; // 말일 이후의 날짜는 조회 X

        LocalDate date = LocalDate.of(year, month, day);

        // 유저가 그 날 쓴 지출 글 가져오기
        List<Article> articleList = articleRepo.findExpenditureArticlesByConsumptionDate(userSeq, date);

        // ResponseDTOList로 변환
        List<ExpenditureResponseDTO> expenditureResponseDTOList = new ArrayList<>();
        for (Article article : articleList) {
            // 가계부 메모 가져오기
            String context = article.getFinancialText();
            // 가계부 메모가 없다면 글 내용 가져오기
            if (context == null || context.isEmpty()){
                context = article.getArticleText();
            }
            // ResponseDTO 생성
            ExpenditureResponseDTO expenditureResponseDTO = ExpenditureResponseDTO.builder()
                    .expenditureCategory(article.getExpenditureCategory())
                    .context(context)
                    .amount(article.getAmount())
                    .articleSeq(article.getArticleSeq())
                    .build();
            // 리스트에 추가
            expenditureResponseDTOList.add(expenditureResponseDTO);
        }

        return expenditureResponseDTOList;
    }

    /**
     * 월별 차트 가져오기
     * @param year
     * @param month
     * @return 리스트[id:카테고리번호, amount:지출금액]
     */
    @Override
    public List<StatisticsResponseDTO> getChart(Users user, int year, int month) {
        // 이번달 1일 ~ 말일 범위 지정
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, getMonthLastDay(year, month));

        List<StatisticsResponseDTO> amountList= new ArrayList<>();

        // 카테고리 1번부터 6번까지 지출 금액 담기
        for(int i=1; i<7; i++){
            StatisticsResponseDTO dto = StatisticsResponseDTO.builder()
                    .id(i)
                    .amount(articleRepo.findSumAmountByUserSeqAndCategory(user.getUserSeq(), i, start, end))
                    .build();
            amountList.add(dto);
        }
        return amountList;
    }

    /**
     * 월별 캘린더 가져오기
     * @param user
     * @param year
     * @param month
     * @return 리스트[id:날짜, amount:지출금액]
     */
    @Override
    public List<StatisticsResponseDTO> getCalendar(Users user, int year, int month) {
        Long userSeq = user.getUserSeq();
        List<StatisticsResponseDTO> list = new ArrayList<>();

        // 1일~31일 일별 지출 가져오기
        int lastday = getMonthLastDay(year, month);

        for(int i=1; i<lastday+1;i++) {
            LocalDate date = LocalDate.of(year, month, i); // 날짜 생성
            StatisticsResponseDTO dto = StatisticsResponseDTO.builder()
                    .id(i)
                    .amount(articleRepo.findSumAmountByConsumptionDate(userSeq, date))
                    .build();
            list.add(dto);
        }
        return list;
    }

    /**
     * 월 지출 금액 구하기
     * @param userSeq
     * @param year
     * @param month
     * @return 해당 달 지출 금액 합계
     */
    @Override
    public Long getSumAmount(Long userSeq, int year, int month){
        // 이번달 1일 ~ 말일 범위 지정
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, getMonthLastDay(year, month));

        // 이 달 쓴 전체 지출 금액 가져오기
        return articleRepo.findSumAmountByUserSeqAndDate(userSeq, start, end);
    }

    /**
     * 월의 말일 조회
     * @param year
     * @param month
     * @return
     */
    private int getMonthLastDay(int year, int month){
        Calendar cal = Calendar.getInstance();
        cal.set(year,month-1,1); // 월은 0이 1월
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}