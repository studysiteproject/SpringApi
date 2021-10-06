package com.hong.springapi.repository;

import com.hong.springapi.model.Categorylist;
import com.hong.springapi.model.CategorylistKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryListRepository extends JpaRepository<Categorylist, CategorylistKey> {
}
