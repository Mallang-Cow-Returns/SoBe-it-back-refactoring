package com.finalproject.mvc.sobeit.service;

import com.finalproject.mvc.sobeit.entity.Users;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserService {
    public Users create(final Users users);

    public Users getByCredentials(final String user_id, final String password, final PasswordEncoder encoder);

    public Boolean checkUserId(final String user_id);

    public Users findUserId(String inputUserId, String inputUserPassword);
}
