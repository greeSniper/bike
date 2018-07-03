package com.tangzhe.bike.repository;

import com.tangzhe.bike.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 唐哲
 * 2018-06-02 10:38
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByMobile(String mobile);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickname WHERE u.id = :id")
    void updateNickName(@Param("id") Long id, @Param("nickname") String nickname);

}
