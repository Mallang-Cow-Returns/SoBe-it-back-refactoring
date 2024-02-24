package com.finalproject.mvc.sobeit.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindIdDTO {
    String inputUserName;
    String inputUserPhoneNumber;
}
