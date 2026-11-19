package edu.pbj.hospitaltriage.mockapi;

import java.util.List;

public class SlotAvailabilityResponse {

    private boolean hasSlots;
    private String recommendedSlotId;
    private String slotTime;
    private String department;
    private List<String> availableSlotTimes;
    private String timestamp;

    public SlotAvailabilityResponse() {}

    public SlotAvailabilityResponse(boolean hasSlots, String recommendedSlotId, String slotTime, String department, List<String> availableSlotTimes, String timestamp) {
        this.hasSlots = hasSlots;
        this.recommendedSlotId = recommendedSlotId;
        this.slotTime = slotTime;
        this.department = department;
        this.availableSlotTimes = availableSlotTimes;
        this.timestamp = timestamp;
    }

    public boolean isHasSlots() {
        return hasSlots;
    }

    public void setHasSlots(boolean hasSlots) {
        this.hasSlots = hasSlots;
    }

    public String getRecommendedSlotId() {
        return recommendedSlotId;
    }

    public void setRecommendedSlotId(String recommendedSlotId) {
        this.recommendedSlotId = recommendedSlotId;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<String> getAvailableSlotTimes() {
        return availableSlotTimes;
    }

    public void setAvailableSlotTimes(List<String> availableSlotTimes) {
        this.availableSlotTimes = availableSlotTimes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

