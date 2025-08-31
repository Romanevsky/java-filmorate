package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза обязательна.")
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность должна быть положительной.")
    private int duration;

    private Set<Integer> likes = new HashSet<>();
}
