package com.hong.springapi.repository;

import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.ApplicationlistKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationlistRepository extends JpaRepository<Applicationlist, ApplicationlistKey> {

    @Query("select S from applicationlist S where S.study_id = :study_id")
    Optional<List<Applicationlist>> findAllByStudy_id(Long study_id);

    @Query("select S from applicationlist S where S.user_id = :user_id")
    Optional<List<Applicationlist>> findAllByUser_id(Long user_id);

    @Query("select S from applicationlist S where S.user_id = :user_id and S.study_id = :study_id")
    Optional<Applicationlist> findByUser_idAndStudy_id(Long user_id, Long study_id);
}
