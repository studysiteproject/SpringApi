package com.hong.springapi.repository;

import com.hong.springapi.model.Categorylist;
import com.hong.springapi.model.CategorylistKey;
import com.hong.springapi.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategorylistRepository extends JpaRepository<Categorylist, CategorylistKey> {

    @Query("select distinct C.study_id from categorylist C " +
            "left join C.study_id S where (:title is null or S.title = :title) " +
            "and (:place is null or S.place = :place) " +
            "and C.tech_id.name in :tech_name")
    public List<Study> findDistinctAllByTitleAndPlaceAndTechQuery(
            @Param("title") String title,
            @Param("place")String place,
            @Param("tech_name")List<String> tech_name);
}
