package com.finalproject.mvc.sobeit.controller;

import com.finalproject.mvc.sobeit.dto.ChartResponseDTO;
import com.finalproject.mvc.sobeit.dto.ExpenditureResponseDTO;
import com.finalproject.mvc.sobeit.dto.ResponseDTO;
import com.finalproject.mvc.sobeit.entity.Users;
import com.finalproject.mvc.sobeit.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
//
@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    /**
     * 월별 지출 내역
     *
     * @param user
     * @return
     */
    @PostMapping("/getExpenditure")
    public ResponseEntity<?> getExpenditure(@AuthenticationPrincipal Users user, @RequestBody Map<String, Integer> date) {
        try {
            ChartResponseDTO responseDTO = ChartResponseDTO.builder()
                    .monthAmount(statisticsService.getSumAmount(user.getUserSeq(), date.get("year"), date.get("month"))) // 월 지출 금액
                    .data(statisticsService.getExpenditure(user, date.get("year"), date.get("month"))) // 일별 지출 내역
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 월별 그래프
     *
     * @param user
     * @param date
     * @return
     */
    @PostMapping("/chart")
    public ResponseEntity<?> getChart(@AuthenticationPrincipal Users user, @RequestBody Map<String, Integer> date) {
        try {
            ChartResponseDTO responseDTO = ChartResponseDTO.builder()
                    .monthAmount(statisticsService.getSumAmount(user.getUserSeq(), date.get("year"), date.get("month"))) // 월 지출 금액
                    .data(statisticsService.getChart(user, date.get("year"), date.get("month"))) // 일별 지출 내역
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 월별 지출 캘린더
     *
     * @param user
     * @param date
     * @return
     */
    @PostMapping("/calendar")
    public ResponseEntity<?> getCalendar(@AuthenticationPrincipal Users user, @RequestBody Map<String, Integer> date) {
        try {
            ChartResponseDTO responseDTO = ChartResponseDTO.builder()
                    .monthAmount(statisticsService.getSumAmount(user.getUserSeq(), date.get("year"), date.get("month"))) // 월 지출 금액
                    .data(statisticsService.getCalendar(user, date.get("year"), date.get("month"))) // 일별 지출 금액
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

}

