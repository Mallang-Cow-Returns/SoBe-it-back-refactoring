package com.finalproject.mvc.sobeit.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ReplyLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long replyLikeSeq;

    @ManyToOne
    @JoinColumn(name = "replySeq", referencedColumnName = "replySeq", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "userSeq", referencedColumnName = "userSeq", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;
}
