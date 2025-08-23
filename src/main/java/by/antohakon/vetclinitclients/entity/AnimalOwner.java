package by.antohakon.vetclinitclients.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "owners", indexes = {
        @Index(columnList = "animalOwnerUuid", name = "animal_owner_uuid_index"),
        @Index(columnList = "lastName", name = "owner_lastname_index")
})
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID animalOwnerUuid;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false, unique = true)
    private String lastName;

    @Column(nullable = false)
    @OneToMany(mappedBy = "animalOwner", cascade = CascadeType.ALL) // каскадиование потом проерить
    private List<Animal> animal;

    public AnimalOwner(UUID animalOwnerUuid, String firstName, String lastName, List<Animal> animal) {
        this.animalOwnerUuid = animalOwnerUuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.animal = animal;
    }

    public AnimalOwner(UUID animalOwnerUuid, String firstName, String lastName) {
        this.animalOwnerUuid = animalOwnerUuid;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
