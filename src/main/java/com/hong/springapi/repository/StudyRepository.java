package com.hong.springapi.repository;

import com.hong.springapi.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study,Long> {

}