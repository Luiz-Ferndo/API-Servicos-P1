package com.prestacaoservicos.service;

import com.prestacaoservicos.dto.*;
import com.prestacaoservicos.dto.RecoveryUserDto;
import com.prestacaoservicos.entity.Role;
import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.enums.RoleNameEnum;
import com.prestacaoservicos.exception.CredenciaisInvalidasException;
import com.prestacaoservicos.exception.EnumInvalidoException;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.exception.RegraNegocioException;
import com.prestacaoservicos.repository.RoleRepository;
import com.prestacaoservicos.repository.UserRepository;
import com.prestacaoservicos.security.config.SecurityConfiguration;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço que encapsula a lógica de negócio para operações relacionadas a usuários.
 */
@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final SecurityConfiguration securityConfiguration;
    private final RoleRepository roleRepository;

    public UserService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       UserRepository userRepository,
                       SecurityConfiguration securityConfiguration, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.securityConfiguration = securityConfiguration;
        this.roleRepository = roleRepository;
    }

    /**
     * Autentica um usuário e retorna um token JWT se as credenciais forem válidas.
     * @param loginUserDto DTO com email e senha.
     * @throws CredenciaisInvalidasException se as credenciais forem inválidas.
     * @return DTO contendo o token JWT.
     */
    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password());

            Authentication authentication = authenticationManager.authenticate(authToken);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
        } catch (AuthenticationException e) {
            throw new CredenciaisInvalidasException("Email ou senha inválidos.");
        }
    }

    /**
     * Cria um novo usuário no sistema, com validação de email e role.
     * @param createUserDto DTO com dados para criação.
     * @throws RegraNegocioException se o email já estiver em uso.
     * @throws EnumInvalidoException se a role fornecida for inválida.
     */
    public void createUser(CreateUserDto createUserDto) {
        if (userRepository.findByEmail(createUserDto.email()).isPresent()) {
            throw new RegraNegocioException("O email informado já está em uso.");
        }

        RoleNameEnum roleEnum;
        try {
            roleEnum = RoleNameEnum.valueOf(createUserDto.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EnumInvalidoException("A role '" + createUserDto.role() + "' é inválida.");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RegraNegocioException("A role '" + roleEnum + "' não existe no sistema."));

        User newUser = User.builder()
                .email(createUserDto.email())
                .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                .roles(List.of(role))
                .build();

        userRepository.save(newUser);
    }

    /**
     * Retorna uma lista com todos os usuários do sistema.
     * @return Uma lista de {@link RecoveryUserDto}.
     */
    public List<RecoveryUserDto> listAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário pelo seu ID.
     * @param id O ID do usuário.
     * @return O {@link RecoveryUserDto} correspondente.
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado.
     */
    public RecoveryUserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID " + id + " não encontrado."));
        return convertToUserDto(user);
    }

    /**
     * Busca um usuário pelo seu email.
     * @param email O email do usuário.
     * @return O {@link RecoveryUserDto} correspondente.
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado.
     */
    public RecoveryUserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com email '" + email + "' não encontrado."));
        return convertToUserDto(user);
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param id O ID do usuário a ser atualizado.
     * @param updateUserDto DTO com os novos dados.
     * @return O {@link RecoveryUserDto} com os dados atualizados.
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado.
     */
    public RecoveryUserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com ID " + id + " não encontrado para atualização."));

        if (updateUserDto.email() != null && !updateUserDto.email().equals(existingUser.getEmail())) {
            Optional<User> userWithNewEmail = userRepository.findByEmail(updateUserDto.email());
            if (userWithNewEmail.isPresent() && !userWithNewEmail.get().getId().equals(id)) {
                throw new RegraNegocioException("O novo email informado já está em uso por outro usuário.");
            }
            existingUser.setEmail(updateUserDto.email());
        }

        if (updateUserDto.name() != null) {
            // @TODO: Implementar lógica para atualizar o nome.
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToUserDto(updatedUser);
    }

    /**
     * Deleta um usuário pelo seu ID.
     * @param id O ID do usuário a ser deletado.
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário com ID " + id + " não encontrado para exclusão.");
        }
        userRepository.deleteById(id);
    }

    /**
     * Método auxiliar para converter uma entidade User para RecoveryUserDto.
     * @param user A entidade {@link User}.
     * @return O {@link RecoveryUserDto} correspondente.
     */
    private RecoveryUserDto convertToUserDto(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return new RecoveryUserDto(user.getId(), user.getEmail(), roleNames);
    }
}