package com.finalproject.mvc.sobeit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long likeNotificationSeq;
    @ManyToOne
    @JoinColumn(name = "userSeq")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "fromUserSeq")
    private Users fromUser;

    /**
     * 나에게 팔로우 요청을 보낸 유저의 프로필 url
     */
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private LocalDateTime notificationDateTime;
}
