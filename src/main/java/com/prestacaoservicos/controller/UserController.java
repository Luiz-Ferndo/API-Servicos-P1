package com.prestacaoservicos.controller;

import com.prestacaoservicos.dto.*;
import com.prestacaoservicos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuários", description = "Operações relacionadas à entidade de usuário")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Autenticar usuário", description = "Retorna um token JWT se as credenciais forem válidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDto> authenticateUser(@Valid @RequestBody LoginUserDto loginUserDto) {
        AuthResponseDto response = userService.authenticateUser(loginUserDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados (requer permissão de administrador)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<RecoveryUserDto>> listAllUsers() {
        List<RecoveryUserDto> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico com base no ID informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecoveryUserDto> findUserById(
            @Parameter(description = "ID do usuário a ser buscado") @PathVariable Long id) {
        RecoveryUserDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário com base no endereço de email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/search")
    public ResponseEntity<RecoveryUserDto> findUserByEmail(
            @Parameter(description = "Endereço de email do usuário") @RequestParam String email) {
        RecoveryUserDto user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecoveryUserDto> updateUser(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid
            @RequestBody UpdateUserDto updateUserDto) {
        RecoveryUserDto updatedUser = userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Excluir usuário", description = "Deleta um usuário com base no ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}