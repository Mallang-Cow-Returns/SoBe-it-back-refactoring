package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.*;
import com.finalproject.mvc.sobeit.entity.*;
import com.finalproject.mvc.sobeit.repository.FollowNotificationRepo;
import com.finalproject.mvc.sobeit.repository.ArticleLikeNotificationRepo;
import com.finalproject.mvc.sobeit.repository.ReplyLikeNotificationRepo;
import com.finalproject.mvc.sobeit.repository.ReplyNotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final ArticleLikeNotificationRepo articleLikeNotificationRepo; // 게시글 좋아요
    private final ReplyNotificationRepo replyNotificationRepo; // 댓글 알림
    private final FollowNotificationRepo followNotificationRepo; // 팔로우 알림
    private final ReplyLikeNotificationRepo replyLikeNotificationRepo; // 댓글 좋아요 알림
    private final ArticleServiceImpl articleService;


    @Override
    public List<NotificationDTO> getAllNotification(Users user) throws Exception {
        List<NotificationDTO> notifications = new ArrayList<>();

        // 팔로우 알림들
        List<FollowNotification> followNotificationList = followNotificationRepo.findByUser(user).orElse(null);
        if (followNotificationList != null) {
            for (FollowNotification followNotification : followNotificationList) {
                // 맞팔 체크 로직 필요
                boolean followToFollowCheck = articleService.followToFollowCheck(user.getUserSeq(), followNotification.getFromUser().getUserSeq());
                notifications.add(toFollowNotificationDto(followNotification, followToFollowCheck));
            }
        }

        // 게시글 좋아요 알림들
        List<ArticleLikeNotification> articleLikeNotificationList = articleLikeNotificationRepo.findByUser(user).orElse(null);
        if (articleLikeNotificationList != null) {
            for (ArticleLikeNotification articleLikeNotification : articleLikeNotificationList) {
                notifications.add(toArticleLikeNotificationDto(articleLikeNotification));
            }
        }

        //댓글 알림
        List<ReplyNotification> replyNotificationList = replyNotificationRepo.findByUser(user).orElse(null);
        if (replyNotificationList != null) {
            for (ReplyNotification replyNotification : replyNotificationList) {
                notifications.add(toReplyNotificationDto(replyNotification));
            }
        }

        //댓글 좋아요 알림
        List<ReplyLikeNotification> replyLikeNotificationList = replyLikeNotificationRepo.findByUser(user).orElse(null);
        if (replyLikeNotificationList != null) {
            for (ReplyLikeNotification replyLikeNotification : replyLikeNotificationList) {
                notifications.add(toReplyLikeNotificationDto(replyLikeNotification));
            }
        }

        if (notifications.size() == 0) throw new Exception("알림이 없습니다.");
        notifications.sort(Comparator.comparing(NotificationDTO::getTimestamp).reversed());

        return notifications;
    }

    /**\
     *
     * @param type : 1=댓글알림, 2=팔로우알림, 3=댓글좋아요, 4=게시글좋아요
     */
    @Override
    public void deleteOneNotice(Users user, Long notificationSeq, int type) throws Exception {
        if (type == 1){
            if (!replyNotificationRepo.existsById(notificationSeq)) throw new Exception("존재하지 않는 알림입니다.");
            replyNotificationRepo.deleteById(notificationSeq);
        } else if (type == 2) {
            if (!followNotificationRepo.existsById(notificationSeq)) throw new Exception("존재하지 않는 알림입니다.");
            followNotificationRepo.deleteById(notificationSeq);
        } else if (type == 3) {
            if (!replyLikeNotificationRepo.existsById(notificationSeq)) throw new Exception("존재하지 않는 알림입니다.");
            replyLikeNotificationRepo.deleteById(notificationSeq);
        } else if (type == 4) {
            if (!articleLikeNotificationRepo.existsById(notificationSeq)) throw new Exception("존재하지 않는 알림입니다.");
            articleLikeNotificationRepo.deleteById(notificationSeq);
        }

    }

    /**
     *
     * @param user : 접속한 유저
     * @return : 삭제 성공 여부
     */
    @Override
    public boolean deleteAllNotice(Users user) throws Exception {
        // 4개의 알림 Repo에 전부 삭제쿼리 날려주기.
        replyNotificationRepo.deleteAllByUser(user);
        followNotificationRepo.deleteAllByUser(user);
        replyLikeNotificationRepo.deleteAllByUser(user);
        articleLikeNotificationRepo.deleteAllByUser(user);

        return true;
    }

    /**
     *
     * @param followNotification : 엔티티
     * @param isFollowed : 맞팔상태인지?
     * @return : 알림 DTO
     */
    public NotificationDTO toFollowNotificationDto(FollowNotification followNotification, boolean isFollowed) {
        NotificationDTO notificationDTO = FollowNotificationDTO.builder()
                    .followingUserId(followNotification.getFromUser().getUserId())
                    .followingUserNickName(followNotification.getFromUser().getNickname())
                    .content(followNotification.getFromUser().getUserId() + "님이 회원님을 팔로우 합니다.")
                    .notificationSeq(followNotification.getLikeNotificationSeq())
                    .isFollowing(isFollowed)
                    .imageUrl(followNotification.getFromUser().getProfileImageUrl())
                    .timestamp(followNotification.getNotificationDateTime())
                    .userTier(followNotification.getUser().getUserTier())
                    .type(2)
                    .build();

        return notificationDTO;
    }
    public NotificationDTO toArticleLikeNotificationDto(ArticleLikeNotification articleLikeNotification){
        NotificationDTO notificationDTO = null;
        if (articleLikeNotification.getType() == 1) {
            notificationDTO = ArticleLikeNotificationDTO.builder()
                    .notificationSeq(articleLikeNotification.getArticleLikeNotificationSeq())
                    .timestamp(articleLikeNotification.getNotificationDateTime())
                    //.imageUrl() 좋아요 하트 이미지 넣어줘야함
                    .articleContent(articleLikeNotification.getNotArticleSeq().getArticleText())
                    .content("게시글의 좋아요 수가 10개 이상입니다.")
                    .notArticleSeq(articleLikeNotification.getNotArticleSeq().getArticleSeq())
                    .type(4)
                    .build();
        } else if (articleLikeNotification.getType() == 2) {
            notificationDTO = ArticleLikeNotificationDTO.builder()
                    .notificationSeq(articleLikeNotification.getArticleLikeNotificationSeq())
                    .timestamp(articleLikeNotification.getNotificationDateTime())
                    //.imageUrl() 좋아요 하트 이미지 넣어줘야함
                    .articleContent(articleLikeNotification.getNotArticleSeq().getArticleText())
                    .content("게시글의 좋아요 수가 50개 이상입니다.")
                    .notArticleSeq(articleLikeNotification.getNotArticleSeq().getArticleSeq())
                    .type(4)
                    .build();
        } else if (articleLikeNotification.getType() == 3) {
            notificationDTO = ArticleLikeNotificationDTO.builder()
                    .notificationSeq(articleLikeNotification.getArticleLikeNotificationSeq())
                    .timestamp(articleLikeNotification.getNotificationDateTime())
                    //.imageUrl() 좋아요 하트 이미지 넣어줘야함
                    .articleContent(articleLikeNotification.getNotArticleSeq().getArticleText())
                    .content("게시글의 좋아요 수가 100개 이상입니다.")
                    .notArticleSeq(articleLikeNotification.getNotArticleSeq().getArticleSeq())
                    .type(4)
                    .build();
        }

        return notificationDTO;
    }

    public NotificationDTO toReplyNotificationDto(ReplyNotification replyNotification) {
        NotificationDTO notificationDTO = ReplyNotificationDTO.builder()
                .notificationSeq(replyNotification.getReplyNotificationSeq())
                .content("새로운 댓글이 달렸습니다. : " + replyNotification.getReply().getReplyText())
                .articleContent(replyNotification.getArticle().getArticleText())
                .timestamp(replyNotification.getNotificationDateTime())
                .imageUrl(replyNotification.getReply().getUser().getProfileImageUrl())
                .notArticleSeq(replyNotification.getReply().getArticle().getArticleSeq())
                .type(1)
                .build();

        return notificationDTO;
    }

    public NotificationDTO toReplyLikeNotificationDto(ReplyLikeNotification replyLikeNotification) {
        NotificationDTO notificationDTO = null;
        if (replyLikeNotification.getType() == 1) {
            notificationDTO = ReplyLikeNotificationDTO.builder()
                    .notificationSeq(replyLikeNotification.getReplyLikeNotificationSeq())
                    .content("댓글의 좋아요 수가 1개 이상입니다.")
                    .articleContent(replyLikeNotification.getReply().getArticle().getArticleText())
                    .notArticleSeq(replyLikeNotification.getReply().getArticle().getArticleSeq())
                    .timestamp(replyLikeNotification.getNotificationDateTime())
                    .type(3)
//                    .imageUrl() 이미지 url 넣어야함
                    .build();
        } else if (replyLikeNotification.getType() == 2) {
            notificationDTO = ReplyLikeNotificationDTO.builder()
                    .notificationSeq(replyLikeNotification.getReplyLikeNotificationSeq())
                    .content("댓글의 좋아요 수가 10개 이상입니다.")
                    .articleContent(replyLikeNotification.getReply().getArticle().getArticleText())
                    .notArticleSeq(replyLikeNotification.getReply().getArticle().getArticleSeq())
                    .timestamp(replyLikeNotification.getNotificationDateTime())
                    .type(3)
//                    .imageUrl() 이미지 url 넣어야함
                    .build();
        } else if (replyLikeNotification.getType() == 3) {
            notificationDTO = ReplyLikeNotificationDTO.builder()
                    .notificationSeq(replyLikeNotification.getReplyLikeNotificationSeq())
                    .content("댓글의 좋아요 수가 100개 이상입니다.")
                    .articleContent(replyLikeNotification.getReply().getArticle().getArticleText())
                    .notArticleSeq(replyLikeNotification.getReply().getArticle().getArticleSeq())
                    .timestamp(replyLikeNotification.getNotificationDateTime())
                    .type(3)
//                    .imageUrl() 이미지 url 넣어야함
                    .build();
        }

        return notificationDTO;
    }
}
