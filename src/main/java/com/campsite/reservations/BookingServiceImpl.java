package com.campsite.reservations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.common.base.Preconditions;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> findVacantDays(LocalDate startDate, LocalDate endDate) {

        LocalDate now = LocalDate.now();
        Preconditions.checkArgument(startDate.isAfter(now), "Start date must be in the future");
        Preconditions.checkArgument(endDate.isAfter(now), "End date must be in the future");
        Preconditions.checkArgument(startDate.isEqual(endDate) || startDate.isBefore(endDate),
                "End date must be equal to start date or greater than start date");

        List<LocalDate> vacantDays = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findForDateRange(startDate, endDate);

        bookings.forEach(b -> vacantDays.removeAll(b.getBookingDates()));
        return vacantDays;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking findBookingById(long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (!booking.isPresent()) {
            System.out.println("wrong booking id");
        }
        return booking.get();
    }

    @Override
    @Transactional()
    @Retryable(include = CannotAcquireLockException.class, maxAttempts = 2, backoff = @Backoff(delay = 150, maxDelay = 300))
    public Booking createBooking(Booking booking) {
        if(!booking.isNew()) {
            System.out.println("new booking must not have id");
        }

        List<LocalDate> vacantDays = findVacantDays(booking.getStartDate(), booking.getEndDate());

        if (!vacantDays.containsAll(booking.getBookingDates())) {
            System.out.println("No vacant dates available");
        }

        booking.setActive(true);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking updateBooking(Long id, Booking booking) {

        Booking persistedBooking = findBookingById(id);

        if(!persistedBooking.isActive()) {
            System.out.println("Booking is cancelled");
        }

        List<LocalDate> vacantDays = findVacantDays(booking.getStartDate(), booking.getEndDate());
        vacantDays.addAll(persistedBooking.getBookingDates());

        if(!vacantDays.containsAll(booking.getBookingDates())) {
            System.out.println("No vacant dates available");
        }

        persistedBooking.setFullName(booking.getFullName());
        persistedBooking.setEmail(booking.getEmail());
        persistedBooking.setStartDate(booking.getStartDate());
        persistedBooking.setEndDate(booking.getEndDate());

        return bookingRepository.save(persistedBooking);
    }

    @Override
    @Transactional
    public boolean cancelBooking(long id) {

        Booking booking = findBookingById(id);
        booking.setActive(false);
        booking = bookingRepository.save(booking);
        return !booking.isActive();
    }
}
