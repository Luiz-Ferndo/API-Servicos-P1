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

@Configuration
public class DataInitializer {
    @Value("${user_admin_email}")
    private String USER_ADMIN_EMAIL;

    @Value("${user_admin_password}")
    private String USER_ADMIN_PASSWORD;

    private static final EnumSet<PermissionEnum> ADMIN_PERMISSIONS = EnumSet.of(
            PermissionEnum.MANAGE_USERS,
            PermissionEnum.MANAGE_SERVICES,
            PermissionEnum.VIEW_REPORTS
    );

    private static final EnumSet<PermissionEnum> CUSTOMER_PERMISSIONS = EnumSet.of(
            PermissionEnum.BOOK_SERVICE,
            PermissionEnum.VIEW_APPOINTMENTS,
            PermissionEnum.CANCEL_APPOINTMENT,
            PermissionEnum.MAKE_PAYMENT,
            PermissionEnum.VIEW_SERVICES,
            PermissionEnum.VIEW_REPORTS
    );

    private static final EnumSet<PermissionEnum> PROVIDER_PERMISSIONS = EnumSet.of(
            PermissionEnum.CONFIRM_EXECUTION,
            PermissionEnum.DEFINE_AVAILABILITY,
            PermissionEnum.VIEW_APPOINTMENTS
    );

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
                System.out.println("Usuário "+ USER_ADMIN_EMAIL + " criado com sucesso!");
            }
        };
    }
}