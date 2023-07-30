package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIncome;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService bookService;

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void createBookingTest() {
        BookingDtoIncome bookingDtoIncome = new BookingDtoIncome(
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(2),
                5L
        );
        BookingDto newBookingDto = bookService.createBooking(1L, bookingDtoIncome);
        assertEquals(newBookingDto.getId(), 1L);
        assertEquals(newBookingDto.getStatus(), BookingStatus.WAITING);
        assertEquals(newBookingDto.getId(), 1L);
        assertEquals(newBookingDto.getItem().getId(), 5L);
        assertEquals(newBookingDto.getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-without-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void createBooking_withItemIsNotAvailableTest() {
        BookingDtoIncome newBookingDto = new BookingDtoIncome(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );
        assertThrows(NotAvailableException.class, () -> bookService.createBooking(1L, newBookingDto));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void createBooking_withUserDoesNotExistTest() {
        BookingDtoIncome newBookingDto = new BookingDtoIncome(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );
        assertThrows(ResourceNotFoundException.class, () -> bookService.createBooking(1000L, newBookingDto));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void createBooking_withItemDoesNotExistTest() {
        BookingDtoIncome newBookingDto = new BookingDtoIncome(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                1000L
        );
        assertThrows(ResourceNotFoundException.class, () -> bookService.createBooking(1L, newBookingDto));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void createBooking_UserIsOwnerTest() {
        BookingDtoIncome newBookingDto = new BookingDtoIncome(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );
        assertThrows(ResourceNotFoundException.class, () -> bookService.createBooking(1L, newBookingDto));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getBookingByIdTest() {
        BookingDto result = bookService.getBookingById(1L, 7L);
        assertThat(result).isNotNull();
        assertEquals(7L, result.getId());
        assertEquals(LocalDateTime.of(2023, 10, 25, 12, 30), result.getStart());
        assertEquals(LocalDateTime.of(2023, 10, 30, 13, 35), result.getEnd());
        assertEquals(3L, result.getItem().getId());
        assertEquals(1L, result.getBooker().getId());
        BookingDto result2 = bookService.getBookingById(1L, 7L);
        assertThat(result2).isNotNull();
        assertEquals(7L, result2.getId());
        assertEquals(LocalDateTime.of(2023, 10, 25, 12, 30), result2.getStart());
        assertEquals(LocalDateTime.of(2023, 10, 30, 13, 35), result2.getEnd());
        assertEquals(3L, result2.getItem().getId());
        assertEquals(1L, result2.getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getBookingById_withUserIsNotBookerOrOwner() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookingById(1000L, 7L));
    }

    /*@Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetBooking_withUserIsBooker() {
        BookingDto result = bookService.getBookingById(1L, 7L);
        assertThat(result).isNotNull();
        assertEquals(7L, result.getId());
        assertEquals(LocalDateTime.of(2022, 10, 25, 12, 30), result.getStart());
        assertEquals(LocalDateTime.of(2022, 10, 30, 13, 35), result.getEnd());
        assertEquals(3L, result.getItem().getId());
        assertEquals(1L, result.getBooker().getId());
    }*/

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-without-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetBooking_withBookingDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookingById(1L, 1000L));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getUserBookings_withUserDoesNotExistTest() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.getUserBookings(1000L, BookingState.ALL, 0, 10));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getUserBookings_withStateIsWaitingTest() {
        List<BookingDto> result = bookService.getUserBookings(1L, BookingState.WAITING, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-rejected-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getUserBookings_withStateIsRejectedTest() {
        List<BookingDto> result = bookService.getUserBookings(1L, BookingState.REJECTED, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-current-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getUserBookings_withStateIsCurrentTest() {
        List<BookingDto> result = bookService.getUserBookings(1L, BookingState.CURRENT, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
        assertTrue(result.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(result.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-past-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getUserBookings_withStateIsPastTest() {
        List<BookingDto> result = bookService.getUserBookings(1L, BookingState.PAST, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
        assertTrue(result.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(result.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-future-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getUserBookings_withStateIsFutureTest() {
        List<BookingDto> result = bookService.getUserBookings(1L, BookingState.FUTURE, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
        assertTrue(result.get(0).getStart().isAfter(LocalDateTime.now()));
        assertTrue(result.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getUserBookings_withStateIsAllTest() {
        List<BookingDto> result = bookService.getUserBookings(1L, BookingState.ALL, 0, 1);
        assertThat(result).isNotNull();
        assertEquals(7L, result.get(0).getId());
        assertEquals(LocalDateTime.of(2023, 10, 25, 12, 30), result.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 10, 30, 13, 35), result.get(0).getEnd());
        assertEquals(3L, result.get(0).getItem().getId());
        assertEquals(1L, result.get(0).getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getOwnerBookings_withStateIsWaitingTest() {
        List<BookingDto> result = bookService.getOwnerBookings(1L, BookingState.WAITING, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-rejected-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getOwnerBookings_withStateIsRejectedTest() {
        List<BookingDto> result = bookService.getOwnerBookings(1L, BookingState.REJECTED, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-current-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getOwnerBookings_withStateIsCurrentTest() {
        List<BookingDto> result = bookService.getOwnerBookings(1L, BookingState.CURRENT, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
        assertTrue(result.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(result.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-past-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getOwnerBookings_withStateIsPastTest() {
        List<BookingDto> result = bookService.getOwnerBookings(1L, BookingState.PAST, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
        assertTrue(result.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(result.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-future-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getOwnerBookings_withStateIsFutureTest() {
        List<BookingDto> result = bookService.getOwnerBookings(1L, BookingState.FUTURE, 0, 1);
        assertEquals(result.get(0).getId(), 7L);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(result.get(0).getItem().getId(), 3L);
        assertEquals(result.get(0).getBooker().getId(), 1L);
        assertTrue(result.get(0).getStart().isAfter(LocalDateTime.now()));
        assertTrue(result.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void getOwnerBookings_withStateIsAllTest() {
        List<BookingDto> result = bookService.getOwnerBookings(1L, BookingState.ALL, 0, 1);
        assertThat(result).isNotNull();
        assertEquals(7L, result.get(0).getId());
        assertEquals(LocalDateTime.of(2023, 10, 25, 12, 30), result.get(0).getStart());
        assertEquals(LocalDateTime.of(2023, 10, 30, 13, 35), result.get(0).getEnd());
        assertEquals(3L, result.get(0).getItem().getId());
        assertEquals(1L, result.get(0).getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void updateBookingStatusTest() {
        BookingDto result = bookService.updateBookingStatus(1L, 7L, true);
        assertEquals(result.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void updateBookingStatus_withBookingDoesNotExistTest() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBookingStatus(1L, 1000L, true));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void updateBookingStatus_withUserIsNotOwnerTest() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBookingStatus(1000L, 7L, true));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-approved-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void updateBookingStatus_StatusIsTheSameTest() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBookingStatus(2L, 7L, true));
    }
}