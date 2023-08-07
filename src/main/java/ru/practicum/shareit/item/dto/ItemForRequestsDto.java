package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemForRequestsDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long requestId;
}
