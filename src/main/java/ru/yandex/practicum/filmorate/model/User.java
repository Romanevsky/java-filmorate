package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * Модель данных User.
 */
@Data
public class User {
    private int id;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелов")
    private String login;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
