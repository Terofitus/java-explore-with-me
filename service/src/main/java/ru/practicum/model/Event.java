package ru.practicum.model;

import dto.EventState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private LocalDateTime created_on;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id", nullable = false)
    private User initiator;
    @Column
    private LocalDateTime published_on;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private EventState state_enum;
    @Column(nullable = false, length = 2000)
    private String annotation;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;
    @Column(nullable = false, length = 7000)
    private String description;
    @Column(nullable = false)
    private LocalDateTime event_date;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false)
    private Integer participant_limit;
    @Column(nullable = false)
    private Boolean request_moderation;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false)
    private Integer views;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private Location location;
}
