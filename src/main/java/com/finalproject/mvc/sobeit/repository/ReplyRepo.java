package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.entity.Reply;
import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepo extends JpaRepository<Reply, Long> {
    Reply findByReplySeq(Long replySeq);

    Reply findByReplySeqAndUser(Long replySeq, Users user);

    Boolean existsByReplySeqAndUser(Long replySeq, Users user);

    @Query("select count(*) from Reply r where r.article.articleSeq = ?1")
    int findReplyCountByArticleSeq(Long articleSeq);

    // 댓글을 작성한 사용자 정보 찾기
    @Query("select u from Reply r, Users u where r.user.userSeq = ?1")
    Users findReplyUsersByUserSeq(Long userSeq);

    // 해당 글에 작성된 댓글 찾기
    @Query("select r from Reply r where r.article.articleSeq = ?1 order by r.writtenDate")
    List<Reply> findReplyByArticleSeq(Long articleSeq);
}
