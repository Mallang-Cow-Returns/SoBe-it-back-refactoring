package com.finalproject.mvc.sobeit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Long userSeq;
    private String userId;
    private String nickname;
    private String userTier;
    private String introduction;
    private String profileImgUrl;
    private int status; // 0 : 팔로우 하지 않는 사람, 1 : 본인, 2 : 팔로우 중인 사람
}
