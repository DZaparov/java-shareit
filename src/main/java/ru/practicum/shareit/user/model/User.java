package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String name;
    @Email
    @NotBlank
    private String email;
}
