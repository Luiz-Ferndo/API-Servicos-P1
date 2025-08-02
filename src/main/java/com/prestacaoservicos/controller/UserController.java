package com.prestacaoservicos.controller;

import com.prestacaoservicos.dto.CreateUserDto;
import com.prestacaoservicos.dto.LoginUserDto;
import com.prestacaoservicos.dto.RecoveryJwtTokenDto;
import com.prestacaoservicos.dto.RecoveryUserDto;
import com.prestacaoservicos.dto.UpdateUserDto;
import com.prestacaoservicos.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciar as operações relacionadas a usuários.
 * Expõe endpoints para autenticação, criação, e gerenciamento de usuários.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Autentica um usuário e retorna um token JWT se as credenciais forem válidas.
     *
     * @param loginUserDto DTO contendo o email e a senha do usuário.
     * @return um ResponseEntity contendo o token JWT e o status HTTP 200 (OK).
     */
    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto) {
        RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param createUserDto DTO contendo os dados para a criação do novo usuário.
     * @return um ResponseEntity com o status HTTP 201 (CREATED) se o usuário for criado com sucesso.
     */
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Retorna uma lista de todos os usuários cadastrados.
     * Este endpoint deve ser protegido e acessível apenas por administradores.
     *
     * @return um ResponseEntity contendo a lista de usuários e o status HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<RecoveryUserDto>> listAllUsers() {
        List<RecoveryUserDto> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Busca e retorna um usuário pelo seu ID.
     *
     * @param id O ID do usuário a ser buscado.
     * @return um ResponseEntity contendo os dados do usuário e o status HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecoveryUserDto> findUserById(@PathVariable Long id) {
        RecoveryUserDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Busca e retorna um usuário pelo seu endereço de email.
     *
     * @param email O email do usuário a ser buscado.
     * @return um ResponseEntity contendo os dados do usuário e o status HTTP 200 (OK).
     */
    @GetMapping("/search")
    public ResponseEntity<RecoveryUserDto> findUserByEmail(@RequestParam String email) {
        RecoveryUserDto user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param id O ID do usuário a ser atualizado.
     * @param updateUserDto DTO contendo os dados a serem atualizados.
     * @return um ResponseEntity contendo os dados atualizados do usuário e o status HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecoveryUserDto> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        RecoveryUserDto updatedUser = userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deleta um usuário do sistema pelo seu ID.
     *
     * @param id O ID do usuário a ser deletado.
     * @return um ResponseEntity com o status HTTP 204 (NO CONTENT) se a exclusão for bem-sucedida.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}