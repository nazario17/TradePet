package com.example.trade.service;

import com.example.trade.model.AuthorizedUser;
import com.example.trade.model.Item;
import com.example.trade.model.ROLE;
import com.example.trade.model.User;
import com.example.trade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    ItemService itemService;

    @Autowired
    UserRepository userRepository;

    public void createUser(User user) {
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

    public AuthorizedUser getByName(String username) {
        return (AuthorizedUser) userRepository.getByUsername(username);
    }

    public List<AuthorizedUser> getAllAuthorizedUsers() {
        return userRepository
                .findAll()
                .stream()
                .filter(user -> user instanceof AuthorizedUser && !(user.getRoles().contains(ROLE.ADMIN)))
                .map(user -> (AuthorizedUser) user)
                .collect(Collectors.toList());

    }

    public void calcBalance(AuthorizedUser user) {
        Long balance = 0L;
        for (Item item : itemService.getByUser(user)) {
            balance += item.getPrice();
        }
        user.setBalance(balance);
        userRepository.save(user);
    }

    public String getUserImage(User user) {
        byte[] imageData = (user.getImage());
        return Base64.getEncoder().encodeToString(imageData);
    }

    public void setImage(byte[] image, User user) {
        user.setImage(image);
    }


    public void addRole(ROLE role, User user) {
        user.getRoles().add(role);
        save(user);
    }

    public void banUser(User user) {
        user.setActive(false);
        save(user);
    }

    public Set<User> getBannedUsers() {
        return userRepository.findAllByActiveIsFalse();
    }

    public void save(User user) {
        userRepository.save(user);
    }
}


