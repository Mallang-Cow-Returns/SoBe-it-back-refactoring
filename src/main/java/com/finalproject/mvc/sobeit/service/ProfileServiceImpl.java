package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.NowUserDTO;
import com.finalproject.mvc.sobeit.dto.ProfileDTO;
import com.finalproject.mvc.sobeit.dto.ProfileUserDTO;
import com.finalproject.mvc.sobeit.entity.*;
import com.finalproject.mvc.sobeit.repository.FollowNotificationRepo;
import com.finalproject.mvc.sobeit.repository.FollowingRepo;
import com.finalproject.mvc.sobeit.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserRepo userRepo;
    private final FollowingRepo followingRepo;
    private final FollowNotificationRepo followNotificationRepo;

    /**
     * 프로필 유저 정보 가져오기
     * @param loggedInUserId: 로그인한 유저 ID
     * @param userId: 해당 프로필의 주인 유저 ID
     * @return profileUserDTO
     * */
    @Override
    public ProfileUserDTO selectUserInfo(String loggedInUserId, String userId) {
        Users user = userRepo.findByUserId(userId);
        Users loggedUser = userRepo.findByUserId(loggedInUserId);

        if(user == null) throw new RuntimeException("사용자 정보가 없습니다.");

        ProfileUserDTO profileUserDTO = new ProfileUserDTO();
        profileUserDTO.setProfileImg(user.getProfileImageUrl());
        profileUserDTO.setNickname(user.getNickname());
        profileUserDTO.setUserId(user.getUserId());
        profileUserDTO.setIntroDetail(user.getIntroduction());
        profileUserDTO.setFollowingCnt(followingRepo.followingCnt(user));
        profileUserDTO.setFollowerCnt(followingRepo.followerCnt(user));



        if (loggedInUserId.equals(userId)){ // 1. 로그인한 유저와 해당 프로필의 유저가 같은 경우
            profileUserDTO.setStatus(1);
        } else { // 2. 로그인한 유저와 해당 프로필의 유저가 다른 경우
            Following following = followingRepo.findByFollowingAndFollower(loggedUser, user.getUserSeq()).orElse(null);
            if (following==null){ // 팔로우하지 않은 상태
                profileUserDTO.setStatus(2);
            } else{ // 팔로우한 상태
                profileUserDTO.setStatus(3);
            }
        }
        return profileUserDTO;
    }

    @Override
    public List<Article> selectArticles(String userId) {
        return null;
    }

    /**
     * 유저 프로필 편집 저장
     * 두 번째 parameter dto로 변경
     * @param loggedInUser
     * @param updateUser
     * @return Users : 프로필 편집한 후 update된 유저
     * */
    @Override
    public Users insertProfile(@AuthenticationPrincipal Users loggedInUser, Users updateUser) {
        Users user = userRepo.findByUserId(updateUser.getUserId());

        user.setNickname(updateUser.getNickname());
        user.setIntroduction(updateUser.getIntroduction());
        user.setProfileImageUrl(updateUser.getProfileImageUrl());

        return userRepo.save(user);
    }

    /**
     * userSeq에 따른 Users 정보를 profileDTO로 가져오기
     * : selectFollowing(), selectFollower()에서 사용됨.
     * : status는 추후 팔로잉/팔로우 버튼 구분에 필요.
     * @param loggedInUser
     * @param targetUserSeq
     * @return profileDTO
     * */
    @Override
    public ProfileDTO selectFollowingUser(@AuthenticationPrincipal Users loggedInUser, Long targetUserSeq) {
        Users targetUser = userRepo.findById(targetUserSeq).orElse(null);

        int status = 0; // loggedInUser가 targetUser를 팔로우하지 않은 상태

        if(loggedInUser.getUserSeq() == targetUser.getUserSeq()) {
            status = 1; // 본인
        } else {
            Following following = followingRepo.findByFollowingAndFollower(loggedInUser, targetUser.getUserSeq()).orElse(null);
            System.out.println("****\nfollowing\n*****" + following);
            if(following != null) status = 2; //  loggedInUser가 targetUser를 팔로우 중인 상태
        }

        ProfileDTO profileDTO = ProfileDTO.builder()
                .userSeq(targetUser.getUserSeq())
                .userId(targetUser.getUserId())
                .nickname(targetUser.getNickname())
                .userTier(targetUser.getUserTier())
                .introduction(targetUser.getIntroduction())
                .profileImgUrl(targetUser.getProfileImageUrl())
                .status(status)
                .build();

        return profileDTO;
    }

    /**
     * 팔로잉 정보(userSeq) 가져오기
     * : selectFollowing()에서 사용됨.
     * : @param이 팔로잉하고 있는 사용자들의 userSeq를 list로 반환
     * @param userSeq
     * @return userSeqList
     * */
    @Override
    public List<Long> selectFollowingUserSeq(Long userSeq) {
        Users user = userRepo.findById(userSeq).orElse(null);
        List<Long> userSeqList = followingRepo.findUserSeqThatUserFollows(user);

        return userSeqList;
    }

    /**
     * 팔로잉 정보(List) 가져오기
     * @param loggedInUser // 로그인 된 사용자
     * @param userId // 팔로잉 정보를 조회할 사용자
     * @return userList
     * */
    @Override
    public List<ProfileDTO> selectFollowing(@AuthenticationPrincipal Users loggedInUser, String userId) {
        Users user = userRepo.findByUserId(userId);

        List<Long> userSeqList = selectFollowingUserSeq(user.getUserSeq());
        if (userSeqList == null) {
            throw new RuntimeException("팔로잉 중인 사용자가 없습니다.");
        }

        List<ProfileDTO> userList = new ArrayList<>();
        userSeqList.forEach(u -> userList.add(selectFollowingUser(loggedInUser, u)));

        return userList;
    }

    /**
     * 팔로워 정보(userSeq) 가져오기
     * : selectFollower()에서 사용됨.
     * : @param을 팔로우하고 있는 사용자들의 userSeq를 list로 반환
     * @param userSeq
     * @return userSeqList
     * */
    @Override
    public List<Long> selectFollowerUserSeq(Long userSeq) {
        Users user = userRepo.findById(userSeq).orElse(null);
        List<Long> userSeqList = followingRepo.findUserSeqThatUserFollowing(user);

        return userSeqList;
    }

    /**
     * 팔로워 정보(List) 가져오기
     * @param loggedInUser // 로그인 된 사용자
     * @param userId // 팔로잉 정보를 조회할 사용자
     * @return userList
     * */
    @Override
    public List<ProfileDTO> selectFollower(@AuthenticationPrincipal Users loggedInUser, String userId) {
        Users user = userRepo.findByUserId(userId);
        List<Long> userSeqList = selectFollowerUserSeq(user.getUserSeq());

        if (userSeqList == null) {
            throw new RuntimeException("팔로잉 중인 사용자가 없습니다.");
        }

        List<ProfileDTO> userList = new ArrayList<>();
        userSeqList.forEach(u -> userList.add(selectFollowingUser(loggedInUser, u)));
        return userList;
    }

    /**
     * 팔로잉 해제
     * @param user
     * @param targetUserId
     * */
    @Override
    public void unfollow(Users user, String targetUserId) {
        Users followingUser = userRepo.findByUserId(targetUserId);

        // 팔로우하려는 사용자가 없음.
        if(followingUser == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        Following f = followingRepo.findByFollowingAndFollower(user, followingUser.getUserSeq()).orElse(null);

        // 서로 팔로잉 관계가 아닐 때
        if(f == null) {
            throw new RuntimeException("User not following " + followingUser.getNickname());
        }
        followingRepo.delete(f);
    }

    /**
     * 팔로우 추가
     * @param user
     * @param targetUserId
     * @return following
     * */
    @Override
    public Following follow(Users user, String targetUserId) {
        Users followingUser = userRepo.findByUserId(targetUserId);

        // 팔로우하려는 사용자가 없음.
        if(followingUser == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        if(user.getUserId().equals(targetUserId)){
            throw new RuntimeException("자기 자신은 팔로우할 수 없습니다.");
        }

        Following isFollowing = followingRepo.findByFollowingAndFollower(user, followingUser.getUserSeq()).orElse(null);

        // user가 targetUser를 이미 팔로우하고 있을 때
        if(isFollowing != null) {
            throw new RuntimeException("이미 팔로우 중입니다.");
        }

        Following f = new Following();
        f.setUser(user);
        f.setFollowingUserSeq(followingUser.getUserSeq());

        // 팔로우 알림 로직
        FollowNotification followNotification = FollowNotification.builder()
                .user(followingUser)
                .fromUser(user)
                .url("http://localhost:3000/profile/profileinfo/" + user.getUserId())
                .notificationDateTime(LocalDateTime.now())
                .build();

        followNotificationRepo.save(followNotification);

        return followingRepo.save(f);
    }

    @Override
    public NowUserDTO selectNowUser(Users user) {
        Users existingUser = userRepo.findById(user.getUserSeq()).orElse(null);

        if(existingUser==null){
            throw new RuntimeException("사용자가 존재하지 않습니다.");
        }
        NowUserDTO nowUserDTO = NowUserDTO.builder()
                .userId(existingUser.getUserId())
                .userSeq(existingUser.getUserSeq())
                .userTier(existingUser.getUserTier())
                .nickname(existingUser.getNickname())
                .profileImgUrl(existingUser.getProfileImageUrl())
                .build();
        return nowUserDTO;
    }
}