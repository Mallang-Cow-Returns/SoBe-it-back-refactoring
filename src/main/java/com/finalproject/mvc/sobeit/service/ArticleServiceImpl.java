package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.ProfileDTO;
import com.finalproject.mvc.sobeit.entity.*;
import com.finalproject.mvc.sobeit.dto.ArticleDTO;
import com.finalproject.mvc.sobeit.dto.ArticleResponseDTO;
import com.finalproject.mvc.sobeit.dto.VoteDTO;
import com.finalproject.mvc.sobeit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService{

    private final ArticleRepo articleRepo;
    private final ArticleLikeRepo articleLikeRepo;
    private final VoteRepo voteRepo;
    private final ArticleLikeNotificationRepo articleLikeNotificationRepo;
    private final ReplyRepo replyRepo;
    private final FollowingRepo followingRepo;
    private final UserRepo userRepo;

    private final ProfileService profileService;

    /**
     * 글 작성
     * @param user
     * @param articleDTO
     * @return 저장된 글
     */
    @Override
    public Article writeArticle(Users user, ArticleDTO articleDTO) throws RuntimeException{
        // 요청 이용해 저장할 글 생성
        Article article = Article.builder()
                .user(user)
                .status(articleDTO.getStatus())
                .imageUrl(articleDTO.getImageUrl())
                .expenditureCategory(articleDTO.getExpenditureCategory())
                .amount(articleDTO.getAmount())
                .financialText(articleDTO.getFinancialText())
                .articleText(articleDTO.getArticleText())
                .writtenDate(LocalDateTime.now())
                .articleType(articleDTO.getArticleType())
                .consumptionDate(articleDTO.getConsumptionDate())
                .isAllowed(articleDTO.getIsAllowed())
                .build();
        return articleRepo.save(article);
    }

    @Override
    public void updateArticleImageUrl(Long articleSeq, String url){
        articleRepo.updateImageUrl(articleSeq, url);
    }
    /**
     * 글 수정
     * @param user
     * @param articleDTO
     * @return 수정된 글
     */
   @Override
    public Article updateArticle(Users user, ArticleDTO articleDTO) throws RuntimeException{
       Article existingArticle = articleRepo.findById(articleDTO.getArticleSeq()).orElse(null); // 수정할 글 가져오기
       if (existingArticle==null) { // 수정할 글이 없는 경우 예외 발생
           throw new RuntimeException("수정할 글이 없습니다.");
       }
       if (user.getUserSeq() != existingArticle.getUser().getUserSeq()){ // 기존 글의 작성자가 아니면 예외 발생
           throw new RuntimeException("글의 작성자가 아닙니다.");
       }

       // 수정될 글
       Article article = Article.builder()
               .user(user)
               .articleSeq(articleDTO.getArticleSeq())
               .status(articleDTO.getStatus())
               .imageUrl(articleDTO.getImageUrl())
               .expenditureCategory(articleDTO.getExpenditureCategory())
               .amount(articleDTO.getAmount())
               .financialText(articleDTO.getFinancialText())
               .articleText(articleDTO.getArticleText())
               .writtenDate(existingArticle.getWrittenDate())
               .articleType(existingArticle.getArticleType()) // 유형은 변경 불가
               .consumptionDate(articleDTO.getConsumptionDate())
               .editedDate(LocalDateTime.now())
               .isAllowed(articleDTO.getIsAllowed())
               .build();
        return articleRepo.save(article);
    }

    /**
     * 글 삭제
     * @param user
     * @param articleSeq
     */
    public void deleteArticle(Users user, Long articleSeq) throws RuntimeException {
        Article foundArticle = articleRepo.findById(articleSeq).orElse(null);
        if (foundArticle==null){ // 삭제할 글이 없는 경우
            throw new RuntimeException("삭제할 글이 없습니다.");
        }

        if (user.getUserSeq()!=foundArticle.getUser().getUserSeq()){ // 삭제 요청 유저가 작성자가 아닐 경우 예외 발생
            throw new RuntimeException("작성자가 아닙니다.");
        }
        articleRepo.deleteById(articleSeq);
    }

    /**
     * 디테일 페이지
     * @param articleSeq
     * @return
     */
    @Override
    public ArticleResponseDTO articleDetail(Users user, Long articleSeq) throws RuntimeException{
        Article article = selectArticleById(articleSeq);
        //글에 대한 권한 확인
        if (article.getStatus()==2 && !user.getUserSeq().equals(article.getUser().getUserSeq()) && !followToFollowCheck(user.getUserSeq(), article.getUser().getUserSeq())){
            throw new RuntimeException("맞팔로우의 유저만 확인 가능한 글입니다.");
        }
        else if(article.getStatus()==3 && !user.getUserSeq().equals(article.getUser().getUserSeq())){
            throw new RuntimeException("비공개 글입니다.");
        }

        // ArticleResponseDTO 반환
        return findArticleResponse(user.getUserSeq(), articleSeq);
    }

    /**
     * 아이디로 글 조회
     * @param articleSeq
     * @return 해당 번호 글
     */
    public Article selectArticleById(Long articleSeq) {
        return articleRepo.findById(articleSeq).orElse(null);
    }

    /**
     * 글 하나에 대한 ArticleResponseDTO 가져오기
     * @param userSeq 조회 요청한 유저 번호
     * @param articleSeq 조회하려는 글 번호
     * @return
     */
    public ArticleResponseDTO findArticleResponse(Long userSeq, Long articleSeq) {
        // 보려는 글 가져오기
        Article article = selectArticleById(articleSeq);

        if (article == null){ // 글이 없는 경우 예외 발생
            throw new RuntimeException("글이 존재하지 않습니다.");
        }

        // 내 글인지 확인
        boolean isMine = (userSeq.equals(article.getUser().getUserSeq()));

        // 댓글 수 가져오기
        //int replyCnt = 0;
        int replyCnt = replyRepo.findReplyCountByArticleSeq(articleSeq);

        // 좋아요 수 가져오기
        int likeCnt = countArticleLike(articleSeq);

        // 좋아요 여부 확인
        ArticleLike articleLike = findArticleLike(userSeq, articleSeq);
        boolean isLiked = true;
        if (articleLike==null) isLiked = false;

        // 투표율 가져오기
        int [] voteInfo = findVoteInfo(articleSeq);

        // 투표여부 가져오기
        boolean isVoted = isMine?true:voteCheck(userSeq, articleSeq); // 내 글이면 무조건 true

        // ArticleResponseDTO 반환
        ArticleResponseDTO articleResponseDTO = ArticleResponseDTO.builder()
                .articleSeq(articleSeq)
                .user(article.getUser())
                .status(article.getStatus())
                .imageUrl(article.getImageUrl())
                .expenditureCategory(article.getExpenditureCategory())
                .amount(article.getAmount())
                .articleText(article.getArticleText())
                .articleType(article.getArticleType())
                .consumptionDate(article.getConsumptionDate())
                .writtenDate(article.getWrittenDate())
                .financialText(article.getFinancialText())
                .isAllowed(article.getIsAllowed())
                .isMine(isMine)
                .commentCnt(replyCnt)
                .likeCnt(likeCnt)
                .isLiked(isLiked)
                .isVoted(isVoted)
                .agree(voteInfo[0]) // 찬성표수
                .disagree(voteInfo[1]) // 반대표수
                .agreeRate(voteInfo[2]) // 찬성표율
                .disagreeRate(voteInfo[3]) // 반대표율
                .build();

        return articleResponseDTO;
    }

    /**
     * 피드
     * @param user
     * @return 피드에 보여줄 글 반환
     */
    @Override
    public List<ArticleResponseDTO> feed(Users user, int size, Long lastArticleId) throws RuntimeException{
        Long userSeq = user.getUserSeq(); // 요청한 유저 번호


        // 권한에 맞는 글번호 리스트 가져오기
        Page<Long> feedSeqList = selectFeedArticleSeq(user.getUserSeq(), size, lastArticleId);
        if (feedSeqList.isEmpty()) { // 가져온 글이 없다면
            throw new RuntimeException("조회할 피드의 글이 없습니다.");
        }

        // ArticleResponseDTO 가져오기
        List<ArticleResponseDTO> feedList = new ArrayList<>();
        feedSeqList.getContent().forEach(f -> feedList.add(findArticleResponse(userSeq, f)));
        return feedList;
    }



    /**
     * 피드 글 번호 조회
     * @param userSeq
     * @return 유저가 볼 수 있는 권한의 글번호 리스트 최신순
     */
    public Page<Long> selectFeedArticleSeq(Long userSeq, int size, Long lastArticleId) {
        Pageable pageable = PageRequest.of(0, size);
        Page<Long> articleSeqs;
        if (lastArticleId == null) {
            articleSeqs = articleRepo.findArticleSeqListInFeedFirst(userSeq, pageable);
            return articleSeqs;
        }
        else {
            articleSeqs = articleRepo.findArticleSeqListInFeed(userSeq, lastArticleId, pageable);
            return articleSeqs;
        }
    }

    /**
     * 글 좋아요
     * @param user
     * @param articleSeq
     * @return
     * @throws RuntimeException
     */
    @Override
    public boolean likeArticle(Users user, Long articleSeq) throws RuntimeException{
        if (selectArticleById(articleSeq) == null){ // 글이 없는 경우 예외 발생
            throw new RuntimeException("좋아요할 글이 존재하지 않습니다.");
        }

        ArticleLike existingLike = findArticleLike(user.getUserSeq(), articleSeq); // 기존 좋아요가 있는 지 확인
        if (existingLike==null){ // 좋아요한 적 없으면 좋아요 생성
            Article articleById = selectArticleById(articleSeq);
            ArticleLike articleLike = ArticleLike.builder()
                    .article(articleById)
                    .user(user)
                    .build();
            articleLikeRepo.save(articleLike);

            /**
             * 좋아요 반영되고 나서 좋아요 수에 따라 알림 발생
             * type = 1 : 좋아요 10개
             * type = 2 : 좋아요 50개
             * type = 3 : 좋아요 100개
             * type = 4 : 좋아요 1000개
             *
             * 게시글에 좋아요를 누른 사람과 게시글을 작성한 사람이 같지 않으면 알림 생성 후 발성
             */
            if (!Objects.equals(user.getUserSeq(), articleById.getUser().getUserSeq())){
                Article likedArticle = articleLike.getArticle();

                Optional<Long> countByArticle = articleLikeRepo.countByArticle(likedArticle);
                if (countByArticle.isPresent()){
                    long articleLikeCnt = countByArticle.get();
                    Users userToSendNotification = likedArticle.getUser();
                    String url = "http://localhost:3000/article/detail/" + likedArticle.getArticleSeq();
                    if (articleLikeCnt == 1) {
                        // 좋아요 수가 10개라면
                        ArticleLikeNotification articleLikeNotification = ArticleLikeNotification.builder().type(1)
                                .user(userToSendNotification)
                                .url(url).notArticleSeq(likedArticle)
                                .notificationDateTime(LocalDateTime.now()).build();
                        articleLikeNotificationRepo.save(articleLikeNotification);
                    }
                    else if (articleLikeCnt == 3){
                        // 좋아요 수가 50개라면
                        ArticleLikeNotification articleLikeNotification = ArticleLikeNotification.builder().type(2)
                                .user(userToSendNotification)
                                .url(url).notArticleSeq(likedArticle)
                                .notificationDateTime(LocalDateTime.now()).build();
                        articleLikeNotificationRepo.save(articleLikeNotification);
                    } else if (articleLikeCnt == 5) {
                        // 좋아요 수가 100개라면
                        ArticleLikeNotification articleLikeNotification = ArticleLikeNotification.builder().type(3)
                                .user(userToSendNotification)
                                .url(url).notArticleSeq(likedArticle)
                                .notificationDateTime(LocalDateTime.now()).build();
                        articleLikeNotificationRepo.save(articleLikeNotification);

                    } else if (articleLikeCnt == 10) {
                        // 좋아요 수가 1000개라면
                        ArticleLikeNotification articleLikeNotification = ArticleLikeNotification.builder().type(4)
                                .user(userToSendNotification)
                                .url(url).notArticleSeq(likedArticle)
                                .notificationDateTime(LocalDateTime.now()).build();
                        articleLikeNotificationRepo.save(articleLikeNotification);
                    }
                }
            }
            return true;
        }
        else { // 좋아요한 적 있으면 좋아요 취소(삭제)
            articleLikeRepo.delete(existingLike);
            return false;
        }
    }

    /**
     * 기존 좋아요 확인
     * @param userSeq
     * @param articleSeq
     * @return
     */
    public ArticleLike findArticleLike(Long userSeq, Long articleSeq) {
        return articleLikeRepo.findArticleLikeByUserSeqAndArticleSeq(userSeq, articleSeq).orElse(null);
    }

    /**
     * 글 좋아요 수 확인
     * @param articleSeq
     * @return 좋아요 수
     */
    public int countArticleLike(Long articleSeq){
        return articleLikeRepo.findCountArticleLikeByArticleSeq(articleSeq);
    }

    /**
     * 투표하기
     * @param user
     * @param voteDTO
     * @return
     */
    public Vote voteArticle(Users user, VoteDTO voteDTO) throws RuntimeException{
        if (selectArticleById(voteDTO.getArticleSeq()) == null){ // 글이 없는 경우 예외 발생
            throw new RuntimeException("투표할 글이 존재하지 않습니다.");
        }
        // 결재 글이 맞는 지 확인
        if (selectArticleById(voteDTO.getArticleSeq()).getArticleType()!=2){
            throw new RuntimeException("투표가 가능한 글이 아닙니다.");
        }
        // 이 사용자가 이 글에 투표한 적이 있는 지 확인
        if (voteCheck(user.getUserSeq(), voteDTO.getArticleSeq())){
            throw new RuntimeException("이미 투표 완료했습니다.");
        }
        // 투표 생성
        Vote vote = Vote.builder()
                .article(selectArticleById(voteDTO.getArticleSeq()))
                .user(user)
                .vote(voteDTO.getVoteType())
                .build();

        // 투표 저장
        Vote votedVote = voteRepo.save(vote);
        if (votedVote == null) {
            throw new RuntimeException("투표 실패");
        }
        return votedVote;
    }

    /**
     * 해당 사용자의 해당 글에 대한 투표 여부 확인
     * @param userSeq
     * @param articleSeq
     * @return true면 투표한 적 있음 / false면 투표한 적 없음
     */
    public boolean voteCheck(Long userSeq, Long articleSeq){
        Vote existingVote = voteRepo.findVoteByUserSeqAndArticleSeq(userSeq, articleSeq).orElse(null);
        if (existingVote==null) return false;
        return true;
    }

    /**
     * 투표수, 투표율 확인
     * @param articleSeq
     * @return [0] : 찬성표수, [1] : 반대표수, [2] : 찬성표율, [3] : 반대표율
     */
    public int[] findVoteInfo(Long articleSeq){
        int[] voteInfo = new int [4];
        // 투표수 확인
        voteInfo[0] = voteRepo.findAgreeCountByArticleSeq(articleSeq);
        voteInfo[1] = voteRepo.findDisagreeCountByArticleSeq(articleSeq);

        // 투표율 계산
        int agreeRate = 0;
        int disagreeRate = 0;
        if (voteInfo[0]!=0 || voteInfo[1]!=0) { // 투표수가 0이 아니라면
            agreeRate = (int)((voteInfo[0]*1.0)/(voteInfo[0]+voteInfo[1]) * 100);
            disagreeRate = 100 - agreeRate;
        }
        voteInfo[2] = agreeRate;
        voteInfo[3] = disagreeRate;
        return voteInfo;
    }

    /**
     * 맞팔 확인
     * @param userSeq 요청 유저 번호
     * @param targetUserSeq 상대 유저 번호
     * @return
     */
    public boolean followToFollowCheck(Long userSeq, Long targetUserSeq) {
        if (followingRepo.findByFollowingAndFollower(userRepo.findByUserSeq(userSeq), targetUserSeq).orElse(null)==null){
            return false;
        }
        else if (followingRepo.findByFollowingAndFollower(userRepo.findByUserSeq(targetUserSeq), userSeq).orElse(null)==null){
            return false;
        }
        return true;
    }

    /**
     * 사이드바 인기 게시물(articleSeq) 상위 세 개 가져오기
     * */
    @Override
    public List<Long> selectHotPostSeq() {
        List<Long> list = articleLikeRepo.findHotPostSeq();//.subList(0, 3);

        System.out.println("\n사이드바~~~~~\n");
        System.out.println(list);

        return list;
    }

    /**
     * 사이드바 인기 게시물 가져오기
     * */
    @Override
    public List<ArticleResponseDTO> selectHotPost(Users user) {
        List<Long> seqList = selectHotPostSeq();

        if (seqList.isEmpty()) { // 가져온 글이 없다면
            throw new RuntimeException("조회할 글이 없습니다.");
        }

        List<ArticleResponseDTO> articleList = new ArrayList<>();
        seqList.forEach(f -> articleList.add(findArticleResponse(user.getUserSeq(), f)));

        return articleList;
    }

    /**
     * 작성한 글의 articleSeq 가져오기
     * @param loggedInUser
     * @param size
     * @param userId
     * @return userSeqList
     * */
    @Override
    public Page<Long> selectArticleSeq(Users loggedInUser, int size, String userId, Long lastArticleId) {
        Pageable pageable = PageRequest.of(0, size);

        Users targetUser = userRepo.findByUserId(userId);
        Page<Long> userSeqList;

        // 1. 로그인한 유저 -> 본인 글(본인 글 selectAll)
        if(Objects.equals(loggedInUser.getUserId(), userId)) {
            if (lastArticleId == null) userSeqList = articleRepo.findProfileArticleSeqByLoginUserWhenLastArticleIdIsNull(loggedInUser.getUserSeq(), pageable);
            else userSeqList = articleRepo.findProfileArticleSeqByLoginUser(loggedInUser.getUserSeq(), pageable, lastArticleId);
        } else {
            // 2. 로그인한 유저 -> 맞팔인 유저의 글(상대의 맞팔 공개 글)
            if(followingRepo.findByFollowingAndFollower(loggedInUser, targetUser.getUserSeq()) != null) {
                if (lastArticleId == null) userSeqList = articleRepo.findProfileArticleSeqByFollowedUserWhenLastArticleIdIsNull(targetUser.getUserSeq(), pageable);
                else userSeqList = articleRepo.findProfileArticleSeqByFollowedUser(targetUser.getUserSeq(), pageable, lastArticleId);

                // 3. 로그인한 유저 -> 맞팔이 아닌 유저의 글(상대의 전체 공개 글)
            } else {
                if (lastArticleId == null) userSeqList = articleRepo.findProfileArticleSeqByUnknownUserWhenLastArticleIdIsNull(targetUser.getUserSeq(), pageable);
                else userSeqList = articleRepo.findProfileArticleSeqByUnknownUser(targetUser.getUserSeq(), pageable, lastArticleId);
            }

        }
        return userSeqList;
    }

    /**
     * 작성한 글(List) 가져오기
     * @param loggedInUser
     * @param size
     * @param userId
     * @return articleList
     * */
    @Override
    public List<ArticleResponseDTO> getArticleList(Users loggedInUser, int size, String userId, Long lastArticleId) throws RuntimeException {
        Users targetUser = userRepo.findByUserId(userId);

        // 권한에 맞는 글번호 리스트 가져오기
        Page<Long> feedSeqList = selectArticleSeq(loggedInUser, size, userId, lastArticleId);
        if (feedSeqList.isEmpty()) { // 가져온 글이 없다면
            throw new RuntimeException("조회할 피드의 글이 없습니다.");
        }

        // ArticleResponseDTO 가져오기
        List<ArticleResponseDTO> articleList = new ArrayList<>();

        // 로그인한 유저 -> 본인 글(본인 글 selectAll)
        if(Objects.equals(loggedInUser.getUserId(), userId)) {
            feedSeqList.getContent().forEach(f -> articleList.add(findArticleResponse(loggedInUser.getUserSeq(), f)));
        } else {
            feedSeqList.getContent().forEach(f -> articleList.add(findArticleResponse(targetUser.getUserSeq(), f)));
        }
        return articleList;
    }


}
