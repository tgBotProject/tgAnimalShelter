package pro.sky.teamproject.tgBot.controller;

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
import pro.sky.teamproject.tgBot.model.adoption.Adoption;
import pro.sky.teamproject.tgBot.service.AdoptionService;
import pro.sky.teamproject.tgBot.utils.ErrorUtils;

import java.util.List;

@RestController
@RequestMapping("adoption")
@RequiredArgsConstructor
@Tag(name = "Усыновление", description = "Операции с записями об усыновлениях")
@ApiResponses(value = {@ApiResponse(responseCode = "500",
        description = "Произошла ошибка, не зависящая от вызывающей стороны")})
public class AdoptionController {

    private final AdoptionService service;

    /* CREATE */
    @Operation(summary = "Добавить новую запись")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись успешно добавлена"),
            @ApiResponse(responseCode = "400", description = "Для указанного юзера или животного есть активное усыновление")})
    @PostMapping("/user/{userId}/animal/{animalId}")
    public ResponseEntity<?> addAdoption(@PathVariable Long userId, @PathVariable Long animalId) {

        return ResponseEntity.ok(service.addAdoption(userId, animalId));
    }

    /* READ */
    @Operation(summary = "Получить все записи об усыновлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Записи найдены",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Adoption.class)))),
            @ApiResponse(responseCode = "404", description = "Записи не найдены")})
    @GetMapping
    public ResponseEntity<List<Adoption>> getAdoptions() {
        List<Adoption> adoptions = service.findAdoptions();
        if (adoptions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoptions);
    }

    @Operation(summary = "Получить записи об усыновлении по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись найдена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Adoption.class)))),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")})
    @GetMapping("/{id}")
    public ResponseEntity<Adoption> getAdoption(@Parameter(description = "Идентификатор записи")
                                                    @PathVariable("id") Long id) {
        Adoption foundAdoption = service.findAdoption(id);
        if (foundAdoption == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundAdoption);
    }

    @Operation(summary = "Получить все записи об усыновлении для определенного усыновителя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Записи найдены",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Adoption.class)))),
            @ApiResponse(responseCode = "404", description = "Записи не найдены")})
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Adoption>> getAdoptionsByUserId(@Parameter(description = "Идентификатор усыновителя")
                                                                   @PathVariable Long userId) {
        List<Adoption> adoptions = service.findAdoptionsByUser(userId);
        if (adoptions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoptions);
    }

    @Operation(summary = "Получить все записи об усыновлении для определенного животного")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Записи найдены",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Adoption.class)))),
            @ApiResponse(responseCode = "404", description = "Записи не найдены")})
    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Adoption>> getAdoptionsByAnimalId(@Parameter(description = "Идентификатор животного")
                                                                     @PathVariable Long animalId) {
        List<Adoption> adoptions = service.findAdoptionsByAnimal(animalId);
        if (adoptions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(adoptions);
    }

    /* UPDATE */
    @Operation(summary = "Изменить существующую запись (окончание испытательного срока, статус, примечание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись успешно изменена",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Adoption.class))}),
            @ApiResponse(responseCode = "400", description = "Статус усыновления отличен от \"текущий\" (CURRENT)"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")})
    @PutMapping
    public ResponseEntity<?> editAdoption(@RequestBody @Valid Adoption adoption,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorUtils.errorsList(bindingResult), HttpStatus.BAD_REQUEST);
        }
        Adoption updatedAdoption = service.updateAdoption(adoption);
        return ResponseEntity.ok(updatedAdoption);
    }

    /* DELETE */
    @Operation(summary = "Удалить запись")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")})
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAdoption(@Parameter(description = "Идентификатор записи")
                                                   @PathVariable Long id) {
        Adoption currentAdoption = service.findAdoption(id);
        if (currentAdoption == null) {
            return ResponseEntity.notFound().build();
        }
        service.deleteAdoption(id);
        return ResponseEntity.ok().build();
    }
}
