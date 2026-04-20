package com.garbage.booking.repository;

import com.garbage.booking.model.Booking;
import com.garbage.booking.model.Booking.BookingStatus;
import com.garbage.booking.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByCollectionDate(LocalDate date);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

        @Query("""
            SELECT b
            FROM Booking b
            LEFT JOIN FETCH b.user u
            LEFT JOIN FETCH b.payment p
            WHERE (:status IS NULL OR b.status = :status)
              AND (:collectionDate IS NULL OR b.collectionDate = :collectionDate)
              AND (:paymentStatus IS NULL OR p.status = :paymentStatus)
              AND (
                :search IS NULL
                OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(b.pickupAddress) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            ORDER BY b.createdAt DESC
            """)
        List<Booking> findAdminBookings(
            @Param("status") BookingStatus status,
            @Param("collectionDate") LocalDate collectionDate,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("search") String search
        );

        @Query("""
            SELECT b
            FROM Booking b
            LEFT JOIN FETCH b.user u
            LEFT JOIN FETCH b.payment p
              WHERE b.status = :status
              AND (:collectionDate IS NULL OR b.collectionDate = :collectionDate)
              AND (:paymentStatus IS NULL OR p.status = :paymentStatus)
              AND (
                :search IS NULL
                OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(b.pickupAddress) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            ORDER BY b.createdAt DESC
            """)
        List<Booking> findCompletedAdminBookings(
              @Param("status") BookingStatus status,
            @Param("collectionDate") LocalDate collectionDate,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("search") String search
        );

        List<Booking> findByIdGreaterThanOrderByCreatedAtDesc(Long id);

        Optional<Booking> findTopByOrderByIdDesc();
}
