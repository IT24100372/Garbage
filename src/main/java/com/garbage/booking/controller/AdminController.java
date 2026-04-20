package com.garbage.booking.controller;

import com.garbage.booking.dto.AdminBookingDto;
import com.garbage.booking.dto.AdminPaymentDto;
import com.garbage.booking.dto.AdminSummaryDto;
import com.garbage.booking.model.Booking;
import com.garbage.booking.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/api/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            return ResponseEntity.ok(adminService.adminLogin(email, password));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/api/admin/auth/{userId}")
    public ResponseEntity<?> checkAdmin(@PathVariable Long userId) {
        if (adminService.isAdmin(userId)) {
            return ResponseEntity.ok(Map.of("authorized", true));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("authorized", false));
    }

    @GetMapping({"/api/admin/bookings", "/bookings"})
    public ResponseEntity<?> getBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false, name = "user") String userSearch,
            @RequestParam(required = false, name = "payment") String paymentStatus
    ) {
        try {
            LocalDate collectionDate = (date == null || date.isBlank()) ? null : LocalDate.parse(date);
            List<AdminBookingDto> bookings = adminService.getBookings(status, collectionDate, userSearch, paymentStatus);
            return ResponseEntity.ok(bookings);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/api/admin/bookings/completed")
    public ResponseEntity<?> getCompletedBookings(
            @RequestParam(required = false) String date,
            @RequestParam(required = false, name = "user") String userSearch,
            @RequestParam(required = false, name = "payment") String paymentStatus
    ) {
        try {
            LocalDate collectionDate = (date == null || date.isBlank()) ? null : LocalDate.parse(date);
            return ResponseEntity.ok(adminService.getCompletedBookings(collectionDate, userSearch, paymentStatus));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping({"/api/admin/booking/{id}/approve"})
    public ResponseEntity<?> approveBooking(@PathVariable Long id) {
        try {
            Booking updated = adminService.approveBooking(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping({"/api/admin/booking/{id}/complete", "/booking/{id}/complete"})
    public ResponseEntity<?> markBookingCompleted(@PathVariable Long id) {
        try {
            Booking updated = adminService.markCompleted(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/api/admin/booking/{id}/collect-cash")
    public ResponseEntity<?> collectCashAndComplete(@PathVariable Long id) {
        try {
            Booking updated = adminService.collectCashAndComplete(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping({"/api/admin/booking/{id}/cancel", "/api/admin/bookings/{id}/cancel"})
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            Booking updated = adminService.cancelBooking(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/api/admin/booking/{id}/refund")
    public ResponseEntity<?> refundBookingPayment(@PathVariable Long id) {
        try {
            Booking updated = adminService.refundBookingPayment(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping({"/api/admin/booking/{id}", "/booking/{id}"})
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            adminService.deleteBooking(id);
            return ResponseEntity.ok(adminService.actionMessage("Booking deleted successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping({"/api/admin/payments", "/payments"})
    public ResponseEntity<List<AdminPaymentDto>> getPayments() {
        return ResponseEntity.ok(adminService.getPayments());
    }

    @GetMapping("/api/admin/summary")
    public ResponseEntity<AdminSummaryDto> getSummary() {
        return ResponseEntity.ok(adminService.getSummary());
    }

    @GetMapping("/api/admin/notifications/new-bookings")
    public ResponseEntity<List<AdminBookingDto>> getNewBookings(
            @RequestParam(required = false, defaultValue = "0") Long afterId
    ) {
        return ResponseEntity.ok(adminService.getNewBookings(afterId));
    }
}
