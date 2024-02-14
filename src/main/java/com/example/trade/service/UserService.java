package com.example.trade.service;

import com.example.trade.model.AuthorizedUser;
import com.example.trade.model.ROLE;
import com.example.trade.model.User;
import com.example.trade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void createUser(User user){
        AuthorizedUser authorizedUser = new AuthorizedUser(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                0L);
        authorizedUser.setActive(true);
        authorizedUser.setRoles(Collections.singleton(ROLE.USER));
        userRepository.save(authorizedUser);
    }

    public User getByName(String username){
        return userRepository.getByUsername(username);
    }

    public List<AuthorizedUser> getAllUsers(){
        return userRepository
                .findAll()
                .stream()
                .filter(AuthorizedUser.class::isInstance)
                .map(user -> (AuthorizedUser)user)
                .collect(Collectors.toList());
    }

    public void save(User user){
        User temp = userRepository.getByUsername(user.getUsername());
        if (temp!=null) {
            userRepository.delete(userRepository.getByUsername(user.getUsername()));
            userRepository.save(user);
        }
        else {
        userRepository.save(user);
        }
    }
}


