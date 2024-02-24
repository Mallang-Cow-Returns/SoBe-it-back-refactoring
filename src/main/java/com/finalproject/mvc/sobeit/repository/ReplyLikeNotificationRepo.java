package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.entity.ArticleLikeNotification;
import com.finalproject.mvc.sobeit.entity.ReplyLikeNotification;
import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyLikeNotificationRepo extends JpaRepository<ReplyLikeNotification, Long> {
    Optional<List<ReplyLikeNotification>> findByUser(Users user);
    @Modifying
    @Query("delete from ReplyLikeNotification r where r.user in :userSeq")
    void deleteAllByUser(@Param("userSeq") Users users);
}
