package com.questioner.repository;

import com.questioner.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    /**
     * @param account 要存储的account
     * @return 返回已经存储在数据库的account
     */
    Account save(Account account);
    /**
     * @param loginUsername
     * @return 返回符合loginUsername的account对象
     */
    Account findByLoginUsername(String loginUsername);
    /**
     * @param email
     * @return 返回符合email的account对象
     */
    Account findByEmail(String email);

    @Query("select count(f.id) from Account a join a.followers f where a.id = ?1")
    Long getUserFollowersCount(Long userId);

    @Query("select count(f.id) from Account  a join a.followed f where a.id = ?1")
    Long getUserFollowedCount(Long userId); //得到用户关注的人的数量

    @Query("select f from Account a join a.followers f where a.id = ?1")
    Page<Account> getUserFollowers(Long userId, Pageable pageable);

    @Query("select f from Account a join a.followed f where a.id = ?1")
    Page<Account> getUserFollowed(Long userId, Pageable pageable);

    @Query("select count(a.id) from Account a join a.followed f where a.id = ?1 and f.id = ?2")
    int hasFollow(Long userId, Long followedId);
}
