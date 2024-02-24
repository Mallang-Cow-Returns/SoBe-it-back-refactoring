package com.finalproject.mvc.sobeit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long user_seq;
    private String token;
    private String user_id;
    private String email;
    private String introduction;
    private String user_name;
    private String password;
    private String nickname;
    private String user_tier;
    private Long challenge_count;
    private String phone_number;
    private String profile_image_url;
    private Boolean is_id_verified; // 사용자 아이디 중복 체크
}
