package by.antohakon.vetclinitclients.repository;

import by.antohakon.vetclinitclients.entity.AnimalOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalOwnerRepository extends JpaRepository<AnimalOwner, Long> {
}
