package com.hong.springapi.repository;

import com.hong.springapi.dto.User_info;
import com.hong.springapi.model.Profile_image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface Profile_imageRepository extends JpaRepository<Profile_image, Long> {
        @Query("select new com.hong.springapi.dto.User_info(P.id, P.user.user_name, P.img_url) " +
            "from profile_image P where P.id = :user_id")
    public Optional<User_info> findByUser_idQuery(
            @Param("user_id") Long userId);
}
