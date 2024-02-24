package com.finalproject.mvc.sobeit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NowUserDTO {
    private Long userSeq;
    private String userId;
    private String nickname;
    private String userTier;
    private String profileImgUrl;
}
