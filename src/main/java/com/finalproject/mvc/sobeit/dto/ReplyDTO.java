package com.finalproject.mvc.sobeit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {
    private Long reply_seq;
    private Long article_seq;
    private Long user_seq;
    private String reply_text;
    private Long parent_reply_seq;
    private LocalDateTime written_date;
    private int is_updated; // 댓글 수정 여부
    private String nickname; // 댓글을 작성한 사용자의 닉네임
    private String user_id; // 댓글을 작성한 사용자의 아이디
    private String user_tier; // 댓글을 작성한 사용자의 티어
    private String profile_image_url; // 댓글을 작성한 사용자의 프로필 이미지 URL
    private int reply_like_cnt; // 댓글 좋아요 개수
    private boolean is_article_writer; // 댓글을 작성한 사용자와 글의 작성자가 같은지
    private boolean is_reply_writer; // 댓글을 작성한 사용자가 자신인지
    private boolean is_clicked_like; // 사용자가 해당 댓글에 좋아요를 클릭했는지
}
