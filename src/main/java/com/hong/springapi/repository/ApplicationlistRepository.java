package com.hong.springapi.repository;

import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.ApplicationlistKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ApplicationlistRepository extends JpaRepository<Applicationlist, ApplicationlistKey> {

    @Query("select S from applicationlist S where S.study_id = :study_id " +
            "order by S.create_date DESC ")
    Optional<List<Applicationlist>> findAllByStudy_id(Long study_id);

    @Query("select S from applicationlist S where S.user_id = :user_id " +
            "order by S.create_date DESC ")
    Optional<List<Applicationlist>> findAllByUser_id(Long user_id);

    @Query("select S from applicationlist S where S.user_id = :user_id and S.study_id = :study_id")
    Optional<Applicationlist> findByUser_idAndStudy_id(Long user_id, Long study_id);

    // update와 delete시는 transactional, modifying 필요
    @Transactional
    @Modifying
    @Query("delete from applicationlist S where S.user_id = :user_id and S.study_id = :study_id")
    void deleteApplicationlist(Long user_id, Long study_id);
}
