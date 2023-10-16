package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location,Integer> {
    Location findByLatAndLon(Double lat, Double lon);
}
