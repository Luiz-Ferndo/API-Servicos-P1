package com.prestacaoservicos.controller;

import com.prestacaoservicos.dto.*;
import com.prestacaoservicos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador responsável pelas operações relacionadas à entidade {@code User}.
 * <p>
 * Fornece endpoints REST para autenticação, criação, atualização, listagem, busca e exclusão de usuários.
 * </p>
 *
 * @author Você
 * @since 1.0
 */
@Tag(name = "Usuários", description = "Operações relacionadas à entidade de usuário")
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    /**
     * Construtor que injeta o {@link UserService}.
     *
     * @param userService serviço responsável pelas regras de negócio da entidade usuário.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Autentica um usuário no sistema e retorna um token JWT se as credenciais forem válidas.
     *
     * @param loginUserDto DTO contendo email e senha do usuário.
     * @return {@link ResponseEntity} com o token JWT em caso de sucesso.
     */
    @Operation(summary = "Autenticar usuário", description = "Retorna um token JWT se as credenciais forem válidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @SecurityRequirements({})
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDto> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        AuthResponseDto response = userService.authenticateUser(loginUserDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param createUserDto DTO contendo os dados do novo usuário.
     * @return {@link ResponseEntity} com status {@code 201 Created} em caso de sucesso.
     */
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/users")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Lista todos os usuários cadastrados no sistema.
     * <p>
     * Requer permissão de administrador.
     * </p>
     *
     * @return {@link ResponseEntity} contendo a lista de usuários.
     */
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados (requer permissão de administrador)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<RecoveryUserDto>> findAll() {
        List<RecoveryUserDto> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Busca um usuário pelo seu identificador único.
     *
     * @param id identificador do usuário.
     * @return {@link ResponseEntity} contendo o usuário encontrado.
     */
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico com base no ID informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<RecoveryUserDto> findUserById(
            @Parameter(description = "ID do usuário a ser buscado") @PathVariable Long id) {
        RecoveryUserDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email endereço de email do usuário.
     * @return {@link ResponseEntity} contendo o usuário encontrado.
     */
    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário com base no endereço de email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<RecoveryUserDto> findUserByEmail(
            @Parameter(description = "Endereço de email do usuário") @RequestParam String email) {
        RecoveryUserDto user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param id            identificador do usuário a ser atualizado.
     * @param updateUserDto DTO contendo os novos dados do usuário.
     * @return {@link ResponseEntity} contendo o usuário atualizado.
     */
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<RecoveryUserDto> update(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        RecoveryUserDto updatedUser = userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deleta um usuário do sistema com base no seu identificador.
     *
     * @param id identificador do usuário a ser removido.
     * @return {@link ResponseEntity} sem conteúdo em caso de sucesso.
     */
    @Operation(summary = "Excluir usuário", description = "Deleta um usuário com base no ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
