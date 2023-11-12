package pro.sky.teamproject.tgBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.teamproject.tgBot.model.Shelter;
import pro.sky.teamproject.tgBot.service.ShelterService;


@RestController
@Tag(name = "Приюты", description = "Операции с приютами")
@RequestMapping("/shelter")
@ApiResponses(value = {@ApiResponse(responseCode = "500",
        description = "Произошла ошибка, не зависящая от вызывающей стороны")})
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    /* ADD shelter */
    @Operation(summary = "Добавить новый приют")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приют успешно добавлен",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class))}),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")})
    @PostMapping()
    public ResponseEntity<Shelter> addShelters(@RequestBody Shelter shelters) {
        Shelter addShelter = shelterService.addShelters(shelters);
        return ResponseEntity.ok(addShelter);
    }

    /* UPDATE shelter */
    @Operation(summary = "Изменить данные приюта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные изменены",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class))}),
            @ApiResponse(responseCode = "404", description = "Приют не найден")})
    @PutMapping
    public ResponseEntity<Shelter> editShelters(@RequestBody Shelter shelters) {
        Shelter editShelter = shelterService.editShelter(shelters);
        if (editShelter == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(editShelter);
    }

    /* GET shelter by ID */
    @Operation(summary = "Получить приют по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приют найден",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class))}),
            @ApiResponse(responseCode = "404", description = "Приют не найден")})
    @GetMapping("{id}")
    public ResponseEntity<Shelter> findShelters(@RequestParam(required = false) Long id) {
        return ResponseEntity.ok(shelterService.findShelters(id));
    }

    /* DELETE shelter */
    @Operation(summary = "Удалить приют")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приют успешно удален"),
            @ApiResponse(responseCode = "404", description = "Приют не найден")})
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteShelters(@PathVariable Long id) {
        shelterService.deleteShelter(id);
        return ResponseEntity.ok().build();
    }
}
