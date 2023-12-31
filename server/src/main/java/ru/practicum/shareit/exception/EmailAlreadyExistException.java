package ru.practicum.shareit.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String email) {
        super("Пользователь с email: " + email + " уже существует");
    }
}
