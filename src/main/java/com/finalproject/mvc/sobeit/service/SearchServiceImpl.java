package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.ArticleResponseDTO;
import com.finalproject.mvc.sobeit.dto.ProfileDTO;
import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.Following;
import com.finalproject.mvc.sobeit.entity.Users;
import com.finalproject.mvc.sobeit.repository.ArticleRepo;
import com.finalproject.mvc.sobeit.repository.FollowingRepo;
import com.finalproject.mvc.sobeit.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchServiceImpl implements SearchService{

    private final UserRepo userRep;
    private final ArticleRepo articleRep;
    private final ArticleServiceImpl articleService;
    private final ProfileServiceImpl profileService;
    private final FollowingRepo followingRepo;
    /**
     * 사용자(users) 검색
     **/
    @Override
    public List<ProfileDTO> usersSearch(Users loggedInUser, String inputText) {
        List<Long> searchUserSeqList = userRep.findByText(inputText);

        if (searchUserSeqList == null || searchUserSeqList.size() == 0) {
            throw new RuntimeException("검색된 사용자가 없습니다.");
        }

        List<ProfileDTO> userList = new ArrayList<>();
        searchUserSeqList.forEach(u -> userList.add(profileService.selectFollowingUser(loggedInUser, u)));

        return userList;
    }

    /**
     * 게시글(Articles) 검색
     **/
    @Override
    public List<ArticleResponseDTO> articlesSearch(Long userSeq, String inputText, Long lastArticleId) {
        Pageable pageable = PageRequest.of(0, 4);
        List<ArticleResponseDTO> searchArticleList = new ArrayList<>();

        if (lastArticleId == null) {
            Page<Article> lastArticleIsnull = articleRep.findArticlesByArticleTextLastArticleIsnull(inputText, pageable);

            if (lastArticleIsnull == null || lastArticleIsnull.getContent().size() == 0){
                throw new RuntimeException("검색어가 포함된 게시글이 존재하지 않습니다.");
            }

            for (Article searchArticle: lastArticleIsnull.getContent()){
                if (searchArticle.getStatus() == 1 || Objects.equals(searchArticle.getUser().getUserSeq(), userSeq)) {
                    // 전체공개인 경우
                    ArticleResponseDTO dto = articleService.findArticleResponse(userSeq, searchArticle.getArticleSeq());
                    searchArticleList.add(dto);
                }
                else if (searchArticle.getStatus() == 2) {
                    // 맞팔공개인 경우
                    Optional<Following> followCheck = followingRepo.findByFollowingAndFollowerUserSeqVer(userSeq, searchArticle.getUser().getUserSeq());
                    if (followCheck.isPresent()){
                        ArticleResponseDTO dto = articleService.findArticleResponse(userSeq, searchArticle.getArticleSeq());
                        searchArticleList.add(dto);
                    }
                }
            }
        }
        else {
            Page<Article> articlesByArticleText = articleRep.findArticlesByArticleText(inputText, lastArticleId, pageable);
            for (Article searchArticle: articlesByArticleText.getContent()){
                if (searchArticle.getStatus() == 1 || Objects.equals(searchArticle.getUser().getUserSeq(), userSeq)) {
                    // 전체공개인 경우 혹은 내가 쓴 글인경우
                    ArticleResponseDTO dto = articleService.findArticleResponse(userSeq, searchArticle.getArticleSeq());
                    searchArticleList.add(dto);
                }
                else if (searchArticle.getStatus() == 2) {
                    // 맞팔공개인 경우
                    Optional<Following> followCheck = followingRepo.findByFollowingAndFollowerUserSeqVer(userSeq, searchArticle.getUser().getUserSeq());
                    if (followCheck.isPresent()){
                        ArticleResponseDTO dto = articleService.findArticleResponse(userSeq, searchArticle.getArticleSeq());
                        searchArticleList.add(dto);
                    }
                }
            }
        }
        searchArticleList.sort(new Comparator<ArticleResponseDTO>() {
            @Override
            public int compare(ArticleResponseDTO o1, ArticleResponseDTO o2) {
                return o2.getWrittenDate().compareTo(o1.getWrittenDate());
            }
        });
        System.out.println("테스트---------");
        System.out.println(searchArticleList);

        return searchArticleList;
    }

}
