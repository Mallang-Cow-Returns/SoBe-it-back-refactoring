package com.finalproject.mvc.sobeit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalproject.mvc.sobeit.entity.Users;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {
    private Long articleSeq;
    private Users user;
    private int status;
    private String imageUrl;
    private int expenditureCategory;
    private Long amount;
    private String financialText;
    private String articleText;
    private int articleType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consumptionDate;
    private String isAllowed;
}
