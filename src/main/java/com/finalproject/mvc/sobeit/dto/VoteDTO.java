package com.finalproject.mvc.sobeit.dto;

import com.finalproject.mvc.sobeit.entity.Article;
import com.finalproject.mvc.sobeit.entity.Users;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VoteDTO {
    private Long articleSeq;
    private int voteType;
}
