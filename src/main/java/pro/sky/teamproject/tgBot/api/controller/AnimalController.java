package pro.sky.teamproject.tgBot.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pro.sky.teamproject.tgBot.model.Animal;
import pro.sky.teamproject.tgBot.api.service.AnimalService;
import pro.sky.teamproject.tgBot.api.utils.ErrorUtils;

import java.util.List;

@RestController
@RequestMapping("/animal")
@RequiredArgsConstructor
@Tag(name = "Животные", description = "Операции с записями о животных")
@ApiResponses(value = {@ApiResponse(responseCode = "500",
        description = "Произошла ошибка, не зависящая от вызывающей стороны")})
public class AnimalController {
    private final AnimalService service;

    /* ADD an animal */
    @Operation(summary = "Добавить новое животное")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное успешно добавлено",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class))}),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")})
    @PostMapping
    public ResponseEntity<?> addAnimal(@RequestBody @Valid Animal animal,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorUtils.errorsList(bindingResult), HttpStatus.BAD_REQUEST);
        }
        Animal newAnimal = service.addAnimal(animal);
        return ResponseEntity.ok(newAnimal);
    }

    /* GET all animals */
    @Operation(summary = "Получить всех животных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животные найдены",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Animal.class)))),
            @ApiResponse(responseCode = "404", description = "Животные не найдены")})
    @GetMapping
    public ResponseEntity<List<Animal>> getAllAnimals() {
        List<Animal> animals = service.findAllAnimals();
        if (animals.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(animals);
    }

    /* GET animal by ID */
    @Operation(summary = "Получить животное по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное найдено",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class))),
            @ApiResponse(responseCode = "404", description = "Животное не найдено")})
    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalById(@Parameter(description = "Идентификатор животного")
                                            @PathVariable("id") Long id) {
        Animal foundAnimal = service.findAnimalById(id);
        if (foundAnimal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundAnimal);
    }

    /* UPDATE animal */
    @Operation(summary = "Изменить существующее животное")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное успешно изменено",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class))}),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено")})

    @PutMapping
    public ResponseEntity<Animal> updateAnimal(@RequestBody @Valid Animal animal,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(ErrorUtils.errorsList(bindingResult), HttpStatus.BAD_REQUEST);
        }

        Animal updatedAnimal = service.updateAnimal(animal);
        return ResponseEntity.ok(updatedAnimal);
    }

    /* DELETE animal */
    @Operation(summary = "Удалить животное")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Животное не найдено")})
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAnimal(@Parameter(description = "Идентификатор животного")
                                             @PathVariable Long id) {
        Animal currentAnimal = service.findAnimalById(id);
        if (currentAnimal == null) {
            return ResponseEntity.notFound().build();
        }
        service.deleteAnimal(id);
        return ResponseEntity.ok().build();
    }

}
