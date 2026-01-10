package com.djokic.data;

import com.djokic.enumeration.RepetitionTypeEnum;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationCreateRequest {
    private int resourceId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private RepetitionTypeEnum repetitionType;
    private LocalDate repetitionEndDate;

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {this.date = date;}

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public RepetitionTypeEnum getRepetitionType() { return repetitionType; }
    public void setRepetitionType(RepetitionTypeEnum repetitionType) {
        this.repetitionType = repetitionType;
    }

    public LocalDate getRepetitionEndDate() { return repetitionEndDate; }
    public void setRepetitionEndDate(LocalDate repetitionEndDate) {
        this.repetitionEndDate = repetitionEndDate;
    }
}
