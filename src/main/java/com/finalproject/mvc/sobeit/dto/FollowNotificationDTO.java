package com.finalproject.mvc.sobeit.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowNotificationDTO implements NotificationDTO {
    private Long notificationSeq;
    private int type;
    private String followingUserNickName;
    private String followingUserId;
    private String content;
    private boolean isFollowing;
    private String userTier;
    private String url;
    private String imageUrl;
    private LocalDateTime timestamp;
    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
