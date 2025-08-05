package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Role;
import com.prestacaoservicos.enums.RoleNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleNameEnum name);
}