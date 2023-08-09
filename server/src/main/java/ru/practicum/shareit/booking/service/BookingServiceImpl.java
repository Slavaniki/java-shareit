package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIncome;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingDtoIncome bookingDtoIncome) {
        final User booker = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователя с id: " + userId + " не существует"));
        Item bookingItem = itemRepository.findById(bookingDtoIncome.getItemId()).orElseThrow(() ->
                        new ResourceNotFoundException("Вещи с id: " + bookingDtoIncome.getItemId() + " не существует"));
        if (!bookingItem.isAvailable()) {
            throw new NotAvailableException("Вещь с id: " + bookingDtoIncome.getItemId() + " недоступна для бронирования");
        }
        if (booker.getId().equals(bookingItem.getOwner().getId())) {
            throw new ResourceNotFoundException("Пользователь с id " + booker.getId() + " не может забронировать вещь с id " + bookingItem.getId() + " , потому что является её владельцем");
        }
        final Booking savedBooking = bookingRepository.save(BookingMapper
                .toBooking(bookingDtoIncome, bookingItem, booker));
        log.info("Бронирование успешно создано");
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        final Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Бронирования с id " + bookingId + " не существует"));
        final User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователя с id: " + userId + " не существует"));
        if (!Objects.equals(user.getId(), booking.getBooker().getId())
                && !Objects.equals(user.getId(), booking.getItem().getOwner().getId())) {
            throw new ResourceNotFoundException("Пользователь с id " + userId + " не может получить бронирование с id " + bookingId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователя с id: " + userId + " не существует"));
        List<Booking> resultList;
        switch (state) {
            case WAITING:
                resultList = bookingRepository.findBookingsByBooker_IdAndStatus(
                        userId,
                        BookingStatus.WAITING,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case REJECTED:
                resultList = bookingRepository.findBookingsByBooker_IdAndStatus(
                        userId,
                        BookingStatus.REJECTED,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case CURRENT:
                resultList = bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                break;
            case PAST:
                resultList = bookingRepository.findBookingsByBooker_IdAndEndIsBefore(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case FUTURE:
                resultList = bookingRepository.findBookingsByBooker_IdAndStartIsAfter(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case ALL:
                resultList = bookingRepository.findBookingsByBooker_Id(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            default:
                throw new NotAvailableException("Unknown state: " + state);
        }
        return resultList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователя с id: " + userId + " не существует"));
        List<Booking> resultList;
        switch (state) {
            case WAITING:
                resultList = bookingRepository.findBookingsByItemOwnerIdAndStatus(
                        userId,
                        BookingStatus.WAITING,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case REJECTED:
                resultList = bookingRepository.findBookingsByItemOwnerIdAndStatus(
                        userId,
                        BookingStatus.REJECTED,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case CURRENT:
                resultList = bookingRepository.findBookingsByItemOwnerIdAndStartBeforeAndEndAfter(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start")),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                break;
            case PAST:
                resultList = bookingRepository.findBookingsByItemOwnerIdAndEndBefore(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start")),
                        LocalDateTime.now()
                );
                break;
            case FUTURE:
                resultList = bookingRepository.findBookingsByItemOwnerIdAndStartAfter(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start")),
                        LocalDateTime.now()
                );
                break;
            case ALL:
                resultList = bookingRepository.findBookingsByItemOwnerId(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            default:
                throw new NotAvailableException("Unknown state: " + state);
        }
        return resultList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(long userId, long bookingId, boolean updateStatus) {
        final Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Бронирования с id " + bookingId + " не существует"));
        if (userId != booking.getItem().getOwner().getId()) {
            throw new ResourceNotFoundException("Пользователь с id " + userId + " не может изменить статус бронирования с id " + bookingId + " , потому что является владельцем вещи");
        }
        BookingStatus status;
        if (updateStatus) {
            status = BookingStatus.APPROVED;
        } else {
            status = BookingStatus.REJECTED;
        }
        if (booking.getStatus() == status) {
            throw new IllegalArgumentException("Это бронирование уже имеет статус: " + status);
        }
        booking.setStatus(status);
        Booking newBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(newBooking);
    }
}
