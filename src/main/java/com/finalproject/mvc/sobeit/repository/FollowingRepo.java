package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.dto.FollowDTO;
import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.Following;
import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface FollowingRepo extends JpaRepository<Following, Long>, QuerydslPredicateExecutor<Following> {

    @Query(value = "select count(f) from Following f where f.user.userSeq = :#{#user.userSeq}")
    int followingCnt(@Param("user") Users user);

    @Query(value = "select count(f) from Following f where f.followingUserSeq = :#{#user.userSeq}")
    int followerCnt(@Param("user") Users user);

    @Query("select a from Following f join Article a on a.user.userSeq = f.followingUserSeq where f.user.userSeq = :#{#user.userSeq}")
    List<Article> findArticleThatUserFollows(@Param("user") Users user);

    @Query("select u from Following f join Users u on u.userSeq = f.user.userSeq where f.user.userSeq = :#{#user.userSeq}")
    List<Users> findProfileThatUserFollows(@Param("user") Users user);

//    @Query("select f from Following f where f.user.userSeq = :#{#user.userSeq} and f.followingUserSeq = :targetUserId")
//    Optional<Following> isFollowing(@Param("user") Users user, Long targetUserSeq);

    // user가 팔로잉하는 사용자의 목록
    @Query("select f.followingUserSeq from Following f join Users u on u.userSeq = f.user.userSeq where f.user.userSeq = :#{#user.userSeq}")
    List<Long> findUserSeqThatUserFollows(@Param("user") Users user);

    // user를 팔로우하는 사용자의 목록
    @Query("select f.user.userSeq from Following f join Users u on u.userSeq = f.user.userSeq where f.followingUserSeq = :#{#user.userSeq}")
    List<Long> findUserSeqThatUserFollowing(@Param("user") Users user);

    @Query("select u from Following f join Users u on u.userSeq = f.followingUserSeq where f.followingUserSeq = :#{#user.userSeq}")
    List<Users> findProfileThatUserFollowing(@Param("user") Users user);

    @Query("select f from Following f where f.user.userSeq = :#{#user.userSeq} and f.followingUserSeq=:targetUserSeq")
    Optional<Following> findByFollowingAndFollower(@Param("user") Users user, Long targetUserSeq);
//    @Query("SELECT f1.followingUserSeq FROM Following f1, Following f2 WHERE f1.user = :user AND f1.followingUserSeq = f2.user.userSeq AND f2.followingUserSeq = :user.userSeq")
//    List<Long> findMutualFollowers(@Param("user") Users user);

    @Query("select f from Following f where f.user.userSeq = :#{#user} and f.followingUserSeq=:targetUserSeq")
    Optional<Following> findByFollowingAndFollowerUserSeqVer(@Param("user") Long user, Long targetUserSeq);
}
