package com.hong.springapi.repository;

import com.hong.springapi.model.Applicationlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationlistRepository extends JpaRepository<Applicationlist,Long> {
    List<Applicationlist> findAllByStudyId(Long studyId);
    List<Applicationlist> findAllByUserId(Long userId);
}
