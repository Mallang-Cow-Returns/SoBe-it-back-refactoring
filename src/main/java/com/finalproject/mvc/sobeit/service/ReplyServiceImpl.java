package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.ReplyDTO;
import com.finalproject.mvc.sobeit.dto.ReplyLikeDTO;
import com.finalproject.mvc.sobeit.dto.UserDTO;
import com.finalproject.mvc.sobeit.entity.*;
import com.finalproject.mvc.sobeit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService {
    private final ArticleRepo articleRepo;
    private final ReplyRepo replyRepo;
    private final ReplyLikeRepo replyLikeRepo;
    private final UserRepo userRepo;
    private final ReplyNotificationRepo replyNotificationRepo;
    private final ReplyLikeNotificationRepo replyLikeNotificationRepo;

    /**
     * 댓글 작성
     * @param user,
     * @param articleNum
     * @param reply
     */
    @Override
    public Reply writeReply(final Users user, Long articleNum, Reply reply){
        if (reply == null || user == null || articleNum == null) {
            throw new RuntimeException("Invalid arguments");
        }

        reply.setArticle(articleRepo.findByArticleSeq(articleNum));
        reply.setUser(user);
        reply.setWrittenDate(LocalDateTime.now());
        Reply savedReply = replyRepo.save(reply);

        /**
         * 댓글을 작성함에 따라 해당 글을 작성한 User의 seq을 기준으로 notificaiton 엔티티 저장
         */
        Article replyArticle = reply.getArticle(); // 댓글을 달 Article

        /**
         * 댓글 쓴 유저랑 게시글을 쓴 유저가 같은 유저가 아닐때만 알림 생성
         */
        if (!Objects.equals(user.getUserSeq(), replyArticle.getUser().getUserSeq())){
            Users userToSendNotification = replyArticle.getUser(); // 알림 등록할 유저
            String url = "http://localhost:3000/article/detail/" + replyArticle.getArticleSeq();
            ReplyNotification replyNotification = ReplyNotification.builder().user(userToSendNotification)
                    .reply(reply)
                    .article(replyArticle)
                    .notificationDateTime(LocalDateTime.now())
                    .url(url).build();
            replyNotificationRepo.save(replyNotification);
        }
        return savedReply;
    }

    /**
     * 댓글 수정
     * @param reply
     * @return
     */
    @Override
    public Reply updateReply(Reply reply){
        if (reply == null) {
            throw new RuntimeException("Invalid arguments");
        }

        Reply updatingReply = replyRepo.findByReplySeq(reply.getReplySeq()); // 업데이트할 댓글

        updatingReply.setReplyText(reply.getReplyText()); // 댓글 수정 내용 반영
        updatingReply.setIsUpdated(updatingReply.getIsUpdated() + 1); // 수정 횟수 추가

        return replyRepo.save(updatingReply);
    }

    /**
     * 댓글 삭제
     * @param user
     * @param replySeq
     * @return
     */
    @Override
    public void deleteReply(final Users user, Long replySeq){
        // 댓글 삭제 시 삭제된 댓글이라고 바꾸기
//        if (user == null || replyRepo.findByReplySeq(replySeq)==null) {
//            throw new RuntimeException("Invalid arguments");
//        }


//        if (replyRepo.existsByReplySeqAndUser(replySeq, user)) {
//            Reply deletingReply = replyRepo.findByReplySeqAndUser(replySeq, user); // 삭제하려는 댓글
//
//            deletingReply.setReplyText("삭제된 댓글입니다.");
//            deletingReply.setIsUpdated(-1);
//
//            Reply deletedReply = replyRepo.save(deletingReply);
//
//            ReplyDTO responseReplyDTO = ReplyDTO.builder()
//                    .reply_seq(deletedReply.getReplySeq())
//                    .reply_text(deletedReply.getReplyText())
//                    .is_updated(deletedReply.getIsUpdated())
//                    .build();
//
//            return responseReplyDTO;
//        }
//        else {
//            return null;
//        }

        // 댓글 삭제
        Reply existingReply = replyRepo.findById(replySeq).orElse(null);
        if (existingReply==null){
            throw new RuntimeException("삭제할 댓글이 없습니다.");
        }

        if (!existingReply.getUser().getUserSeq().equals(user.getUserSeq())){
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }
        replyRepo.deleteById(replySeq);
    }

    /**
     * 해당 글의 댓글 전체 조회
     * @param articleSeq
     * @return
     */
    @Override
    public List<ReplyDTO> selectAllReply(final Users user, Long articleSeq) {
        List<Reply> writtenReplyList = replyRepo.findReplyByArticleSeq(articleSeq); // 해당 글의 댓글 리스트
        Long articleUserSeq = articleRepo.findByArticleSeq(articleSeq).getUser().getUserSeq(); // 해당 글 작성자의 사용자 고유 번호

        List<ReplyDTO> responseReplyDTOList = new ArrayList<>();
        for (Reply writtenReply : writtenReplyList) { // 해당 글에 작성된 댓글의 개수만큼 반복
            int replyLikeCount = countReplyLike(writtenReply.getReplySeq()); // 해당 글에 작성된 댓글 중 하나의 댓글의 좋아요 개수
            Users replyWriter = userRepo.findByUserSeq(writtenReply.getUser().getUserSeq()); // 해당 글에 작성된 댓글 중 하나의 댓글의 작성자 정보
            boolean is_article_writer = false; // 글 작성자와 댓글 작성자가 같은지
            boolean is_reply_writer = false; // 현재 로그인한 사용자가 해당 댓글의 작성자인지

            if (Objects.equals(writtenReply.getUser().getUserSeq(), articleUserSeq)) {
                is_article_writer = true;
            }

            if (Objects.equals(user.getUserSeq(), writtenReply.getUser().getUserSeq())) {
                is_reply_writer = true;
            }

            ReplyLike replyLike;
            boolean is_clicked_like = false; // 현재 로그인한 사용자가 해당 댓글의 좋아요를 클릭했는지
            if (replyLikeRepo.existsByReplyAndUser(writtenReply, user)) {
                replyLike = replyLikeRepo.findByReplyAndUser(writtenReply, user);

                if (Objects.equals(user.getUserSeq(), replyLike.getUser().getUserSeq())) {
                    is_clicked_like = true;
                }
            }
            responseReplyDTOList.add(
                    ReplyDTO.builder()
                            .reply_seq(writtenReply.getReplySeq())
                            .user_id(writtenReply.getUser().getUserId())
                            .article_seq(writtenReply.getArticle().getArticleSeq())
                            .user_seq(writtenReply.getUser().getUserSeq())
                            .reply_text(writtenReply.getReplyText())
                            .parent_reply_seq(writtenReply.getParentReplySeq())
                            .written_date(writtenReply.getWrittenDate())
                            .reply_like_cnt(replyLikeCount)
                            .nickname(replyWriter.getNickname())
                            .user_tier(replyWriter.getUserTier())
                            .profile_image_url(replyWriter.getProfileImageUrl())
                            .is_article_writer(is_article_writer)
                            .is_reply_writer(is_reply_writer)
                            .is_clicked_like(is_clicked_like)
                            .build()
            );
        }

        return responseReplyDTOList;
    }

    /**
     * 댓글 작성자 찾기
     * @param userSeq
     * @return
     */
    public UserDTO selectReplyWriter(Long userSeq) {
        Users writer = replyRepo.findReplyUsersByUserSeq(userSeq);

        UserDTO responseUserDTO = UserDTO.builder()
                .user_id(writer.getUserId())
                .nickname(writer.getNickname())
                .user_tier(writer.getUserTier())
                .profile_image_url(writer.getProfileImageUrl())
                .build();

        return responseUserDTO;
    }


    /**
     * 댓글 좋아요
     * @param user
     * @param replySeq
     * @return
     */
    @Override
    public boolean likeReply(Users user, Long replySeq) {
        if (replyRepo.findByReplySeq(replySeq)== null){ // 댓글이 없는 경우 예외 발생
            throw new RuntimeException("좋아요할 댓글이 존재하지 않습니다.");
        }

        ReplyLike existingLike = replyLikeRepo.findByReplySeqAndUser(replySeq,user.getUserSeq());
        if (existingLike==null){ // 좋아요한 적 없으면 좋아요 생성
            Reply reply= replyRepo.findByReplySeq(replySeq);
            ReplyLike replyLike = ReplyLike.builder()
                    .reply(reply)
                    .user(user)
                    .build();
            replyLikeRepo.save(replyLike);

            Optional<Long> countByReply = replyLikeRepo.countByReply(replyLike.getReply()); // 해당 댓글의 좋아요 수
            if (countByReply.isPresent()) {
                Long replyLikeCnt = countByReply.get();
                Users userToSendNotification = replyLike.getReply().getUser();
                String url = "http://localhost:3000/article/detail/" + replyLike.getReply().getArticle().getArticleSeq();

                if (replyLikeCnt == 1) {
                    ReplyLikeNotification replyLikeNotification = ReplyLikeNotification.builder()
                            .reply(replyLike.getReply())
                            .type(1)
                            .notificationDateTime(LocalDateTime.now())
                            .url(url)
                            .user(userToSendNotification)
                            .build();
                    replyLikeNotificationRepo.save(replyLikeNotification);
                } else if (replyLikeCnt == 10) {
                    ReplyLikeNotification replyLikeNotification = ReplyLikeNotification.builder()
                            .reply(replyLike.getReply())
                            .type(2)
                            .notificationDateTime(LocalDateTime.now())
                            .url(url)
                            .user(userToSendNotification)
                            .build();
                    replyLikeNotificationRepo.save(replyLikeNotification);
                } else if (replyLikeCnt == 100) {
                    ReplyLikeNotification replyLikeNotification = ReplyLikeNotification.builder()
                            .reply(replyLike.getReply())
                            .type(3)
                            .notificationDateTime(LocalDateTime.now())
                            .url(url)
                            .user(userToSendNotification)
                            .build();
                    replyLikeNotificationRepo.save(replyLikeNotification);
                }
            }

            return true;
        }
        else { // 좋아요한 적 있으면 좋아요 취소(삭제)
            replyLikeRepo.delete(existingLike);
            return false;
        }
    }

    /**
     * 댓글 좋아요 개수 확인
     * @param replySeq
     * @return
     */
    public int countReplyLike(Long replySeq) {
        return replyLikeRepo.findCountReplyLikeByReplySeq(replySeq);
    }
}
