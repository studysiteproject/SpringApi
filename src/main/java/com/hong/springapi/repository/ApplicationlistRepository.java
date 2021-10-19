package com.hong.springapi.repository;

import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.ApplicationlistKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationlistRepository extends JpaRepository<Applicationlist, ApplicationlistKey> {

    Optional<List<Applicationlist>> findAllByStudyId(Long studyId);
    Optional<List<Applicationlist>> findAllByUserId(Long userId);
}
