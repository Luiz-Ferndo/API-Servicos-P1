package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.repository.UserRepository;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação da interface {@link UserDetailsService} do Spring Security.
 *
 * <p>Esta classe é responsável por carregar os dados de um usuário (pelo seu email)
 * a partir do banco de dados e convertê-los em um objeto {@link UserDetails}, que o
 * Spring Security utiliza para realizar os processos de autenticação e autorização.</p>
 *
 * @version 1.0
 * @since 02/08/2025
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Construtor para injeção de dependência do repositório de usuários.
     *
     * @param userRepository O repositório para acessar os dados dos usuários.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Localiza um usuário com base em seu nome de usuário (que, neste caso, é o email).
     *
     * @param username O email do usuário a ser carregado.
     * @return um objeto {@link UserDetails} com os dados do usuário encontrado.
     * @throws UsernameNotFoundException se o usuário não for encontrado no banco de dados.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return new UserDetailsImpl(user);
    }
}