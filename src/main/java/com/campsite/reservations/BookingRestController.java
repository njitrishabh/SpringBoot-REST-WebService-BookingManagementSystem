package com.campsite.reservations;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {
    private BookingService bookingService;

    public BookingRestController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/vacant-dates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LocalDate>> getVacantDates(
            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = ISO.DATE) LocalDate endDate
    ) {

        if (startDate == null) {
            startDate = LocalDate.now().plusDays(1);
        }
        if(endDate == null) {
            endDate = startDate.plusMonths(1);
        }
        List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
        return new ResponseEntity<>(vacantDates, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getBooking(@PathVariable() long id) {
        Booking booking = bookingService.findBookingById(id);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Valid Booking booking){

        int max_stay = Period.between(booking.getStartDate(), booking.getEndDate()).getDays();

        if (max_stay <= 3) {
            bookingService.createBooking(booking);
            return new ResponseEntity<>("Booking completed successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("The campsite can be reserved for max 3 days.", HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateBooking(@PathVariable("id") long id,
                                                           @RequestBody Booking booking){

        int max_stay = Period.between(booking.getStartDate(), booking.getEndDate()).getDays();

        if (max_stay <= 3) {
            bookingService.updateBooking(id, booking);
            return new ResponseEntity<>("Booking updated successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("The campsite can be reserved for max 3 days.", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateBooking(@PathVariable("id") long id) {
        boolean cancelled = bookingService.cancelBooking(id);
        if(cancelled) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
