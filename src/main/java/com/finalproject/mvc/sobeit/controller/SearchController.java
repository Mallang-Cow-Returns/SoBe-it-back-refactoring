package com.finalproject.mvc.sobeit.controller;

import com.finalproject.mvc.sobeit.dto.ArticleResponseDTO;
import com.finalproject.mvc.sobeit.dto.ProfileDTO;
import com.finalproject.mvc.sobeit.dto.ResponseDTO;
import com.finalproject.mvc.sobeit.entity.Users;
import com.finalproject.mvc.sobeit.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    /**
     * 사용자(users) 검색
     **/
    @RequestMapping("/users")
    public ResponseEntity<?> usersSearch(@AuthenticationPrincipal Users loggedInUser, @RequestBody Map<String, String> searchMap){
        try {
            List<ProfileDTO> userList = searchService.usersSearch(loggedInUser, searchMap.get("inputText"));

            return ResponseEntity.ok().body(userList);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 게시글(Articles) 검색
     **/
    @RequestMapping("/articles")
    public ResponseEntity<?> articlesSearch(@AuthenticationPrincipal Users user,
                                            @RequestParam String inputText,
                                            @RequestParam(required = false) Long lastArticleId){
        try {
            List<ArticleResponseDTO> articleSearchResults = searchService.articlesSearch(user.getUserSeq(), inputText, lastArticleId);
            return ResponseEntity.ok().body(articleSearchResults);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }

    }
}
