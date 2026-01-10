package com.djokic.data;

import com.djokic.enumeration.RepetitionTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationDTO {
    private Reservation reservation;
    private RepetitionTypeEnum repetitionType;
    private LocalDate repetitionEndDate;

    public ReservationDTO(Reservation reservation, RepetitionTypeEnum repetitionType, LocalDate repetitionEndDate) {
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
}
