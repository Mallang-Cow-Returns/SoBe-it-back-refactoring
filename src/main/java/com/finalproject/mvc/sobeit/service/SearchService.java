package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.ArticleResponseDTO;
import com.finalproject.mvc.sobeit.dto.ProfileDTO;
import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface SearchService {
    /**
     * 사용자(users) 검색
     **/
    List<ProfileDTO> usersSearch(Users loggedInUser, String inputText);

    /**
     * 게시글(Articles) 검색
     **/
    List<ArticleResponseDTO> articlesSearch(Long userSeq, String inputText, Long lastArticleId);

}
