package com.garbage.booking.service;

import com.garbage.booking.dto.AdminBookingDto;
import com.garbage.booking.dto.AdminPaymentDto;
import com.garbage.booking.dto.AdminSummaryDto;
import com.garbage.booking.model.Booking;
import com.garbage.booking.model.Booking.BookingStatus;
import com.garbage.booking.model.Payment;
import com.garbage.booking.model.Payment.PaymentStatus;
import com.garbage.booking.model.User;
import com.garbage.booking.repository.BookingRepository;
import com.garbage.booking.repository.PaymentRepository;
import com.garbage.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public User adminLogin(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied. Admin credentials required.");
        }

        user.setPassword(null);
        return user;
    }

    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == User.Role.ADMIN)
                .orElse(false);
    }

    public List<AdminBookingDto> getBookings(String status, LocalDate date, String userSearch, String paymentStatus) {
        BookingStatus bookingStatus = parseBookingStatus(status);
        PaymentStatus parsedPaymentStatus = parsePaymentStatus(paymentStatus);
        boolean unpaidOnly = paymentStatus != null && paymentStatus.equalsIgnoreCase("UNPAID");

        List<AdminBookingDto> bookings = bookingRepository.findAdminBookings(
                        bookingStatus,
                        date,
                        unpaidOnly ? null : parsedPaymentStatus,
                        normalizeSearch(userSearch)
                )
                .stream()
                .map(AdminBookingDto::from)
                .toList();

        if (unpaidOnly) {
            return bookings.stream().filter(booking -> "UNPAID".equals(booking.getPaymentStatus())).toList();
        }

        return bookings;
    }

    public List<AdminBookingDto> getCompletedBookings(LocalDate date, String userSearch, String paymentStatus) {
        PaymentStatus parsedPaymentStatus = parsePaymentStatus(paymentStatus);
        boolean unpaidOnly = paymentStatus != null && paymentStatus.equalsIgnoreCase("UNPAID");

        List<AdminBookingDto> bookings = bookingRepository.findCompletedAdminBookings(
                BookingStatus.COLLECTED,
                        date,
                        unpaidOnly ? null : parsedPaymentStatus,
                        normalizeSearch(userSearch)
                )
                .stream()
                .map(AdminBookingDto::from)
                .toList();

        if (unpaidOnly) {
            return bookings.stream().filter(booking -> "UNPAID".equals(booking.getPaymentStatus())).toList();
        }

        return bookings;
    }

    public Booking approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot approve a cancelled booking.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public Booking markCompleted(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot complete a cancelled booking.");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for booking id: " + bookingId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Cannot complete booking until payment is marked as paid.");
        }

        booking.setStatus(BookingStatus.COLLECTED);
        return bookingRepository.save(booking);
    }

    public Booking collectCashAndComplete(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot collect cash for a cancelled booking.");
        }

        if (booking.getStatus() == BookingStatus.COLLECTED) {
            throw new RuntimeException("Booking is already completed.");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for booking id: " + bookingId));

        if (payment.getPaymentMethod() != Payment.PaymentMethod.CASH_ON_COLLECTION) {
            throw new RuntimeException("Cash collection is only available for Cash on Collection payments.");
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Cannot collect cash for a refunded payment.");
        }

        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new RuntimeException("Cannot collect cash for a failed payment record.");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.COLLECTED);
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.COLLECTED) {
            throw new RuntimeException("Cannot cancel a completed booking.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking refundBookingPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new RuntimeException("Manual refund is allowed only for cancelled bookings.");
        }

        var payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("No payment found for booking id: " + bookingId));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Payment already refunded for booking id: " + bookingId);
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded.");
        }

        if (payment.getPaymentMethod() != Payment.PaymentMethod.NET_BANKING) {
            throw new RuntimeException("Refund processing is enabled only for cancelled net banking bookings.");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        return booking;
    }

    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }
        bookingRepository.deleteById(bookingId);
    }

    public List<AdminPaymentDto> getPayments() {
        return paymentRepository.findAllWithBookingAndUser().stream().map(AdminPaymentDto::from).toList();
    }

    public AdminSummaryDto getSummary() {
        AdminSummaryDto summary = new AdminSummaryDto();
        summary.setTotalBookings(bookingRepository.count());
        summary.setCompletedBookings(bookingRepository.countByStatus(BookingStatus.COLLECTED));
        summary.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));
        summary.setCancelledBookings(bookingRepository.countByStatus(BookingStatus.CANCELLED));
        summary.setLatestBookingId(bookingRepository.findTopByOrderByIdDesc().map(Booking::getId).orElse(0L));
        return summary;
    }

    public List<AdminBookingDto> getNewBookings(Long afterId) {
        long safeAfterId = afterId == null ? 0L : afterId;
        return bookingRepository.findByIdGreaterThanOrderByCreatedAtDesc(safeAfterId)
                .stream()
                .map(AdminBookingDto::from)
                .toList();
    }

    public Map<String, String> actionMessage(String message) {
        return Map.of("message", message);
    }

    private BookingStatus parseBookingStatus(String status) {
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            return null;
        }

        if (status.equalsIgnoreCase("APPROVED")) {
            return BookingStatus.CONFIRMED;
        }

        if (status.equalsIgnoreCase("COMPLETED")) {
            return BookingStatus.COLLECTED;
        }

        return BookingStatus.valueOf(status.trim().toUpperCase());
    }

    private PaymentStatus parsePaymentStatus(String paymentStatus) {
        if (paymentStatus == null || paymentStatus.isBlank() || paymentStatus.equalsIgnoreCase("ALL")) {
            return null;
        }
        if (paymentStatus.equalsIgnoreCase("PAID")) {
            return PaymentStatus.SUCCESS;
        }
        if (paymentStatus.equalsIgnoreCase("UNPAID")) {
            return PaymentStatus.PENDING;
        }
        return PaymentStatus.valueOf(paymentStatus.trim().toUpperCase());
    }

    private String normalizeSearch(String rawSearch) {
        if (rawSearch == null || rawSearch.isBlank()) {
            return null;
        }
        return rawSearch.trim();
    }
}
