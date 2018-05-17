package com.questioner.service.abs;

import com.questioner.entity.Account;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {

    Account save(Account account);

    Account register(Account userToAdd);

    Account getUser(Long userId);

    Account getUserByEmail(String email);

    Long getUserFollowersCount(Long userId);

    Long getUserFollowedCount(Long userId);

    Page<Account> getUserFollowers(Long userId, int currentPage, int pageSize);

    Page<Account> getUserFollowed(Long userId, int currentPage, int pageSize);

    boolean followUser(Long userId, Long followedUserId);

    boolean unFollowUser(Long userId, Long followedUserId);

    boolean hasFollowUser(Long userId, Long followedUserId);

    boolean validateLoginUsername(String loginUsername);

    boolean validateEmail(String Email);

}
