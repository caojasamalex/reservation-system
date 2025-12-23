package com.djokic.data;

import com.djokic.enumeration.RepetitionTypeEnum;

import java.time.LocalDate;

public class ReservationRepetition {
    private Reservation reservation;
    private RepetitionTypeEnum repetitionType;
    private LocalDate repetitionEndDate;

    public ReservationRepetition() {
    }

    public ReservationRepetition(Reservation reservation, RepetitionTypeEnum repetitionType, LocalDate repetitionEndDate) {
        this.reservation = reservation;
        this.repetitionType = repetitionType;
        this.repetitionEndDate = repetitionEndDate;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public RepetitionTypeEnum getRepetitionType() {
        return repetitionType;
    }

    public void setRepetitionType(RepetitionTypeEnum repetitionType) {
        this.repetitionType = repetitionType;
    }

    public LocalDate getRepetitionEndDate() {
        return repetitionEndDate;
    }

    public void setRepetitionEndDate(LocalDate repetitionEndDate) {
        this.repetitionEndDate = repetitionEndDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReservationRepetition{")
                .append("reservation=")
                .append(this.reservation != null ? this.reservation.getId() : "null")
                .append(", repetitionType=")
                .append(this.repetitionType)
                .append(", repetitionEndDate=")
                .append(this.repetitionEndDate)
                .append("}");
        return sb.toString();
    }
}
