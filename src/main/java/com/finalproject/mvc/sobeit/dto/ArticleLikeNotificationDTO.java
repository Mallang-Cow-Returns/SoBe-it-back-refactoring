package com.finalproject.mvc.sobeit.dto;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleLikeNotificationDTO implements NotificationDTO {
    private Long notificationSeq;
    private int type;
    private String content;
    private Long notArticleSeq;
    private String imageUrl;
    private String articleContent;
    private LocalDateTime timestamp;

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
