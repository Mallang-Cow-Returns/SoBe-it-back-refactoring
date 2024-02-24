package com.finalproject.mvc.sobeit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUserDTO {
    private String profileImg; // 프로필 이미지
    private String nickname; // 닉네임
    private String userId; // ID
    private String introDetail; // 자기소개
    private int followingCnt; // 팔로잉 수
    private int followerCnt; // 팔로워 수

    private int status; // 프로필 대상 : 1. 자신 , 2. 팔로우하지 않은 타인, 3. 팔로우한 타인
}
