package com.bartlomiejskura.mymemories.security;

import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {
    @Autowired
    private UserRepository userRepository;

    public boolean hasUserId(Authentication authentication, Long userId) {
        User user = userRepository.findByEmail(authentication.getName());
        return user.getID().equals(userId);
    }
}