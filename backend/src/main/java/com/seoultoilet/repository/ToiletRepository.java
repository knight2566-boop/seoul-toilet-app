package com.seoultoilet.repository;

import com.seoultoilet.entity.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToiletRepository extends JpaRepository<Toilet, Long> {

    @Query(value = """
        SELECT * FROM toilets
        WHERE lat IS NOT NULL AND lng IS NOT NULL
        AND (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(lat)) *
                cos(radians(lng) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(lat))
            )
        ) <= :radius
        ORDER BY (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(lat)) *
                cos(radians(lng) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(lat))
            )
        )
    """, nativeQuery = true)
    List<Toilet> findNearby(@Param("lat") double lat, @Param("lng") double lng, @Param("radius") double radius);

    List<Toilet> findByType(String type);

    @Query("SELECT DISTINCT t.type FROM Toilet t ORDER BY t.type")
    List<String> findDistinctTypes();
}
