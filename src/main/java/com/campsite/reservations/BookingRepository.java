package com.campsite.reservations;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "100")})
    @Query("select b from Booking b "
            + "where ((b.startDate < ?1 and ?2 < b.endDate) "
            + "or (?1 < b.endDate and b.endDate <= ?2) "
            + "or (?1 <= b.startDate and b.startDate <=?2)) "
            + "and b.active = true "
            + "order by b.startDate asc")
    List<Booking> findForDateRange(LocalDate startDate, LocalDate endDate);
}
