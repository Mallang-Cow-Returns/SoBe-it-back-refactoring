package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.NowUserDTO;
import com.finalproject.mvc.sobeit.dto.ProfileDTO;
import com.finalproject.mvc.sobeit.dto.ProfileUserDTO;
import com.finalproject.mvc.sobeit.entity.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface ProfileService {

    /**
     * 프로필 유저 정보 가져오기
     * @param loggedInUserId
     * @param userId
     * @return profileUserDTO
     */
    ProfileUserDTO selectUserInfo(String loggedInUserId, String userId);

    /**
     * 작성한 글 가져오기
     * @param userId
     * @return userArticles
     */
    List<Article> selectArticles(String userId);

    /**
     * 유저 프로필 편집 저장
     * @param loggedInUser
     * @param updateUser
     * @return Users : 프로필 편집한 후 update 된 유저
     */
    Users insertProfile(@AuthenticationPrincipal Users loggedInUser, Users updateUser);

    /**
     * userSeq에 따른 Users 정보를 profileDTO로 가져오기
     * @param loggedInUser
     * @param targetUserSeq
     * @return profileDTO
     * */
    ProfileDTO selectFollowingUser(@AuthenticationPrincipal Users loggedInUser, Long targetUserSeq);

    /**
     * 팔로잉 정보(userSeq) 가져오기
     * @param userSeq
     * @return userSeqList
     * */
    List<Long> selectFollowingUserSeq(Long userSeq);

    /**
     * 팔로잉 정보(List) 가져오기
     * @param loggedInUser // 로그인 된 사용자
     * @param userId // 팔로잉 정보를 조회할 사용자
     * @return userList
     * */
    List<ProfileDTO> selectFollowing(@AuthenticationPrincipal Users loggedInUser, String userId);

    /**
     * 팔로워 정보(userSeq) 가져오기
     * @param userSeq
     * @return userSeqList
     * */
    List<Long> selectFollowerUserSeq(Long userSeq);


    /**
     * 팔로워 정보(List) 가져오기
     * @param loggedInUser // 로그인 된 사용자
     * @param userId // 팔로잉 정보를 조회할 사용자
     * @return userList
     * */
    List<ProfileDTO> selectFollower(@AuthenticationPrincipal Users loggedInUser, String userId);

    /**
     * 팔로잉 해제
     * @param user
     * @param targetUserId
     */
    void unfollow(Users user, String targetUserId);

    /**
     * 팔로우 추가
     * @param user
     * @param targetUserId
     * @return following
     */
    Following follow(Users user, String targetUserId);

    /**
     * 현재 접속 유저 정보 가져오기
     * @param user
     * @return
     */
    NowUserDTO selectNowUser(Users user);
}