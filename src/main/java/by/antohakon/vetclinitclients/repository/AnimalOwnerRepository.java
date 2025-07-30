package by.antohakon.vetclinitclients.repository;

import by.antohakon.vetclinitclients.entity.Animal;
import by.antohakon.vetclinitclients.entity.AnimalOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnimalOwnerRepository extends JpaRepository<AnimalOwner, Long> {

    AnimalOwner findByAnimalOwnerUuid(UUID animalOwnerUuid);
    boolean existsAnimalOwnersByLastName(String lastName);
}
