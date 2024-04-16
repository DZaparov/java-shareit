package ru.practicum.shareit.request.model;

import java.util.Date;

import lombok.*;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@ToString
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestor;
    private Date created;
}
