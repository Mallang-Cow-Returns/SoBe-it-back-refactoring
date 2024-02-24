package com.finalproject.mvc.sobeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeleteDTO {
    private String notificationSeq;
    private int type;
}
