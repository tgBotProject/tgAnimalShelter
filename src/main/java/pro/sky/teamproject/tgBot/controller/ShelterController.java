package pro.sky.teamproject.tgBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.teamproject.tgBot.model.Shelters;
import pro.sky.teamproject.tgBot.service.ShelterService;

@RestController
@RequestMapping("/shelter")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @Operation
            (summary = "Добавление приюта в базу данных",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Приют добавлен",
                                    content = {
                                            @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = Shelters.class)
                                            )
                                    }
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Приют уже находится в базе данных"
                            )
                    },
                    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Новый приют",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = Shelters.class)
                                    )
                            }
                    )
            )
    @PostMapping()
    public ResponseEntity<Shelters> addShelters(@RequestBody Shelters shelters) {
        Shelters addShelters = shelterService.addShelters(shelters);
        return ResponseEntity.ok(addShelters);
    }

    @Operation
            (summary = "Редактирование информации о приюте",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Новая информация сохранена",
                                    content = {
                                            @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = Shelters.class)
                                            )
                                    }
                            ),
                    },
                    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Новая информация о приюте",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = Shelters.class)
                                    )
                            }
                    )
            )
    @PutMapping
    public ResponseEntity<Shelters> editShelters(@RequestBody Shelters shelters) {
        Shelters editShelters = shelterService.editShelters(shelters);
        if (editShelters == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(editShelters);
    }

    @Operation
            (summary = "Поиск приюта в базе данных",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Приют найден",
                                    content = {
                                            @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = Shelters.class)
                                            )
                                    }
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Приют не найден"
                            ),

                            @ApiResponse(
                                    responseCode = "500",
                                    description = "Сервер не может обработать запрос",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE
                                    )
                            )
                    }
            )
    @GetMapping("{id}")
    public ResponseEntity<Shelters> findShelters(@RequestParam(required = false) Long id) {
        return ResponseEntity.ok(shelterService.findShelters(id));
    }

    @Operation
            (summary = "Удаление приюта из базы данных",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Приют удален",
                                    content = {
                                            @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = Shelters.class)
                                            )
                                    }
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Приют не найден"
                            ),
                    }
            )
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteShelters(@PathVariable Long id) {
        shelterService.deleteShelter(id);
        return ResponseEntity.ok().build();
    }
}
