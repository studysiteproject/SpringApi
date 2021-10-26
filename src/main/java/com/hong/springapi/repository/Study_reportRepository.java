package com.hong.springapi.repository;

import com.hong.springapi.model.Study_report;
import com.hong.springapi.model.User_favoriteKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Study_reportRepository extends JpaRepository<Study_report, User_favoriteKey> {

}
