package com.finalproject.mvc.sobeit.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponseDTO {
    private Long monthAmount; // 이번달 지출금액
    private List<?> data;
}
