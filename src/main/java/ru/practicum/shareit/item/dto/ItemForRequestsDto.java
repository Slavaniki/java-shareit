package ru.practicum.shareit.item.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemForRequestsDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long requestId;
}
