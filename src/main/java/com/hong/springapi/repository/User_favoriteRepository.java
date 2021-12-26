package com.hong.springapi.repository;

import com.hong.springapi.model.Study;
import com.hong.springapi.model.User;
import com.hong.springapi.model.User_favorite;
import com.hong.springapi.model.User_favoriteKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface User_favoriteRepository extends JpaRepository<User_favorite, User_favoriteKey> {

    @Query("select distinct F.study_id from user_favorite F where F.user_id.id = :user_id")
    public List<Study> findDistinctAllByUser_idQuery(
            @Param("user_id") Long user_id
    );

    @Query("select F.study_id from user_favorite F where F.user_id.id = :user_id and F.study_id = :study_id ")
    public Optional<User_favorite> findByUser_favoriteKey(
            @Param("user_id")   Long user_id,
            @Param("study_id")  Long study_id);
}
