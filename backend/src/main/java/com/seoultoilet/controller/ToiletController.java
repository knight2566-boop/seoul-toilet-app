package com.seoultoilet.controller;

import com.seoultoilet.dto.ToiletResponse;
import com.seoultoilet.service.ToiletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/toilets")
public class ToiletController {

    private final ToiletService toiletService;

    public ToiletController(ToiletService toiletService) {
        this.toiletService = toiletService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<ToiletResponse>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1.5") double radius) {
        return ResponseEntity.ok(toiletService.findNearby(lat, lng, radius));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToiletResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toiletService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ToiletResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(toiletService.search(q));
    }
}
