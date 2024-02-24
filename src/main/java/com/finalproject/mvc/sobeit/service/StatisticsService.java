package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.ExpenditureListResponseDTO;
import com.finalproject.mvc.sobeit.dto.StatisticsResponseDTO;
import com.finalproject.mvc.sobeit.entity.Users;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    /**
     * 월별 지출 내역
     */
    public List<ExpenditureListResponseDTO> getExpenditure(Users user, int year, int month);

    /**
     * 월별 차트
     */
    public List<StatisticsResponseDTO> getChart(Users user, int year, int month);

    /**
     * 월별 캘린더
     */
    public List<StatisticsResponseDTO> getCalendar(Users user, int year, int month);

    /**
     * 월별 지출 금액 가져오기
     */
    public Long getSumAmount(Long userSeq, int year, int month);

}
