package com.hong.springapi.repository;

import com.hong.springapi.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study,Long> {
    Optional<List<Study>> findAllByUserId(Long userId);
    Optional<Study> findByUserId(Long userId);
}
