package com.hong.springapi.repository;

import com.hong.springapi.model.Technologylist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechnologylistRepository extends JpaRepository<Technologylist, Long> {
    Optional<Technologylist> findByName(String name);
}
