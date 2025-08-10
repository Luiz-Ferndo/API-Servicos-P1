package com.prestacaoservicos.security.userdetails;

import com.prestacaoservicos.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementação da interface {@link UserDetails} do Spring Security.
 * <p>
 * Esta classe atua como um "adaptador" entre a entidade de usuário da aplicação ({@link User})
 * e o formato que o Spring Security espera para realizar a autenticação e autorização.
 * Ela encapsula um objeto {@code User} para fornecer as informações necessárias.
 *
 * @version 1.0
 * @since 02/08/2025
 */
public class UserDetailsImpl implements UserDetails {
    private final User user;

    /**
     * Construtor que recebe a entidade de usuário da aplicação.
     *
     * @param user A entidade {@link User} contendo os dados do usuário.
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Retorna as autorizações (roles) concedidas ao usuário.
     * <p>
     * Mapeia a lista de {@code Role} do usuário para uma coleção de {@link SimpleGrantedAuthority},
     * que é o formato requerido pelo Spring Security.
     *
     * @return Uma coleção de {@link GrantedAuthority}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna o ID do usuário.
     * <p>
     * Este método é utilizado pelo Spring Security para identificar o usuário autenticado.
     *
     * @return O ID do usuário.
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Retorna a senha usada para autenticar o usuário.
     *
     * @return A senha do usuário.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Retorna o nome de usuário usado para autenticar o usuário.
     * <p>
     * Neste sistema, o email do usuário é utilizado como nome de usuário.
     *
     * @return O email do usuário.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indica se a conta do usuário expirou.
     * <p>
     * Por padrão, retorna {@code true}. A lógica pode ser estendida para verificar
     * um campo de data de expiração na entidade {@code User}.
     *
     * @return {@code true} se a conta for válida (não expirada).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está bloqueado ou desbloqueado.
     * <p>
     * Por padrão, retorna {@code true}. A lógica pode ser estendida para verificar
     * um campo de status na entidade {@code User}.
     *
     * @return {@code true} se o usuário não estiver bloqueado.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais do usuário (senha) expiraram.
     * <p>
     * Por padrão, retorna {@code true}. A lógica pode ser estendida para forçar
     * a alteração de senha periodicamente.
     *
     * @return {@code true} se as credenciais forem válidas (não expiradas).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado ou desabilitado.
     * <p>
     * Por padrão, retorna {@code true}. A lógica pode ser estendida para desativar
     * contas de usuário através de um campo booleano na entidade {@code User}.
     *
     * @return {@code true} se o usuário estiver habilitado.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}