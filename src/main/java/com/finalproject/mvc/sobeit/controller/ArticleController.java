package com.finalproject.mvc.sobeit.controller;

import com.finalproject.mvc.sobeit.dto.*;
import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.Users;
import com.finalproject.mvc.sobeit.entity.Vote;
import com.finalproject.mvc.sobeit.service.ArticleServiceImpl;
import com.finalproject.mvc.sobeit.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final ArticleServiceImpl articleService;
    private final S3Service s3Service;

    /**
     * 글 작성
     * @param user
     * @param articleDTO
     * @return 성공 시 작성된 글
     */
    @PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> writeArticle(@AuthenticationPrincipal Users user, @RequestPart("articleDTO") ArticleDTO articleDTO, @RequestPart(required = false) MultipartFile file) {
        try {
            // 서비스 이용해 글 저장
            Article article = articleService.writeArticle(user, articleDTO);

            /**
             * 이미지 파일을 S3에 업로드하고
             * 리턴받은 URL을 DB에 업데이트
             */
            if (file != null) {
                String imageUrl = s3Service.articleImageUpload(file, article.getArticleSeq());
                articleService.updateArticleImageUrl(article.getArticleSeq(), imageUrl);
            }
            // 파일 관련 로직 끝

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }


    /**
     * 글 수정
     * @param user
     * @param articleDTO
     * @return 성공 시 업데이트된 글
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateArticle(@AuthenticationPrincipal Users user, @RequestPart("articleDTO") ArticleDTO articleDTO, @RequestPart(required = false) MultipartFile file){
        try{
            // 서비스 이용해 글 저장
            Article updatedArticle = articleService.updateArticle(user, articleDTO);
            /**
             * 이미지 파일을 S3에 업로드하고
             * 리턴받은 URL을 DB에 업데이트
             */
            if (file != null) {
                String imageUrl = s3Service.articleImageUpload(file, updatedArticle.getArticleSeq());
                articleService.updateArticleImageUrl(updatedArticle.getArticleSeq(), imageUrl);
            }
            return ResponseEntity.ok().body(updatedArticle);
        } catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 글 삭제
     * @param user
     * @param { "articleSeq": 삭제할 글번호}
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteArticle(@AuthenticationPrincipal Users user, @RequestBody Map<String, Long> articleSeqMap){
        try{
            articleService.deleteArticle(user, articleSeqMap.get("articleSeq"));
            return ResponseEntity.ok().body("delete success");
        }
        catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 글 1개 조회
     * @param user
     * @param articleSeq
     * @return
     */
    @GetMapping("/detail")
    public ResponseEntity<?> articleDetail(@AuthenticationPrincipal Users user, Long articleSeq){
        try{
            ArticleResponseDTO articleResponseDTO = articleService.articleDetail(user, articleSeq);
            return ResponseEntity.ok().body(articleResponseDTO);
        } catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 피드 글 조회
     * @return
     */
    @GetMapping("/selectAll")
    public ResponseEntity<?> selectArticleAll(@AuthenticationPrincipal Users user, @RequestParam(required = false) Long lastArticleId, @RequestParam int size){
        try {
            List<ArticleResponseDTO> articleResponseDTOList = articleService.feed(user, size, lastArticleId);
            System.out.println("컨트롤러ㅎㅇ");
            System.out.println(articleResponseDTOList);

            return ResponseEntity.ok().body(articleResponseDTOList);
        } catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .noContent().build(); // Error 500
        }
    }

    /**
     * 글 좋아요
     * @param user
     * @param articleSeqMap
     * @return
     */
    @PostMapping("/like")
    public ResponseEntity<?> likeArticle(@AuthenticationPrincipal Users user, @RequestBody Map<String, Long> articleSeqMap){
        try{
            boolean like = articleService.likeArticle(user, articleSeqMap.get("articleSeq"));
            return ResponseEntity.ok().body(like);
        }
        catch(Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 투표하기
     * @param user
     * @param voteDTO
     * @return 성공 시 투표 정보 반환
     */
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@AuthenticationPrincipal Users user, @RequestBody VoteDTO voteDTO){
        try{
            Vote vote = articleService.voteArticle(user, voteDTO);
            return ResponseEntity.ok().body(vote);
        } catch(Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();

            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }

    }

    /**
     * 사이드바 인기 게시물 가져오기
     * */
    @PostMapping("/hotpost")
    public ResponseEntity<?> selectHotPost(@AuthenticationPrincipal Users user) {
        try {
            List<ArticleResponseDTO> list = articleService.selectHotPost(user);
            return ResponseEntity.ok().body(list);
        } catch(Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }

    /**
     * 작성한 글 목록 가져오기
     * : 로그인된 사용자 또는 다른 사용자가 작성한 글 목록 가져오기.
     *   추후 공개 여부에 따라 param을 @Authentication Users user, Users targetUser로 변경
     * @param
     * @return 작성한 글 목록
     * */
    @GetMapping("/list")
    public ResponseEntity<?> articleList(@AuthenticationPrincipal Users loggedInUser,
                                         @RequestParam String userId,
                                         @RequestParam(required = false) Long lastArticleId)
    {
        try {
            System.out.println("articleList");
            List<ArticleResponseDTO> list = articleService.getArticleList(loggedInUser, 4, userId, lastArticleId);
            return ResponseEntity.ok().body(list);
        } catch(Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .internalServerError() // Error 500
                    .body(responseDTO);
        }
    }
}
