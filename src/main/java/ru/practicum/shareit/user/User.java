package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;

    public User update(UserDto userDto) {
        if (userDto.getName() == null) {
            userDto.setName(name);
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(email);
        }
        return User.builder()
                .id(id)
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}