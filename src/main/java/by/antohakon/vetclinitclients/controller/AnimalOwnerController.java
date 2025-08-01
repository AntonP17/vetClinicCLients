package by.antohakon.vetclinitclients.controller;


import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.AnimalOwnerDto;
import by.antohakon.vetclinitclients.dto.AnimalOwnersWithAnimalsDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalOwnerDto;
import by.antohakon.vetclinitclients.service.AnimalOwnerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/owners")
public class AnimalOwnerController {

    private final AnimalOwnerServiceImpl animalOwnerService;

    @GetMapping()
    @ResponseStatus(value = HttpStatus.OK)
    public Page<AnimalOwnerDto> getAllAnimalOwners(@PageableDefault(size = 5) Pageable pageable) {
        return animalOwnerService.getAllAnimalOwners(pageable);
    }

    @GetMapping("/{animalOwnerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public AnimalOwnersWithAnimalsDto getAnimalOwner(@PathVariable UUID animalOwnerId) {
        return animalOwnerService.getAnimalOwnerById(animalOwnerId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public AnimalOwnerDto createAnimalOwner(@RequestBody CreateAnimalOwnerDto animalOwnerDto) {
        return animalOwnerService.createAnimalOwner(animalOwnerDto);
    }

    @PutMapping("/{animalOwnerId}")
    @ResponseStatus(value = HttpStatus.OK)
    public AnimalOwnerDto updateAnimalOwner(@PathVariable UUID animalOwnerId,
                                            @RequestBody CreateAnimalOwnerDto animalOwnerDto) {
        return animalOwnerService.updateAnimalOwner(animalOwnerDto, animalOwnerId);
    }

    @DeleteMapping("/{animalOwnerId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAnimalOwner(@PathVariable UUID animalOwnerId) {
        animalOwnerService.deleteAnimalOwner(animalOwnerId);
    }


}
