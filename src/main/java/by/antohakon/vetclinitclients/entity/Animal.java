package by.antohakon.vetclinitclients.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "animals")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID animalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalType animalType;

    @Column(nullable = false)
    private String animalName;

    @ManyToOne
    @JoinColumn(name = "owner_id",nullable = false)
    private AnimalOwner animalOwner;

    public Animal(AnimalType animalType, String animalName, AnimalOwner animalOwner) {
        this.animalType = animalType;
        this.animalName = animalName;
        this.animalOwner = animalOwner;
    }
}
