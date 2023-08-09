package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @NotBlank
    @Size(max = 200,
            message = "Описание не может быть более 200 символов.")
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
