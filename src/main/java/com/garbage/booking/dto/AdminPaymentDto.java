package com.garbage.booking.dto;

import com.garbage.booking.model.Payment;

import java.time.LocalDateTime;

public class AdminPaymentDto {

    private Long paymentId;
    private Long bookingId;
    private String userName;
    private String userEmail;
    private Double amount;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;

    public static AdminPaymentDto from(Payment payment) {
        AdminPaymentDto dto = new AdminPaymentDto();
        dto.setPaymentId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null);
        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            dto.setPaymentStatus("PAID");
        } else if (payment.getStatus() == Payment.PaymentStatus.PENDING) {
            dto.setPaymentStatus("UNPAID");
        } else if (payment.getStatus() == Payment.PaymentStatus.FAILED) {
            dto.setPaymentStatus("FAILED");
        } else if (payment.getStatus() == Payment.PaymentStatus.REFUNDED) {
            dto.setPaymentStatus("REFUNDED");
        } else {
            dto.setPaymentStatus("UNPAID");
        }
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());

        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId());
            if (payment.getBooking().getUser() != null) {
                dto.setUserName(payment.getBooking().getUser().getName());
                dto.setUserEmail(payment.getBooking().getUser().getEmail());
            }
        }

        return dto;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}
