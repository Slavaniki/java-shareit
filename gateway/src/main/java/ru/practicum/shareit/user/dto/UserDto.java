package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым", groups = {Create.class})
    private String name;
    @NotEmpty(groups = {Create.class})
    @Email(message = "Введён некорректный email", groups = {Create.class, Update.class})
    private String email;
}
