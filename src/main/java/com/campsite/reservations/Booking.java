package com.campsite.reservations;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Entity
@Table(name = "bookings")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

    protected Booking() {

    }

    public Booking(Long version, @NotEmpty @Size(max = 30) String fullName, @NotEmpty @Size(max = 30) String email, @NotNull @Future(message = "Booking start date must be a future date") LocalDate startDate, @NotNull @Future(message = "Booking end date must be a future date") LocalDate endDate, boolean active) {
        this.version = version;
        this.fullName = fullName;
        this.email = email;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @NotEmpty
    @Size(max = 30)
    @Column(name = "full_name", nullable = false, length = 30)
    private String fullName;

    @NotEmpty
    @Size(max = 30)
    @Column(name = "email", nullable = false, length = 30)
    private String email;

    @NotNull
    @Future(message = "Booking start date must be a future date")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Future(message = "Booking end date must be a future date")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "active", nullable = false)
    private boolean active;

    @JsonIgnore
    public boolean isNew() {
       return this.id == null;
    }

    @JsonIgnore
    public List<LocalDate> getBookingDates() {
        return this.startDate.datesUntil(this.endDate).collect(Collectors.toList());
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getActive() {
        return active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }
}
