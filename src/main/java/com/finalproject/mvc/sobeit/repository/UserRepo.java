package com.finalproject.mvc.sobeit.repository;

import com.finalproject.mvc.sobeit.entity.Following;
import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface UserRepo extends JpaRepository<Users, Long>, QuerydslPredicateExecutor<Users> {

        Users findByUserSeq(Long userSeq);

        Users findByUserId(String userId);

        Users findByEmail(String email);

        Users findByUserIdAndPassword(String userId, String password);

        Boolean existsByUserId(String userId);

        Boolean existsByEmail(String email);

        Boolean existsByPhoneNumber(String phone_number);

        @Query("select u.userSeq from Users u where u.userId like %:inputText%")
        List<Long> findByText(@Param("inputText") String inputText);

        Users findByUserNameAndPhoneNumber(String userName, String phoneNumber);

//        @Query(value = "select u from Users u where u.userId like %?1%")
//        List<Users> followingCnt(String userId);

    }

