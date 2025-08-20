package com.prestacaoservicos.service;

import com.prestacaoservicos.dto.*;
import com.prestacaoservicos.dto.RecoveryUserDto;
import com.prestacaoservicos.entity.Permission;
import com.prestacaoservicos.entity.Role;
import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.entity.UserPhone;
import com.prestacaoservicos.enums.RoleNameEnum;
import com.prestacaoservicos.exception.CredenciaisInvalidasException;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.exception.RegraNegocioException;
import com.prestacaoservicos.repository.RoleRepository;
import com.prestacaoservicos.repository.UserPhoneRepository;
import com.prestacaoservicos.repository.UserRepository;
import com.prestacaoservicos.security.config.SecurityConfiguration;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final UserPhoneRepository userPhoneRepository;

    private final String DEFAULT_TOKEN_TYPE = "Bearer";

    public UserService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       UserRepository userRepository,
                       SecurityConfiguration securityConfiguration,
                       RoleRepository roleRepository,
                          UserPhoneRepository userPhoneRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.securityConfiguration = securityConfiguration;
        this.roleRepository = roleRepository;
        this.userPhoneRepository = userPhoneRepository;
    }

    /**
     * Autentica um usuário com base no email e senha fornecidos.
     * @param loginUserDto DTO contendo email e senha do usuário.
     * @return Um {@link AuthResponseDto} contendo o token JWT e informações do usuário.
     * @throws CredenciaisInvalidasException se as credenciais forem inválidas.
     */
    public AuthResponseDto authenticateUser(LoginUserDto loginUserDto) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginUserDto.email(),
                            loginUserDto.password()
                    );

            Authentication authentication = authenticationManager.authenticate(authToken);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            String token = jwtTokenService.generateToken(userDetails);
            long expiresIn = jwtTokenService.getExpirationInSeconds();

            AuthResponseDto.UserInfo userInfo = new AuthResponseDto.UserInfo(
                    userDetails.getId(),
                    userDetails.getName(),
                    userDetails.getUsername(),
                    userDetails.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList(),
                    userDetails.getPermissions()
                            .stream()
                            .toList()
            );

            return new AuthResponseDto(
                    token,
                    DEFAULT_TOKEN_TYPE,
                    expiresIn,
                    userInfo
            );
        } catch (AuthenticationException e) {
            throw new CredenciaisInvalidasException("Email ou senha inválidos.");
        }
    }

    /**
     * Cria um novo usuário no sistema.
     * @param createUserDto DTO contendo os dados do novo usuário.
     * @throws RegraNegocioException se o email já estiver em uso ou a role não existir.
     */
    @Transactional
    public void createUser(CreateUserDto createUserDto) {
        if (userRepository.findByEmail(createUserDto.email()).isPresent()) {
            throw new RegraNegocioException("O email informado já está em uso.");
        }

        RoleNameEnum roleEnum = createUserDto.role();
        if (roleEnum == null) {
            throw new RegraNegocioException("A role deve ser informada.");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RegraNegocioException("A role '" + roleEnum + "' não existe no sistema."));

        User newUser = User.builder()
                .name(createUserDto.name())
                .email(createUserDto.email())
                .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                .roles(List.of(role))
                .phones(new ArrayList<>())
                .build();

        if (createUserDto.phones() != null && !createUserDto.phones().isEmpty()) {
            addUserPhones(newUser, createUserDto.phones());
        }

        userRepository.save(newUser);
    }

    /**
     * Retorna uma lista com todos os usuários do sistema.
     * @return Uma lista de {@link RecoveryUserDto}.
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public RecoveryUserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com email '" + email + "' não encontrado."));
        return convertToUserDto(user);
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param id O ID do usuário a ser atualizado.
     * @param updateUserDto DTO contendo os novos dados do usuário.
     * @return O {@link RecoveryUserDto} atualizado.
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado.
     * @throws RegraNegocioException se o novo email já estiver em uso por outro usuário.
     */
    @Transactional
    public RecoveryUserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (updateUserDto.name() != null) {
            existingUser.setName(updateUserDto.name());
        }

        if (updateUserDto.email() != null &&
                !Objects.equals(updateUserDto.email(), existingUser.getEmail())) {

            if (userRepository.findByEmail(updateUserDto.email()).isPresent()) {
                throw new RegraNegocioException("O email informado já está em uso.");
            }
            existingUser.setEmail(updateUserDto.email());
        }

        if (updateUserDto.password() != null) {
            existingUser.setPassword(securityConfiguration.passwordEncoder()
                    .encode(updateUserDto.password()));
        }

        if (updateUserDto.phones() != null) {
            existingUser.getPhones().clear();
            addUserPhones(existingUser, updateUserDto.phones());
        }

        User updatedUser = userRepository.save(existingUser);

        return convertToUserDto(updatedUser);
    }

    /**
     * Deleta um usuário pelo seu ID.
     * @param id O ID do usuário a ser deletado.
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado.
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário com ID " + id + " não encontrado para exclusão.");
        }
        userRepository.deleteById(id);
    }

    /**
     * Converte uma entidade {@link User} em um {@link RecoveryUserDto}.
     *
     * @param user A entidade {@link User}.
     * @return O DTO {@link RecoveryUserDto} correspondente.
     */
    private RecoveryUserDto convertToUserDto(User user) {
        List<String> roleNames = (user.getRoles() == null) ?
                List.of() :
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .toList();

        List<String> permissions = (user.getRoles() == null) ?
                List.of() :
                user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .distinct()
                        .toList();

        List<PhoneDto> phones = (user.getPhones() == null) ?
                List.of() :
                user.getPhones().stream()
                        .map(phone -> new PhoneDto(phone.getPhone(), phone.getType()))
                        .toList();

        return new RecoveryUserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roleNames,
                permissions,
                phones
        );
    }


    /**
     * Adiciona telefones a um usuário.
     * @param user O usuário ao qual os telefones serão adicionados.
     * @param phones Lista de DTOs de telefone a serem adicionados.
     * @throws RegraNegocioException se algum telefone não tiver tipo definido.
     */
    private void addUserPhones(User user, List<PhoneDto> phones) {
        for (PhoneDto phoneDto : phones) {
            if (phoneDto.type() == null) {
                throw new RegraNegocioException("O tipo de telefone deve ser informado.");
            }
            UserPhone phone = new UserPhone();
            phone.setPhone(phoneDto.phone());
            phone.setType(phoneDto.type());
            phone.setUser(user);
            user.getPhones().add(phone);
        }
    }

}