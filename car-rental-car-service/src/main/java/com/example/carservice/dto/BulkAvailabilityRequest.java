package com.example.carservice.dto;

import java.util.List;

public class BulkAvailabilityRequest {

    private List<Long> ids;
    private boolean available;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}