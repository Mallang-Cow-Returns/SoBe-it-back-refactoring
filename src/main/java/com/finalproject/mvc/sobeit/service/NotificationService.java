package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.dto.NotificationDTO;
import com.finalproject.mvc.sobeit.entity.Users;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getAllNotification(Users user) throws Exception;
    void deleteOneNotice(Users user, Long notificationSeq, int type) throws Exception;
    boolean deleteAllNotice(Users user) throws Exception;
}
