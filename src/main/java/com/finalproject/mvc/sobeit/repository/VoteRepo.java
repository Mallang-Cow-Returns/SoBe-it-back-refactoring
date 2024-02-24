package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepo extends JpaRepository <Vote, Long>{
    /**
     * 투표 여부 확인
     * @param userSeq
     * @param articleSeq
     * @return 투표했다면 vote, 투표한 적 없으면 null
     */
    @Query("select v from Vote v where v.user.userSeq =?1 and v.article.articleSeq =?2")
    Optional<Vote> findVoteByUserSeqAndArticleSeq(Long userSeq, Long articleSeq);

    /**
     * 찬성표 수 확인
     * @param articleSeq
     * @return 해당 글 찬성표 수
     */
    @Query("select count(*) from Vote v where v.article.articleSeq = ?1 and v.vote = 1")
    int findAgreeCountByArticleSeq(Long articleSeq);

    /**
     * 반대표 수 확인
     * @param articleSeq
     * @return 해당 글 반대표 수
     */
    @Query("select count(*) from Vote v where v.article.articleSeq = ?1 and v.vote = 2")
    int findDisagreeCountByArticleSeq(Long articleSeq);
}
