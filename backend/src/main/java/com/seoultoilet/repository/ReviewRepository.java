package com.seoultoilet.repository;

import com.seoultoilet.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByToiletIdOrderByCreatedAtDesc(Long toiletId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.toiletId = :toiletId")
    Double findAverageRatingByToiletId(Long toiletId);

    long countByToiletId(Long toiletId);
}
