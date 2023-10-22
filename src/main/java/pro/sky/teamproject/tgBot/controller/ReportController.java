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
import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.service.ReportService;
import pro.sky.teamproject.tgBot.utils.ErrorUtils;

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "Отчеты", description = "Операции с отчетами")
@ApiResponses(value = {@ApiResponse(responseCode = "500",
        description = "Произошла ошибка, не зависящая от вызывающей стороны")})
public class ReportController {
    private final ReportService service;

    /* ADD the report */
    @Operation(summary = "Добавить отчет")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет добавлен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных отчета")})
    @PostMapping
    public ResponseEntity<?> addReport(@RequestBody @Valid Report report,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorUtils.errorsList(bindingResult), HttpStatus.BAD_REQUEST);
        }
        Report newReport = service.addReport(report);
        return ResponseEntity.ok(newReport);
    }

    /* GET all reports */
    @Operation(summary = "Получить все отчеты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчеты найдены",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Report.class)))),
            @ApiResponse(responseCode = "404", description = "Отчеты не найдены")})
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = service.findAllReports();
        if (reports.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reports);
    }

    /* GET report by ID */
    @Operation(summary = "Получить отчет по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет найден",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report.class))),
            @ApiResponse(responseCode = "404", description = "Отчет не найден")})
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@Parameter(description = "Идентификатор отчета")
                                                @PathVariable Long id) {
        Report report = service.findReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    /* UPDATE report */
    @Operation(summary = "Обновить отчет")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет обновлен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report.class))),
            @ApiResponse(responseCode = "404", description = "Отчет не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка в данных отчета")})
    @PutMapping
    public ResponseEntity<?> updateReport(@RequestBody @Valid Report report, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorUtils.errorsList(bindingResult), HttpStatus.BAD_REQUEST);
        }
        Report updatedReport = service.updateReport(report);
        return ResponseEntity.ok(updatedReport);
    }

    /* DELETE report */
    @Operation(summary = "Удалить отчет по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет успешно удален"),
            @ApiResponse(responseCode = "404", description = "Отчет не найден")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@Parameter(description = "Идентификатор отчета")
                                             @PathVariable Long id) {
        service.deleteReport(id);
        return ResponseEntity.ok().build();
    }

    /* VALIDATE report */
    @Operation(summary = "Подтвердить или отклонить отчет")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет обновлен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report.class))),
            @ApiResponse(responseCode = "404", description = "Отчет не найден")})
    @PatchMapping("/{id}/validate")
    public ResponseEntity<Report> validateReport(@Parameter(description = "Идентификатор отчета")
                                                 @PathVariable Long id,
                                                 @RequestParam("isValid") boolean isValid) {
        Report report = service.validateReport(id, isValid);
        return ResponseEntity.ok(report);
    }



}
