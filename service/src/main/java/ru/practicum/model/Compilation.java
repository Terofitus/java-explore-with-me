package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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
    private List<Event> events;
    @Column
    @EqualsAndHashCode.Exclude
    private Boolean pinned;
    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Exclude
    private String title;
}
