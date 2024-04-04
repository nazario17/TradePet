package com.example.trade.Service;

import com.example.trade.model.AuthorizedUser;
import com.example.trade.service.BanService;
import com.example.trade.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BanServiceTest {

    @InjectMocks
    private BanService banService;
    @Mock
    private UserService userService;

    @Test
    public void testUnbanUsersAfterThreeMonths() {

        //Створили юзера1
        AuthorizedUser user1 = new AuthorizedUser();
        user1.setUsername("A");
        user1.setActive(false);
        LocalDateTime banDateTime1 = LocalDateTime.now().minusMonths(4);
        user1.setBanTime(banDateTime1);

        //Створили юзера2
        AuthorizedUser user2 = new AuthorizedUser();
        user2.setUsername("B");
        user2.setActive(false);
        LocalDateTime banDateTime2 = LocalDateTime.now().minusMonths(2);
        user2.setBanTime(banDateTime2);

        Set<AuthorizedUser> bannedUsers = new HashSet<>();
        bannedUsers.add(user1);
        bannedUsers.add(user2);

        when(userService.getBannedUsers()).thenAnswer(invocation -> bannedUsers);

        banService.unbanUsersAfterThreeMonths();

        assertAll("Banned users",
                () -> assertTrue(user1.isActive()),
                () -> assertFalse(user2.isActive())
        );
        verify(userService,times(1)).save(user1);
        verify(userService,never()).save(user2);
    }
}
