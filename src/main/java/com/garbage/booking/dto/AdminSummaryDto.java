package com.garbage.booking.dto;

public class AdminSummaryDto {

    private Long totalBookings;
    private Long completedBookings;
    private Long pendingBookings;
    private Long cancelledBookings;
    private Long latestBookingId;

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Long getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(Long completedBookings) {
        this.completedBookings = completedBookings;
    }

    public Long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(Long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public Long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(Long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public Long getLatestBookingId() {
        return latestBookingId;
    }

    public void setLatestBookingId(Long latestBookingId) {
        this.latestBookingId = latestBookingId;
    }
}
