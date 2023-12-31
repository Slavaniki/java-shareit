package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@Entity
@ToString
@EqualsAndHashCode
public class ItemRequest {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "requester_id", nullable = false)
    @ManyToOne(optional = false)
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;
}
