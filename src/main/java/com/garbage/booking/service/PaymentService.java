package com.garbage.booking.service;

import com.garbage.booking.model.Booking;
import com.garbage.booking.model.Booking.BookingStatus;
import com.garbage.booking.model.Payment;
import com.garbage.booking.model.Payment.PaymentStatus;
import com.garbage.booking.repository.BookingRepository;
import com.garbage.booking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Payment processPayment(Long bookingId, Payment paymentRequest) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (paymentRequest.getPaymentMethod() == null) {
            throw new RuntimeException("Payment method is required.");
        }

        // Check if booking already has a payment record
        Optional<Payment> existingPaymentOpt = paymentRepository.findByBookingId(bookingId);

        Payment payment;
        if (existingPaymentOpt.isPresent()) {
            Payment existing = existingPaymentOpt.get();
            if (existing.getStatus() == PaymentStatus.SUCCESS
                    && paymentRequest.getPaymentMethod() != Payment.PaymentMethod.CASH_ON_COLLECTION) {
                throw new RuntimeException("Payment already completed for booking: " + bookingId);
            }
            // Reuse the existing record for a retry (avoids unique-constraint violation)
            payment = existing;
        } else {
            payment = paymentRequest;
            payment.setBooking(booking);
            payment.setCreatedAt(LocalDateTime.now());
        }

        // Apply fields from the incoming request
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setCardHolderName(paymentRequest.getCardHolderName());
        payment.setCardLastFour(paymentRequest.getCardLastFour());
        payment.setAmount(booking.getAmount());
        payment.setTransactionId(generateTransactionId());

        if (payment.getPaymentMethod() == Payment.PaymentMethod.CASH_ON_COLLECTION) {
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentDate(null);
            bookingRepository.save(booking);
            return paymentRepository.save(payment);
        }

        // Simulate payment gateway processing for online payments
        boolean paymentSuccess = simulatePaymentGateway(payment);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByBooking_User_Id(userId);
    }

    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded.");
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        // Cancel the associated booking
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);
    }

    /**
     * Simulates payment gateway processing.
     * In production, integrate with Razorpay, Stripe, PayU, etc.
     * Returns true 95% of the time to simulate success.
     */
    private boolean simulatePaymentGateway(Payment payment) {
        // Simulate 95% success rate
        return Math.random() > 0.05;
    }
}
