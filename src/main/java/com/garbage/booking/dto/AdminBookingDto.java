package com.garbage.booking.dto;

import com.garbage.booking.model.Booking;
import com.garbage.booking.model.Payment;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminBookingDto {

    private Long id;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String pickupAddress;
    private LocalDate collectionDate;
    private String wasteType;
    private String bookingStatus;
    private String paymentStatus;
    private Double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;

    public static AdminBookingDto from(Booking booking) {
        AdminBookingDto dto = new AdminBookingDto();
        dto.setId(booking.getId());
        dto.setPickupAddress(booking.getPickupAddress());
        dto.setCollectionDate(booking.getCollectionDate());
        dto.setWasteType(booking.getWasteType() != null ? booking.getWasteType().name() : null);
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            dto.setBookingStatus("APPROVED");
        } else if (booking.getStatus() == Booking.BookingStatus.COLLECTED) {
            dto.setBookingStatus("COMPLETED");
        } else {
            dto.setBookingStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        }
        dto.setAmount(booking.getAmount());
        dto.setCreatedAt(booking.getCreatedAt());

        if (booking.getUser() != null) {
            dto.setUserName(booking.getUser().getName());
            dto.setUserEmail(booking.getUser().getEmail());
            dto.setUserPhone(booking.getUser().getPhone());
        }

        Payment payment = booking.getPayment();
        if (payment != null) {
            if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
                dto.setPaymentStatus("PAID");
            } else if (payment.getStatus() == Payment.PaymentStatus.FAILED) {
                dto.setPaymentStatus("FAILED");
            } else if (payment.getStatus() == Payment.PaymentStatus.REFUNDED) {
                dto.setPaymentStatus("REFUNDED");
            } else {
                dto.setPaymentStatus("UNPAID");
            }
            dto.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null);
            dto.setPaymentDate(payment.getPaymentDate());
        } else {
            dto.setPaymentStatus("UNPAID");
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
