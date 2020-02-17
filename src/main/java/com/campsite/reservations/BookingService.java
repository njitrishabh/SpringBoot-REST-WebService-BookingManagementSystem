package com.campsite.reservations;

import java.time.LocalDate;
import java.util.List;
import com.campsite.reservations.Booking;

public interface BookingService {

    List<LocalDate> findVacantDays(LocalDate startDate, LocalDate endDate);

    Booking findBookingById(long id);

    Booking createBooking(Booking booking);

    Booking updateBooking(Long id, Booking booking);

    boolean cancelBooking(long id);
}
