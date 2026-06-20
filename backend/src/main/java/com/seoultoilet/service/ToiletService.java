package com.seoultoilet.service;

import com.seoultoilet.dto.ToiletResponse;
import com.seoultoilet.entity.Toilet;
import com.seoultoilet.repository.ReviewRepository;
import com.seoultoilet.repository.ToiletRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToiletService {

    private final ToiletRepository toiletRepository;
    private final ReviewRepository reviewRepository;

    public ToiletService(ToiletRepository toiletRepository, ReviewRepository reviewRepository) {
        this.toiletRepository = toiletRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<ToiletResponse> findNearby(double lat, double lng, double radius) {
        List<Toilet> toilets = toiletRepository.findNearby(lat, lng, radius);
        return toilets.stream()
                .map(t -> toResponse(t, distanceKm(lat, lng, t.getLat(), t.getLng())))
                .collect(Collectors.toList());
    }

    public ToiletResponse findById(Long id) {
        Toilet toilet = toiletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Toilet not found: " + id));
        return toResponse(toilet, null);
    }

    public List<ToiletResponse> search(String query) {
        return toiletRepository.findAll().stream()
                .filter(t -> t.getName() != null && t.getName().contains(query))
                .map(t -> toResponse(t, null))
                .collect(Collectors.toList());
    }

    private ToiletResponse toResponse(Toilet t, Double distanceKm) {
        ToiletResponse r = new ToiletResponse();
        r.setId(t.getId());
        r.setName(t.getName());
        r.setType(t.getType());
        r.setAddress(t.getAddress());
        r.setLat(t.getLat());
        r.setLng(t.getLng());
        r.setAgency(t.getAgency());
        r.setPhone(t.getPhone());
        r.setHours(t.getHours());
        r.setHoursDetail(t.getHoursDetail());
        r.setMaleToilet(t.getMaleToilet());
        r.setMaleUrinal(t.getMaleUrinal());
        r.setFemaleToilet(t.getFemaleToilet());
        r.setDisabledMale(t.getDisabledMale());
        r.setDisabledFemale(t.getDisabledFemale());
        r.setEmergencyBell(t.getEmergencyBell());
        r.setDiaperTable(t.getDiaperTable());
        r.setDistanceKm(distanceKm);
        r.setAverageRating(reviewRepository.findAverageRatingByToiletId(t.getId()));
        r.setReviewCount(reviewRepository.countByToiletId(t.getId()));
        return r;
    }

    public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
