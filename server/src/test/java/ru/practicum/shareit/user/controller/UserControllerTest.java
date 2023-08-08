package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private final UserDto user = new UserDto(1L, "Vasia", "VasiaPupkin@gmail.com");
    private final UserDto userWithoutId = new UserDto(null, "Vasia", "VasiaPupkin@gmail.com");
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void createUserTest() throws Exception {
        Mockito
                .when(userService.createUser(any()))
                .thenReturn(user);
        mvc.perform(post("/users")
                                .content(mapper.writeValueAsString(user))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        Mockito.verify(userService, Mockito.times(1))
                .createUser(user);
    }

    @Test
    void getUserByIdTest() throws Exception {
        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(user);
        mvc.perform(get("/users/{id}", user.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        Mockito.verify(userService, Mockito.times(1))
                .getUserById(1L);
    }

    @Test
    void getAllUsersTest() throws Exception {
        Mockito
                .when(userService.getAllUsers())
                .thenReturn(List.of(user));
        mvc.perform(get("/users")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(user.getName())))
                .andExpect(jsonPath("$.[0].email", is(user.getEmail())));
        Mockito.verify(userService, Mockito.times(1))
                .getAllUsers();
    }

    @Test
    void updateUserTest() throws Exception {
        Mockito
                .when(userService.updateUser(anyLong(), any()))
                .thenReturn(user);
        mvc.perform(patch("/users/{id}", user.getId())
                                .content(mapper.writeValueAsString(userWithoutId))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        Mockito.verify(userService, Mockito.times(1))
                .updateUser(1L, userWithoutId);
    }

    @Test
    void deleteUserTest() throws Exception {
        userService.deleteUser(anyLong());
        Mockito
                .verify(userService, times(1))
                .deleteUser(anyLong());
        mvc.perform(delete("/users/{id}", user.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}