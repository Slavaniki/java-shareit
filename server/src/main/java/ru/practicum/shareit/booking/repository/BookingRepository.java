package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getBookingsByItem_IdInOrderByEndAsc(Set<Long> itemIds);

    List<Booking> findBookingsByItemOwnerId(Long id, Pageable pageable);

    List<Booking> findBookingsByItemOwnerIdAndStatus(Long id, BookingStatus status, Pageable pageable);

    List<Booking> findBookingsByItemOwnerIdAndStartBeforeAndEndAfter(Long id, Pageable pageable, LocalDateTime startDateTime,
                                                                     LocalDateTime endDateTime);

    List<Booking> findBookingsByItemOwnerIdAndEndBefore(Long id, Pageable pageable, LocalDateTime endDateTime);

    List<Booking> findBookingsByItemOwnerIdAndStartAfter(Long id, Pageable pageable, LocalDateTime startDateTime);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date < now() " +
            "ORDER BY end_date DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getPastOrCurrentBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date > now() AND status <> 'REJECTED' " +
            "ORDER BY start_date " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getFutureBookingByItemId(long id);

    List<Booking> findBookingsByBooker_Id(long id, Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(long id,
                                                                       LocalDateTime checkStart,
                                                                       LocalDateTime checkEnd);

    List<Booking> findBookingsByBooker_IdAndEndIsBefore(long id, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndStartIsAfter(long id, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findBookingsByItem_IdAndBooker_IdAndEndIsBefore(long itemId,
                                                                  long bookerId,
                                                                  LocalDateTime dateTime);
}
