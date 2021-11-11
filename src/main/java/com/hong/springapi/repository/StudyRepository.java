package com.hong.springapi.repository;

import com.hong.springapi.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long>{

    public List<Study>findAllByTitle(String title);

    @Query("select S from study S where (:title is null or S.title = :title) " +
            "and (:place is null or S.place = :place)")
    public List<Study> findAllByTitleAndPlaceQuery(
            @Param("title") String title,
            @Param("place") String place
    );

    @Query("select S from study S where S.user_id = :user_id")
    Optional<List<Study>> findAllByUser_id(Long user_id);
}
