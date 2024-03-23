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

        // 검색 결과 없는 경우
        if (searchUserSeqList == null || searchUserSeqList.isEmpty()) {
            return null;
        }

        // 프로필 카드 조회
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
        List<ArticleResponseDTO> searchArticleList;

        // 스크롤 맨처음인 경우 (첫번째 게시글부터)
        if (lastArticleId == null) {
            Page<Article> lastArticleIsnull = articleRep.findArticlesByArticleTextLastArticleIsnull(inputText, pageable);
            // 조회된 게시글 없는 경우
            if (lastArticleIsnull == null || lastArticleIsnull.getContent().isEmpty()){
                return null;
            }
            searchArticleList = checkAuthSearchArticleList(userSeq, lastArticleIsnull);
        }
        // 스크롤 하단인 경우 (n번째 게시글부터)
        else {
            Page<Article> articlesByArticleText = articleRep.findArticlesByArticleText(inputText, lastArticleId, pageable);
            searchArticleList = checkAuthSearchArticleList(userSeq, articlesByArticleText);
        }
        searchArticleList.sort(new Comparator<ArticleResponseDTO>() {
            @Override
            public int compare(ArticleResponseDTO o1, ArticleResponseDTO o2) {
                return o2.getWrittenDate().compareTo(o1.getWrittenDate());
            }
        });

        return searchArticleList;
    }

    /**
     * 검색된 게시글 권한에 따라 필터링
     * @param searchedArticleList
     * @return
     */
    private List<ArticleResponseDTO> checkAuthSearchArticleList (Long userSeq, Page<Article> searchedArticleList){
        List<ArticleResponseDTO> authArticleList = new ArrayList<>();

        for (Article searchArticle: searchedArticleList.getContent()){
            if (searchArticle.getStatus() == 1 || Objects.equals(searchArticle.getUser().getUserSeq(), userSeq)) {
                // 전체공개인 경우 혹은 내가 쓴 글인경우
                ArticleResponseDTO dto = articleService.findArticleResponse(userSeq, searchArticle.getArticleSeq());
                authArticleList.add(dto);
            }
            else if (searchArticle.getStatus() == 2) {
                // 맞팔공개인 경우
                Optional<Following> followCheck = followingRepo.findByFollowingAndFollowerUserSeqVer(userSeq, searchArticle.getUser().getUserSeq());
                if (followCheck.isPresent()){
                    ArticleResponseDTO dto = articleService.findArticleResponse(userSeq, searchArticle.getArticleSeq());
                    authArticleList.add(dto);
                }
            }
        }
        return authArticleList;
    }

}
