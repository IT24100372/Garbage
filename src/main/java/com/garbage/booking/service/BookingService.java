package com.garbage.booking.service;

import com.garbage.booking.model.Booking;
import com.garbage.booking.model.Booking.BookingStatus;
import com.garbage.booking.model.User;
import com.garbage.booking.repository.BookingRepository;
import com.garbage.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    public Booking createBooking(Long userId, Booking booking) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (booking.getCollectionDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new RuntimeException("Collection date must be at least 1 day in advance.");
        }

        booking.setUser(user);
        double weight = (booking.getWeight() != null && booking.getWeight() > 0) ? booking.getWeight() : 1.0;
        booking.setWeight(weight);
        booking.setAmount(Math.round(booking.getWasteType().getPrice() * weight * 100.0) / 100.0);
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.COLLECTED) {
            throw new RuntimeException("Cannot modify a booking that has already been collected.");
        }

        booking.setPickupAddress(updatedBooking.getPickupAddress());
        booking.setWasteType(updatedBooking.getWasteType());
        booking.setCollectionDate(updatedBooking.getCollectionDate());
        booking.setTimeSlot(updatedBooking.getTimeSlot());
        booking.setSpecialInstructions(updatedBooking.getSpecialInstructions());
        double weight = (updatedBooking.getWeight() != null && updatedBooking.getWeight() > 0) ? updatedBooking.getWeight() : booking.getWeight() != null ? booking.getWeight() : 1.0;
        booking.setWeight(weight);
        booking.setAmount(Math.round(updatedBooking.getWasteType().getPrice() * weight * 100.0) / 100.0);
        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        if (booking.getStatus() == BookingStatus.COLLECTED) {
            throw new RuntimeException("Cannot cancel a completed booking.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        if (booking.getStatus() == BookingStatus.COLLECTED) {
            throw new RuntimeException("Cannot delete a booking that has already been collected.");
        }
        bookingRepository.deleteById(id);
    }

    public Long countByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }
}
