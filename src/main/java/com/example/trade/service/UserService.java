package com.example.trade.service;

import com.example.trade.model.AuthorizedUser;
import com.example.trade.model.ROLE;
import com.example.trade.model.User;
import com.example.trade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void createUser(User user){
        user.setRoles(Collections.singleton(ROLE.USER));
        user.setActive(true);
        userRepository.save(user);
    }

    public User getByName(String username){
        return userRepository.getByUsername(username);
    }
}
