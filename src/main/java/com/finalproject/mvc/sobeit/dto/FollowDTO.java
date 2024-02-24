package com.finalproject.mvc.sobeit.dto;

import com.finalproject.mvc.sobeit.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="following")
    private Users following;

    @ManyToOne
    @JoinColumn(name="follower")
    private Users follower;
}
