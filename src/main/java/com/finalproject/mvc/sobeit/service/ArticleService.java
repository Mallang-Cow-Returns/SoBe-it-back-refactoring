package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.ArticleDTO;
import com.finalproject.mvc.sobeit.dto.ArticleResponseDTO;
import com.finalproject.mvc.sobeit.dto.VoteDTO;
import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.ArticleLike;
import com.finalproject.mvc.sobeit.entity.Users;
import com.finalproject.mvc.sobeit.entity.Vote;
import com.finalproject.mvc.sobeit.repository.ArticleLikeRepo;
import com.finalproject.mvc.sobeit.repository.ArticleRepo;
import com.finalproject.mvc.sobeit.repository.VoteRepo;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


public interface ArticleService {
    /**
     * 글 작성
     * @param user
     * @param articleDTO
     * @return 작성된 글
     */
    public Article writeArticle(Users user, ArticleDTO articleDTO);

    public void updateArticleImageUrl(Long articleSeq, String url);

    /**
     * 글 수정
     * @param user
     * @param articleDTO
     * @return 수정된 글
     */
    public Article updateArticle(Users user, ArticleDTO articleDTO);

    /**
     * 글 삭제
     * @param user
     * @param articleSeq
     */
    public void deleteArticle(Users user, Long articleSeq);

    /**
     * 디테일 페이지
     * @param articleSeq
     * @return
     */
    public ArticleResponseDTO articleDetail(Users user, Long articleSeq);

    /**
     * 피드
     * @param user
     * @return
     */
    public List<ArticleResponseDTO> feed(Users user, int size, Long lastArticleId);

    /**
     * 글 좋아요
     * @param user
     * @param articleSeq
     * @return true : 좋아요, false : 좋아요 취소
     */
    public boolean likeArticle(Users user, Long articleSeq);

    /**
     * 사이드바 인기 게시물(articleSeq) 상위 세 개 가져오기
     * */
    public List<Long> selectHotPostSeq();

    /**
     * 사이드바 인기 게시물 가져오기
     * */
    public List<ArticleResponseDTO> selectHotPost(Users user);

    /**
     * 작성한 글의 articleSeq 가져오기
     * @param loggedInUser
     * @param size
     * @param userId
     * @return userSeqList
     * */
    Page<Long> selectArticleSeq(Users loggedInUser, int size, String userId, Long lastArticleId);

    /**
     * 작성한 글(List) 가져오기
     * @param user
     * @param size
     * @param userId
     * @return articleList
     * */
    List<ArticleResponseDTO> getArticleList(Users user, int size, String userId, Long lastArticleId);
}
