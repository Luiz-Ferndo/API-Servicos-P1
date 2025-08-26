package com.prestacaoservicos.config;

import com.prestacaoservicos.entity.Permission;
import com.prestacaoservicos.entity.Role;
import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.enums.PermissionEnum;
import com.prestacaoservicos.enums.RoleNameEnum;
import com.prestacaoservicos.repository.PermissionRepository;
import com.prestacaoservicos.repository.RoleRepository;
import com.prestacaoservicos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classe responsável por inicializar os dados básicos do sistema
 * (permissões, papéis e usuário administrador).
 *
 * <p>Executada automaticamente ao iniciar a aplicação, garantindo que:
 * <ul>
 *     <li>Todas as permissões definidas em {@link PermissionEnum} estejam no banco.</li>
 *     <li>Papéis de administrador, cliente e prestador sejam criados/atualizados.</li>
 *     <li>Um usuário administrador padrão seja cadastrado (se não existir).</li>
 * </ul>
 * </p>
 */
@Configuration
public class DataInitializer {

    /** Nome do administrador definido em application.properties */
    @Value("${app.admin.name}")
    private String adminName;

    /** Email do administrador definido em application.properties */
    @Value("${app.admin.email}")
    private String adminEmail;

    /** Senha do administrador definido em application.properties */
    @Value("${app.admin.password}")
    private String adminPassword;

    /** Permissões atribuídas ao papel de Administrador */
    private static final EnumSet<PermissionEnum> ADMIN_PERMISSIONS = EnumSet.of(
            PermissionEnum.MANAGE_USERS,
            PermissionEnum.MANAGE_SERVICES,
            PermissionEnum.VIEW_REPORTS
    );

    /** Permissões atribuídas ao papel de Cliente */
    private static final EnumSet<PermissionEnum> CUSTOMER_PERMISSIONS = EnumSet.of(
            PermissionEnum.BOOK_SERVICE,
            PermissionEnum.VIEW_APPOINTMENTS,
            PermissionEnum.CANCEL_APPOINTMENT,
            PermissionEnum.MAKE_PAYMENT,
            PermissionEnum.VIEW_SERVICES
    );

    /** Permissões atribuídas ao papel de Prestador de Serviço */
    private static final EnumSet<PermissionEnum> PROVIDER_PERMISSIONS = EnumSet.of(
            PermissionEnum.CONFIRM_EXECUTION,
            PermissionEnum.DEFINE_AVAILABILITY,
            PermissionEnum.VIEW_APPOINTMENTS
    );

    /**
     * Inicializa os dados básicos da aplicação ao iniciar.
     *
     * @param permissionRepo repositório de permissões
     * @param roleRepo repositório de papéis
     * @param userRepo repositório de usuários
     * @param passwordEncoder encoder para senhas
     * @return {@link CommandLineRunner} que executa a carga inicial de dados
     */
    @Bean
    @Transactional
    public CommandLineRunner initData(
            PermissionRepository permissionRepo,
            RoleRepository roleRepo,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Map<String, Permission> permissionsMap = createPermissions(permissionRepo);

            Set<Permission> adminPerms = getPermissionsFromEnum(permissionsMap, ADMIN_PERMISSIONS);
            Role adminRole = createOrUpdateRole(roleRepo, RoleNameEnum.ROLE_ADMINISTRATOR, adminPerms);

            Set<Permission> customerPerms = getPermissionsFromEnum(permissionsMap, CUSTOMER_PERMISSIONS);
            createOrUpdateRole(roleRepo, RoleNameEnum.ROLE_CUSTOMER, customerPerms);

            Set<Permission> providerPerms = getPermissionsFromEnum(permissionsMap, PROVIDER_PERMISSIONS);
            createOrUpdateRole(roleRepo, RoleNameEnum.ROLE_SERVICE_PROVIDER, providerPerms);

            createAdminUser(userRepo, passwordEncoder, adminRole);
        };
    }

    /**
     * Cria todas as permissões definidas em {@link PermissionEnum}, caso não existam no banco.
     *
     * @param permissionRepo repositório de permissões
     * @return mapa de permissões, indexado pelo nome
     */
    private Map<String, Permission> createPermissions(PermissionRepository permissionRepo) {
        return EnumSet.allOf(PermissionEnum.class).stream()
                .map(pEnum -> permissionRepo.findByName(pEnum.getName())
                        .orElseGet(() -> permissionRepo.save(pEnum.toPermission())))
                .collect(Collectors.toMap(Permission::getName, Function.identity()));
    }

    /**
     * Recupera as permissões correspondentes a um conjunto de {@link PermissionEnum}.
     *
     * @param map mapa de permissões já persistidas
     * @param enumSet conjunto de enums de permissões
     * @return conjunto de permissões prontas para atribuição
     */
    private Set<Permission> getPermissionsFromEnum(Map<String, Permission> map, EnumSet<PermissionEnum> enumSet) {
        return enumSet.stream()
                .map(pEnum -> map.get(pEnum.getName()))
                .collect(Collectors.toSet());
    }

    /**
     * Cria ou atualiza um papel (role) no banco, atribuindo suas permissões.
     *
     * @param roleRepo repositório de papéis
     * @param roleName nome do papel
     * @param permissions permissões atribuídas ao papel
     * @return papel salvo/atualizado
     */
    private Role createOrUpdateRole(RoleRepository roleRepo, RoleNameEnum roleName, Set<Permission> permissions) {
        Role role = roleRepo.findByName(roleName)
                .orElse(new Role(roleName));

        role.setPermissions(permissions);
        return roleRepo.save(role);
    }

    /**
     * Cria o usuário administrador padrão caso ele não exista.
     *
     * @param userRepo repositório de usuários
     * @param passwordEncoder encoder de senha
     * @param adminRole papel de administrador atribuído ao usuário
     */
    private void createAdminUser(UserRepository userRepo, PasswordEncoder passwordEncoder, Role adminRole) {
        if (userRepo.findByEmail(adminEmail).isEmpty()) {
            User adminUser = new User(
                    adminName,
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    Set.of(adminRole)
            );
            userRepo.save(adminUser);
            System.out.println("Usuário administrador '" + adminEmail + "' criado com sucesso!");
        }
    }
}
