package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
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

    List<Booking> findBookingsByItemOwnerId(Long id, Sort sort);

    List<Booking> findBookingsByItemOwnerIdAndStatus(Long id, BookingStatus status, Sort sort);

    List<Booking> findBookingsByItemOwnerIdAndStartBeforeAndEndAfter(Long id, Sort sort, LocalDateTime startDateTime,
                                                                     LocalDateTime endDateTime);

    List<Booking> findBookingsByItemOwnerIdAndEndBefore(Long id, Sort sort, LocalDateTime endDateTime);

    List<Booking> findBookingsByItemOwnerIdAndStartAfter(Long id, Sort sort, LocalDateTime startDateTime);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date < now() " +
            "ORDER BY end_date DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getPastOrCurrentBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id IN ? AND start_date < now() " +
            "ORDER BY end_date DESC " +
            "LIMIT 1",
            nativeQuery = true)
    List<Optional<Booking>> getPastOrCurrentBookingByItemIdIn(Set<Long> itemIds);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date > now() AND status <> 'REJECTED' " +
            "ORDER BY start_date " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getFutureBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id IN ? AND start_date > now() " +
            "ORDER BY start_date " +
            "LIMIT 1",
            nativeQuery = true)
    List<Optional<Booking>> getFutureBookingByItemIdList(Set<Long> itemIds);

    List<Booking> findBookingsByBooker_Id(long id, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(long id,
                                                                       LocalDateTime checkStart,
                                                                       LocalDateTime checkEnd);

    List<Booking> findBookingsByBooker_IdAndEndIsBefore(long id, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStartIsAfter(long id, LocalDateTime dateTime, Sort sort);

    List<Booking> findBookingsByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByItem_IdAndBooker_IdAndEndIsBefore(long itemId,
                                                                  long bookerId,
                                                                  LocalDateTime dateTime);
}
