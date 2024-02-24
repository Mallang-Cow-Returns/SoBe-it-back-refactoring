package com.finalproject.mvc.sobeit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLikeDTO {
    private Long reply_like_seq;
    private Long reply_seq;
    private Long user_seq;
    private Boolean is_liked;
}
