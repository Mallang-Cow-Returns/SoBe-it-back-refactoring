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
        Pageable pageable = PageRequest.of(0, 4); // 한 번에 글 4개 조회
        Page<Long> searchArticleSeqList;
        List<ArticleResponseDTO> searchArticleList = new ArrayList<>();

        if (lastArticleId == null) { // 스크롤 맨처음인 경우 (첫번째 게시글부터)
            searchArticleSeqList = articleRep.findArticlesByArticleTextLastArticleIsnull(userSeq, inputText, pageable);
        }
        else { // 스크롤 하단인 경우 (n번째 게시글부터)
            searchArticleSeqList = articleRep.findArticlesByArticleText(userSeq, inputText, lastArticleId, pageable);
        }

        // 조회된 게시글 없는 경우
        if (searchArticleSeqList == null || searchArticleSeqList.isEmpty()){
            return null;
        }

        // ArticleResponseDTO 가져오기
        searchArticleSeqList.getContent().forEach(f -> searchArticleList.add(articleService.findArticleResponse(userSeq, f)));
        return searchArticleList;
    }

}
