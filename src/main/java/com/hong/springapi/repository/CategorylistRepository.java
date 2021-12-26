package com.hong.springapi.repository;

import com.hong.springapi.model.Categorylist;
import com.hong.springapi.model.CategorylistKey;
import com.hong.springapi.model.Study;
import com.hong.springapi.model.Technologylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategorylistRepository extends JpaRepository<Categorylist, CategorylistKey> {
    @Query("select distinct C.study_id from categorylist C "+
            "left join C.study_id S "+
            "left join C.tech_id T "+
            "where (:title is null or S.title = :title) " +
            "and (:place is null or S.place = :place) " +
            "and (:category is null or T.category = :category) " +
            "and (C.tech_id.name in :tech_name)")
    public List<Study> findDistinctAllByTitleAndPlaceAndTechQuery(
            @Param("title") String title,
            @Param("place")String place,
            @Param("category")String category,
            @Param("tech_name")List<String> tech_name);

    @Query("select distinct C.study_id from categorylist C "+
            "left join C.study_id S "+
            "left join C.tech_id T "+
            "where (:title is null or S.title = :title) " +
            "and (:place is null or S.place = :place) " +
            "and (:category is null or T.category = :category)")
    public List<Study> findDistinctAllByTitleAndPlaceAndCategoryQuery(
            @Param("title") String title,
            @Param("place")String place,
            @Param("category")String category);

    @Query("select C.tech_id from categorylist C left join C.tech_id T " +
            "where C.study_id.id = :study_id")
    public List<Technologylist> findAllByStudy_idQuery(
            @Param("study_id") Long study_id);
}
