package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;

    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Email должен быть валидным адресом электронной почты.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым.")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения обязательна.")
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();
}
