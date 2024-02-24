package com.finalproject.mvc.sobeit.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long replySeq;

    @ManyToOne
    @JoinColumn(name = "userSeq", referencedColumnName = "userSeq", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "articleSeq", referencedColumnName = "articleSeq", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;

    @Column(nullable = false)
    private String replyText;

    private Long parentReplySeq;

    @Column(nullable = false)
    private LocalDateTime writtenDate;

    @Column(nullable = false)
    private Integer isUpdated; // 0 : 수정 X  // 1 이상 : 수정 O

    @PrePersist
    public void prePersist(){
        this.parentReplySeq = this.parentReplySeq == null ? 0L : this.parentReplySeq;
        this.isUpdated = this.isUpdated == null ? 0 : this.isUpdated;
    }
}
