package com.seoultoilet.controller;

import com.seoultoilet.dto.ReviewRequest;
import com.seoultoilet.entity.Review;
import com.seoultoilet.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/toilets/{toiletId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long toiletId) {
        return ResponseEntity.ok(reviewService.getReviews(toiletId));
    }

    @PostMapping
    public ResponseEntity<Review> addReview(
            @PathVariable Long toiletId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(toiletId, request));
    }

    @GetMapping("/average")
    public ResponseEntity<Map<String, Object>> getAverage(@PathVariable Long toiletId) {
        Double avg = reviewService.getAverageRating(toiletId);
        return ResponseEntity.ok(Map.of("averageRating", avg != null ? avg : 0.0));
    }
}
