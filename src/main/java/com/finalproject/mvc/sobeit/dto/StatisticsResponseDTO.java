package com.finalproject.mvc.sobeit.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponseDTO {
    private int id;
    private Long amount;
}
