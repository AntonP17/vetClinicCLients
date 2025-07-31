package by.antohakon.vetclinitclients.controller;

import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalDto;
import by.antohakon.vetclinitclients.dto.UpdateAnimalDto;
import by.antohakon.vetclinitclients.entity.Animal;
import by.antohakon.vetclinitclients.service.AnimalService;
import by.antohakon.vetclinitclients.service.AnimalServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/animals")
public class AnimalController {

    private final AnimalServiceImpl animalService;

    @GetMapping()
    @ResponseStatus(value = HttpStatus.OK)
    public Page<AnimalDto> getAllAnimals(@PageableDefault(size = 5) Pageable pageable) {
        return animalService.getAllAnimals(pageable);
    }

    @GetMapping("/{animalId}")
    @ResponseStatus(value = HttpStatus.OK)
    public AnimalDto getAnimalById(@PathVariable UUID animalId) {
        return animalService.getAnimalById(animalId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public AnimalDto createAnimal(@RequestBody CreateAnimalDto createAnimalDto) {
        return animalService.createAnimal(createAnimalDto);
    }


    // ЭТОТ МЕТОД ПРОЕврИТЬ ПРОВЕРИТЬ ПРВОЕРИТЬ ДТО ДТО ДОЛЖНО БЫТЬ УПДАТЕДТО
    @PutMapping("/{animalId}")
    @ResponseStatus(value = HttpStatus.OK)
    public AnimalDto updateAnimal(@PathVariable UUID animalId, @RequestBody UpdateAnimalDto updateAnimalDto) {
       return animalService.updateAnimal(updateAnimalDto,animalId);
    }

    @DeleteMapping("/{animalId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAnimal(@PathVariable UUID animalId) {
        animalService.deleteAnimal(animalId);
    }
}
