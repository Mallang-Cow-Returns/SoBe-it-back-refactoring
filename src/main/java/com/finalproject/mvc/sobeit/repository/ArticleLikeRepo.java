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
    @Query(value="SELECT a.article_Seq\n" +
            "FROM Article_Like l JOIN Article a on a.article_Seq = l.article_Seq \n" +
            "WHERE a.status = 1 \n" +
            "GROUP BY a.article_Seq \n" +
            "ORDER BY COUNT(l) DESC \n" +
            "LIMIT 3", nativeQuery = true)
    public List<Long> findHotPostSeq();

}
