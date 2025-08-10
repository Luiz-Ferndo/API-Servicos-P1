package com.prestacaoservicos.config;

import com.prestacaoservicos.entity.Permission;
import com.prestacaoservicos.entity.Role;
import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.enums.PermissionEnum;
import com.prestacaoservicos.enums.RoleNameEnum;
import com.prestacaoservicos.exception.PermissaoNaoEncontradaException;
import com.prestacaoservicos.repository.PermissionRepository;
import com.prestacaoservicos.repository.RoleRepository;
import com.prestacaoservicos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Classe de configuração responsável por inicializar dados essenciais na aplicação.
 * <p>
 * Cria permissões, papéis (roles) e um usuário administrador padrão ao iniciar o sistema,
 * garantindo que a base mínima de dados de segurança esteja configurada.
 * <p>
 * As permissões são definidas via enums e associadas às roles correspondentes.
 * O usuário administrador padrão é criado com credenciais configuradas via propriedades.
 *
 * @since 1.0
 * @version 1.0
 */
@Configuration
public class DataInitializer {

    /**
     * Email do usuário administrador padrão, injetado a partir das propriedades da aplicação.
     */
    @Value("${user_admin_email}")
    private String USER_ADMIN_EMAIL;

    /**
     * Senha do usuário administrador padrão, injetada a partir das propriedades da aplicação.
     */
    @Value("${user_admin_password}")
    private String USER_ADMIN_PASSWORD;

    /**
     * Conjunto de permissões atribuídas à role de administrador.
     */
    private static final EnumSet<PermissionEnum> ADMIN_PERMISSIONS = EnumSet.of(
            PermissionEnum.MANAGE_USERS,
            PermissionEnum.MANAGE_SERVICES,
            PermissionEnum.VIEW_REPORTS
    );

    /**
     * Conjunto de permissões atribuídas à role de cliente (customer).
     */
    private static final EnumSet<PermissionEnum> CUSTOMER_PERMISSIONS = EnumSet.of(
            PermissionEnum.BOOK_SERVICE,
            PermissionEnum.VIEW_APPOINTMENTS,
            PermissionEnum.CANCEL_APPOINTMENT,
            PermissionEnum.MAKE_PAYMENT,
            PermissionEnum.VIEW_SERVICES,
            PermissionEnum.VIEW_REPORTS
    );

    /**
     * Conjunto de permissões atribuídas à role de prestador de serviços (service provider).
     */
    private static final EnumSet<PermissionEnum> PROVIDER_PERMISSIONS = EnumSet.of(
            PermissionEnum.CONFIRM_EXECUTION,
            PermissionEnum.DEFINE_AVAILABILITY,
            PermissionEnum.VIEW_APPOINTMENTS
    );

    /**
     * Bean {@link CommandLineRunner} que inicializa os dados de permissões, roles e usuário administrador.
     * <p>
     * Executado automaticamente na inicialização da aplicação Spring Boot.
     *
     * @param permRepo          Repositório para manipulação de permissões.
     * @param roleRepo          Repositório para manipulação de roles.
     * @param userRepo          Repositório para manipulação de usuários.
     * @param passwordEncoder   Encoder para hash da senha do usuário administrador.
     * @return Runnable que executa a inicialização dos dados.
     */
    @Bean
    public CommandLineRunner initData(
            PermissionRepository permRepo,
            RoleRepository roleRepo,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            List<Permission> permissions = Arrays.stream(PermissionEnum.values())
                    .map(PermissionEnum::toPermission)
                    .map(p -> permRepo.findByName(p.getName()).orElseGet(() -> permRepo.save(p)))
                    .toList();

            List<Permission> adminPerms = ADMIN_PERMISSIONS.stream()
                    .map(p -> permRepo.findByName(p.getName())
                            .orElseThrow(() -> new PermissaoNaoEncontradaException("Permissão " + p + " não encontrada")))
                    .toList();

            List<Permission> customerPerms = CUSTOMER_PERMISSIONS.stream()
                    .map(p -> permRepo.findByName(p.getName())
                            .orElseThrow(() -> new PermissaoNaoEncontradaException("Permissão " + p + " não encontrada")))
                    .toList();

            List<Permission> providerPerms = PROVIDER_PERMISSIONS.stream()
                    .map(p -> permRepo.findByName(p.getName())
                            .orElseThrow(() -> new PermissaoNaoEncontradaException("Permissão " + p + " não encontrada")))
                    .toList();

            Role admin = roleRepo.findByName(RoleNameEnum.ROLE_ADMINISTRATOR).orElseGet(() -> {
                Role r = Role.builder().name(RoleNameEnum.ROLE_ADMINISTRATOR).build();
                r.setPermissions(adminPerms);
                return roleRepo.save(r);
            });

            Role customer = roleRepo.findByName(RoleNameEnum.ROLE_CUSTOMER).orElseGet(() -> {
                Role r = Role.builder().name(RoleNameEnum.ROLE_CUSTOMER).build();
                r.setPermissions(customerPerms);
                return roleRepo.save(r);
            });

            Role provider = roleRepo.findByName(RoleNameEnum.ROLE_SERVICE_PROVIDER).orElseGet(() -> {
                Role r = Role.builder().name(RoleNameEnum.ROLE_SERVICE_PROVIDER).build();
                r.setPermissions(providerPerms);
                return roleRepo.save(r);
            });

            if (userRepo.findByEmail(USER_ADMIN_EMAIL).isEmpty()) {
                User adminUser = User.builder()
                        .email(USER_ADMIN_EMAIL)
                        .password(passwordEncoder.encode(USER_ADMIN_PASSWORD))
                        .roles(List.of(admin))
                        .build();
                userRepo.save(adminUser);
                System.out.println("Usuário " + USER_ADMIN_EMAIL + " criado com sucesso!");
            }
        };
    }
}