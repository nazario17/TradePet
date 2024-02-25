package com.example.trade.service;

import com.example.trade.model.*;
import com.example.trade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    ItemService itemService;

    @Autowired
    UserRepository userRepository;

    public void createUser(User user){
        AuthorizedUser authorizedUser = new AuthorizedUser(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                0L);
        authorizedUser.setActive(true);
        try {
            authorizedUser.setImage(Files.readAllBytes(Paths.get("src/main/resources/static/images/no_foto_pic.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        authorizedUser.setRoles(Collections.singleton(ROLE.USER));
        userRepository.save(authorizedUser);
    }

    public User getByName(String username){
        return userRepository.getByUsername(username);
    }

    public List<AuthorizedUser> getAllAuthorizedUsers(){
        return userRepository
                .findAll()
                .stream()
                .filter(user -> user instanceof AuthorizedUser && !(user.getRoles().contains(ROLE.ADMIN)))
                .map(user -> (AuthorizedUser) user)
                .collect(Collectors.toList());

    }

    public void calcBalance(AuthorizedUser user){
        Long balance = 0L;
        for (Item item: itemService.getByUser(user)){
            balance+=item.getPrice();
        }
        user.setBalance(balance);
        userRepository.save(user);

    }
    public String getUserImage(User user) {
        byte[] imageData = (user.getImage());
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        return base64Image;
    }


    public void save(AuthorizedUser user){
            userRepository.save(user);
    }
}


