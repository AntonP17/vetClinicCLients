package by.antohakon.vetclinitclients.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "owners")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false, unique = true)
    private String lastName;

    @Column(nullable = false)
    @OneToMany(mappedBy = "animalOwner", cascade = CascadeType.ALL) // каскадиование потом проерить
    private List<Animal> animal;

    public AnimalOwner(String firstName, String lastName, List<Animal> animal) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.animal = animal;
    }
}
