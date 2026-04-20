package com.garbage.booking.repository;

import com.garbage.booking.model.Payment;
import com.garbage.booking.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByBooking_User_Id(Long userId);

    @Query("""
            SELECT p
            FROM Payment p
            LEFT JOIN FETCH p.booking b
            LEFT JOIN FETCH b.user u
            ORDER BY p.createdAt DESC
            """)
    List<Payment> findAllWithBookingAndUser();
}
