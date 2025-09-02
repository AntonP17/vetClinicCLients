package by.antohakon.vetclinitclients.service;

import by.antohakon.vetclinitclients.dto.AnimalDto;
import by.antohakon.vetclinitclients.dto.CreateAnimalDto;
import by.antohakon.vetclinitclients.dto.UpdateAnimalDto;
import by.antohakon.vetclinitclients.entity.Animal;
import by.antohakon.vetclinitclients.entity.AnimalOwner;
import by.antohakon.vetclinitclients.entity.AnimalType;
import by.antohakon.vetclinitclients.exceptions.AnimalNotFoundException;
import by.antohakon.vetclinitclients.exceptions.OwnerNotFoundException;
import by.antohakon.vetclinitclients.repository.AnimalOwnerRepository;
import by.antohakon.vetclinitclients.repository.AnimalRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private AnimalOwnerRepository animalOwnerRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @InjectMocks
    private AnimalServiceImpl animalService;

    private static final UUID TEST_UUID = UUID.fromString("2e8f5f7b-7dfe-4c60-a03a-c38a1446033b");
    private AutoCloseable mockStatic;

    @BeforeEach
    void setUp() {
        mockStatic = Mockito.mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(TEST_UUID);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockStatic.close();
    }

    @Test
    @SneakyThrows
    @DisplayName("поиск в БД позитив")
    void getAnimalById_positive() {

        Animal animal = Animal.builder()
                .animalId(TEST_UUID)
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .build();

        when(animalRepository.findByAnimalId(TEST_UUID)).thenReturn(animal);

        AnimalDto result = animalService.getAnimalById(TEST_UUID);

        assertEquals(TEST_UUID, result.id());
        assertEquals("Gasya", result.animalName());
    }

    @Test
    @SneakyThrows
    @DisplayName("поиск в бд негатив")
    void getAnimalById_NotFound() {

        when(animalRepository.findByAnimalId(TEST_UUID)).thenReturn(null);

        assertThrows(AnimalNotFoundException.class, () -> {
            animalService.getAnimalById(TEST_UUID);
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("создание в БД позиитив")
    void createAnimal_positive() {

        CreateAnimalDto request = CreateAnimalDto.builder()
                .animalOwnerUuid(TEST_UUID)
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .build();

        AnimalOwner owner = AnimalOwner.builder()
                .animalOwnerUuid(TEST_UUID)
                .firstName("Имя")
                .lastName("Фамилия")
                .build();

        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(owner);
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        AnimalDto result = animalService.createAnimal(request);

        assertEquals(TEST_UUID, result.id());
        assertEquals("Gasya", result.animalName());
    }

    @Test
    @SneakyThrows
    @DisplayName("создание в бд негатив")
    void createAnimal_OwnerNotFound() {

        CreateAnimalDto request = CreateAnimalDto.builder()
                .animalOwnerUuid(TEST_UUID)
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .build();

        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(null);

        assertThrows(OwnerNotFoundException.class, () -> {
            animalService.createAnimal(request);
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("обновление в бд позитив")
    void updateAnimal_positive() {

        UpdateAnimalDto request = UpdateAnimalDto.builder()
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .animalOwnerUuid(TEST_UUID)
                .build();

        Animal existingAnimal = Animal.builder()
                .animalId(TEST_UUID)
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .build();

        AnimalOwner owner = AnimalOwner.builder()
                .animalOwnerUuid(TEST_UUID)
                .firstName("Имя")
                .lastName("Фамилия")
                .build();

        when(animalRepository.findByAnimalId(TEST_UUID)).thenReturn(existingAnimal);
        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(owner);
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        AnimalDto result = animalService.updateAnimal(request, TEST_UUID);

        assertEquals("Gasya", result.animalName());
    }

    @Test
    @SneakyThrows
    @DisplayName("обновление не найдено животное")
    void updateAnimal_AnimalNotFound() {

        UpdateAnimalDto request = UpdateAnimalDto.builder()
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .animalOwnerUuid(TEST_UUID)
                .build();

        when(animalRepository.findByAnimalId(TEST_UUID)).thenReturn(null);

        assertThrows(AnimalNotFoundException.class, () -> {
            animalService.updateAnimal(request, TEST_UUID);
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("обновление не найден владелец")
    void updateAnimal_OwnerNotFound() {

        UpdateAnimalDto request = UpdateAnimalDto.builder()
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .animalOwnerUuid(TEST_UUID)
                .build();

        Animal existingAnimal = Animal.builder()
                .animalId(TEST_UUID)
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .build();

        when(animalRepository.findByAnimalId(TEST_UUID)).thenReturn(existingAnimal);
        when(animalOwnerRepository.findByAnimalOwnerUuid(TEST_UUID)).thenReturn(null);

        assertThrows(OwnerNotFoundException.class, () -> {
            animalService.updateAnimal(request, TEST_UUID);
        });
    }

    @Test
    @SneakyThrows
    @DisplayName("удаление из БД")
    void deleteAnimal_positive() {

        AnimalOwner owner = AnimalOwner.builder()
                .animalOwnerUuid(TEST_UUID)
                .build();

        Animal existingAnimal = Animal.builder()
                .animalId(TEST_UUID)
                .animalType(AnimalType.BIRD)
                .animalName("Gasya")
                .animalOwner(owner)
                .build();

        when(animalRepository.findByAnimalId(TEST_UUID)).thenReturn(existingAnimal);
        doNothing().when(animalRepository).delete(existingAnimal);

        when(cacheManager.getCache("owner_cache")).thenReturn(cache);
        doNothing().when(cache).evict(TEST_UUID);

        assertDoesNotThrow(() -> {
            animalService.deleteAnimal(TEST_UUID);
        });

    }

    @Test
    void deleteAnimal_NotFound_ShouldThrowException() {

        when(animalRepository.findByAnimalId(TEST_UUID)).thenReturn(null);

        assertThrows(AnimalNotFoundException.class, () -> {
            animalService.deleteAnimal(TEST_UUID);
        });
    }

}
