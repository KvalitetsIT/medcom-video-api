package dk.medcom.video.api.dao.entity;

import java.time.Instant;

public class PoolHistory {
    private Long id;
    private String organisationCode;
    private boolean poolEnabled;
    private Integer desiredPoolSize;
    private Integer availablePoolRooms;
    private Instant statusTime;
    private Instant createdTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganisationCode() {
        return organisationCode;
    }

    public void setOrganisationCode(String organisationCode) {
        this.organisationCode = organisationCode;
    }

    public boolean isPoolEnabled() {
        return poolEnabled;
    }

    public void setPoolEnabled(boolean poolEnabled) {
        this.poolEnabled = poolEnabled;
    }

    public Integer getDesiredPoolSize() {
        return desiredPoolSize;
    }

    public void setDesiredPoolSize(Integer desiredPoolSize) {
        this.desiredPoolSize = desiredPoolSize;
    }

    public Integer getAvailablePoolRooms() {
        return availablePoolRooms;
    }

    public void setAvailablePoolRooms(Integer availablePoolRooms) {
        this.availablePoolRooms = availablePoolRooms;
    }

    public Instant getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Instant statusTime) {
        this.statusTime = statusTime;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }
}
