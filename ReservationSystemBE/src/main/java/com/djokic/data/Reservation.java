package com.djokic.data;

import com.djokic.enumeration.ReservationStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Reservation {
    private int id = -1;
    private User user;
    private Resource resource;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ReservationStatusEnum status;
    private final LocalDateTime createdAt;

    public Reservation() {
        this.createdAt = LocalDateTime.now();
    }

    public Reservation(int id, User user, Resource resource, LocalDate date, LocalTime startTime, LocalTime endTime, ReservationStatusEnum status) {
        this.id = id;
        this.user = user;
        this.resource = resource;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        if (this.endTime != null && startTime.isAfter(this.endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (this.startTime != null && endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.endTime = endTime;
    }

    public ReservationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ReservationStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reservation{")
                .append("id=")
                .append(this.id)
                .append(", user=")
                .append(this.user != null ? this.user.getUserId() : "null")
                .append(", resource=")
                .append(this.resource != null ? this.resource.getId() : "null")
                .append(", date=")
                .append(this.date)
                .append(", startTime=")
                .append(this.startTime)
                .append(", endTime=")
                .append(this.endTime)
                .append(", status=")
                .append(this.status)
                .append(", createdAt=")
                .append(this.createdAt)
                .append("}");
        return sb.toString();
    }
}
