package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.Reply;
import com.finalproject.mvc.sobeit.entity.ReplyLike;
import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReplyLikeRepo extends JpaRepository<ReplyLike, Long> {
    ReplyLike findByReplyAndUser(Reply reply, Users user);

    @Query("select r from ReplyLike r where r.reply.replySeq = ?1 and r.user.userSeq = ?2")
    ReplyLike findByReplySeqAndUser(Long replySeq, Long userSeq);

    boolean existsByReplyAndUser(Reply reply, Users user);

    Optional<Long> countByReply(Reply reply);

    @Query("select count(*) from ReplyLike r where r.reply.replySeq = ?1")
    int findCountReplyLikeByReplySeq(Long replySeq);
}
