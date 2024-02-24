package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface ArticleLikeRepo extends JpaRepository<ArticleLike, Long> {
    // 해당 유저가 해당 글에 한 좋아요 반환
    @Query("select a from ArticleLike a where a.user.userSeq=?1 and a.article.articleSeq = ?2")
    public Optional<ArticleLike> findArticleLikeByUserSeqAndArticleSeq(Long userSeq, Long articleSeq);

    @Query("select count(*) from ArticleLike  a where a.article.articleSeq = ?1")
    public int findCountArticleLikeByArticleSeq(Long articleSeq);
    Optional<Long> countByArticle(Article article);

//    @Query("select a.article.articleSeq, count(a) as likeCount from ArticleLike a group by a.article.articleSeq order by likeCount desc")
    @Query("SELECT l.article.articleSeq FROM ArticleLike l JOIN Article a on a.articleSeq = l.article.articleSeq where a.status = 1 GROUP BY l.article.articleSeq ORDER BY COUNT(l) DESC")
    public List<Long> findHotPostSeq();

}
