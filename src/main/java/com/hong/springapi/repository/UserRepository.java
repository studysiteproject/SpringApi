package com.hong.springapi.repository;

import com.hong.springapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("select U from user U where U.user_id = :user_id")
    public Optional<User> findByUser_idQuery(
            @Param("user_id")String user_id);
}
