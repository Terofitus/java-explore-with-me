package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Integer id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Compilations_events",
        joinColumns = @JoinColumn(name = "compilation_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;
    @Column
    @EqualsAndHashCode.Exclude
    private Boolean pinned;
    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Exclude
    private String title;
}
