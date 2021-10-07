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

//    @Query("select S from study as S left join S.categorylists as C where S.title = ?1 and S.place = ?2 and C.tech_id = ?3")
//    public List<Study> findAllByTitleAndPlaceAndTechQuery(String title, String place, Long tech_id);


}
