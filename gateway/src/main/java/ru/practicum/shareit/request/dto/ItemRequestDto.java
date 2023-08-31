package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    @Size(max = 200,
            message = "Описание не может быть длиннее 200 символов",
            groups = {Create.class})
    @NotBlank
    @NonNull
    private String description;
    private UserDto requester;
    private LocalDateTime created;
}
