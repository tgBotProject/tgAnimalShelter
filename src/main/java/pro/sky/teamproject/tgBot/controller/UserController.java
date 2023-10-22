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
import pro.sky.teamproject.tgBot.model.user.User;
import pro.sky.teamproject.tgBot.service.UserService;
import pro.sky.teamproject.tgBot.utils.ErrorUtils;

import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции с пользователями")
@ApiResponses(value = {@ApiResponse(responseCode = "500",
        description = "Произошла ошибка, не зависящая от вызывающей стороны")})
public class UserController {

    private final UserService service;

    /* CREATE */
        //все пользователи добавляются через бота
    /* READ */

    @Operation(summary = "Получить всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Записи найдены",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))),
            @ApiResponse(responseCode = "404", description = "Записи не найдены")})
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = service.findUsers();
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получить пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Adoption.class))}),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@Parameter(description = "Идентификатор пользователя")
                                                @PathVariable("id") Long id) {
        User foundUser = service.findUser(id);
        if (foundUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundUser);
    }

    @Operation(summary = "Получить всех пользователей с определенной ролью")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи найдены",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Adoption.class)))),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены")})
    @GetMapping("/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@Parameter(description = "Искомая роль (из enum Role)")
                                                         @PathVariable("role") String role) {
        List<User> foundUsers = service.findUsersByRole(role);
        if (foundUsers == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundUsers);
    }

    /* UPDATE */
    @Operation(summary = "Изменить данные пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные изменены",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Adoption.class))}),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @PutMapping
    public ResponseEntity<?> editUser(@RequestBody @Valid User User,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorUtils.errorsList(bindingResult), HttpStatus.BAD_REQUEST);
        }
        User currentUser = service.findUser(User.getId());
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }
        User updatedUser = service.updateUser(User);
        return ResponseEntity.ok(updatedUser);
    }

    /* DELETE */
    @Operation(summary = "Удалить пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")})
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "Идентификатор пользователя")
                                               @PathVariable Long id) {
        User currentUser = service.findUser(id);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
