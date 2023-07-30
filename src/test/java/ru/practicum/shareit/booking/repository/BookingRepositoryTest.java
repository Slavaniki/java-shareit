package ru.practicum.shareit.booking.repository;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@SqlGroup({
        @Sql(value = {"booking-repository-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"booking-repository-test-after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final ItemRequest request = new ItemRequest(
            4L,
            "great garden without people",
            secondUser,
            LocalDateTime.of(2022, 10, 10, 12, 0));
    private final Item paradise = new Item(3L,
            "Paradise",
            "great garden without people",
            true,
            firstUser,
            request);
    private final Booking booking = new Booking(
            4L,
            LocalDateTime.of(2023, 10, 20, 12, 30),
            LocalDateTime.of(2023, 10, 21, 13, 35),
            paradise,
            secondUser,
            BookingStatus.WAITING);
    private final Item apple = new Item(5L, "Apple", "very tasty fruit", true, secondUser, null);
    private final Booking bookingCurrent = new Booking(
            7L,
            LocalDateTime.of(2022, 10, 25, 12, 30),
            LocalDateTime.of(2023, 10, 30, 13, 35),
            apple,
            firstUser,
            BookingStatus.WAITING);

    @Test
    void getPastOrCurrentBookingByItemIdTest() {
        Optional<Booking> result = bookingRepository.getPastOrCurrentBookingByItemId(apple.getId());
        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(bookingCurrent.getId());
        assertThat(result.get().getStart()).isEqualTo(bookingCurrent.getStart());
        assertThat(result.get().getEnd()).isEqualTo(bookingCurrent.getEnd());
        assertThat(result.get().getStatus()).isEqualTo(bookingCurrent.getStatus());
    }

    @Test
    void getFutureBookingByItemIdTest() {
        Optional<Booking> result = bookingRepository.getFutureBookingByItemId(paradise.getId());
        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
        assertThat(result.get().getStart()).isEqualTo(booking.getStart());
        assertThat(result.get().getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get().getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get().getStatus()).isEqualTo(booking.getStatus());
    }
}