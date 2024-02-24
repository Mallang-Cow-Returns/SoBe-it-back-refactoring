package com.finalproject.mvc.sobeit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyLikeNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long replyLikeNotificationSeq;

    /**
     * 알림을 받는 유저 시퀀스
     */
    @ManyToOne
    @JoinColumn(name = "userSeq")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    /**
     * 댓글 좋아요가 발생한 해당 글의 URL
     */
    @Column(nullable = false)
    private String url;

    /**
     * 좋아요 1개 = 1
     * 좋아요 10개 = 2
     * 좋아요 100개 = 3
     */
    @Column(nullable = false)
    private int type;

    @ManyToOne
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reply reply;

    @Column(nullable = false)
    private LocalDateTime notificationDateTime;
}
