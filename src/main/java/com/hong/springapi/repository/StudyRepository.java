package com.hong.springapi.repository;

import com.hong.springapi.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study,Long>{
   /* Study save(Study study);
    Optional<Study> findById(Long id);
    Optional<Study> findByTitle(String title);
    Optional<Study> findByUser_id(Long id);
    Optional<Study> findByPlace(String place);
    Optional<Study> findByCreate_Date(LocalDateTime create_time);
    Optional<Study> findByMaxman(int maxman);
    Optional<Study> findByNowman(int nowman);
    Optional<Study> findByWarn_cnt(int warn_cnt);

    List<Study> findAll();*/
}
