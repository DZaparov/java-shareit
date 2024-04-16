package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Email;

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
    private String email;
}
