package com.example.trade.service;

import com.example.trade.model.AuthorizedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BanService {

    @Autowired
    private UserService userService;

    //Щодня перевіряємо, чи є в нас користувачі, термін бану яких закінчився
    @Scheduled(cron = "@daily")
    public void unbanUsersAfterThreeMonths() {
        Set<AuthorizedUser> bannedUsers = userService.getBannedUsers().stream()
                .map(u->(AuthorizedUser)(u)).collect(Collectors.toSet());

        for (AuthorizedUser user : bannedUsers) {
            LocalDateTime banDateTime = user.getBanTime();
            LocalDateTime unbanDateTime = banDateTime.plusMonths(3);

            // Перевіряємо, чи минув час для розбану
            if (LocalDateTime.now().isAfter(unbanDateTime)) {
                user.setActive(true);
                user.setBanTime(null);
                userService.save(user);
            }
        }
    }
}

