package com.djokic.data;

import com.djokic.enumeration.ResourceTypeEnum;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Resource {
    private int id = -1;
    private String resourceName;
    private ResourceTypeEnum resourceType;
    private LocalTime timeFrom;
    private LocalTime timeTo;
    private int quantity = 1;
    private final LocalDateTime createdAt;

    public Resource() {
        createdAt = LocalDateTime.now();
    }

    public Resource(int id, String resourceName, ResourceTypeEnum resourceType, LocalTime timeFrom, LocalTime timeTo) {
        this.id = id;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.createdAt = LocalDateTime.now();
    }

    public Resource(int id, String resourceName, ResourceTypeEnum resourceType, LocalTime timeFrom, LocalTime timeTo, int quantity) {
        this.id = id;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return this.id;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ResourceTypeEnum getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    public LocalTime getTimeFrom() {
        return this.timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalTime getTimeTo() {
        return this.timeTo;
    }

    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resource{")
                .append("id=")
                .append(this.id)
                .append(", resourceName=")
                .append(this.resourceName)
                .append(", resourceType=")
                .append(this.resourceType)
                .append(", timeFrom=")
                .append(this.timeFrom)
                .append(", timeTo=")
                .append(this.timeTo)
                .append(", quantity=")
                .append(this.quantity)
                .append(", createdAt=")
                .append(this.createdAt)
                .append("}");
        return sb.toString();
    }
}
