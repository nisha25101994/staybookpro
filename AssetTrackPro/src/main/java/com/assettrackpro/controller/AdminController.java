package com.assettrackpro.controller;

import com.assettrackpro.entity.AssetRequest;
import com.assettrackpro.entity.AssetRequestStatus;
import com.assettrackpro.entity.User;
import com.assettrackpro.repository.AssetRequestRepository;
import com.assettrackpro.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserRepository userRepo;
    private final AssetRequestRepository requestRepo;
    private final com.assettrackpro.service.AssetRequestService requestService; // Use service for complex logic

    public AdminController(UserRepository userRepo, AssetRequestRepository requestRepo, com.assettrackpro.service.AssetRequestService requestService) {
        this.userRepo = userRepo;
        this.requestRepo = requestRepo;
        this.requestService = requestService;
    }

    @GetMapping("/users")
    public org.springframework.data.domain.Page<User> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return userRepo.findAll(pageable);
    }

    @GetMapping("/requests")
    public org.springframework.data.domain.Page<AssetRequest> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return requestRepo.findAll(pageable);
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("totalUsers", userRepo.count());
        stats.put("totalRequests", requestRepo.count());
        stats.put("pendingRequests", requestRepo.findByStatus(com.assettrackpro.entity.AssetRequestStatus.PENDING, org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        return stats;
    }

    @PutMapping("/users/{id}/enable")
    public void enableUser(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(payload.get("enabled"));
        userRepo.save(user);
    }
    
    @PutMapping("/requests/{id}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {

        try {

            String statusValue = payload.get("status");

            if (statusValue == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Status is required"));
            }

            AssetRequestStatus newStatus = AssetRequestStatus.valueOf(statusValue);

            String currentUserEmail = authentication.getName();

            AssetRequest updatedRequest =
                    requestService.updateStatus(id, newStatus, currentUserEmail);

            return ResponseEntity.ok(updatedRequest);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid status value"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
