package com.prestacaoservicos.service;

import com.prestacaoservicos.dto.ServicoRequestDTO;
import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.exception.RegraNegocioException;
import com.prestacaoservicos.repository.ServicoRepository;
import com.prestacaoservicos.repository.UserRepository;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsável por gerenciar as regras de negócio relacionadas a {@link Servico}.
 *
 * Fornece operações de criação, atualização, exclusão lógica e consulta de serviços.
 * Cada serviço pode estar associado a um {@link User} (prestador).
 */
@Service
public class ServicoService {

    private final ServicoRepository servicoRepo;
    private final UserRepository userRepo;

    /**
     * Construtor do service com injeção de dependências.
     *
     * @param servicoRepo Repositório de serviços
     * @param userRepo    Repositório de usuários
     */
    public ServicoService(ServicoRepository servicoRepo, UserRepository userRepo) {
        this.servicoRepo = servicoRepo;
        this.userRepo = userRepo;
    }

    /**
     * Cria um novo serviço e o associa ao usuário logado.
     *
     * @param dto           Dados do serviço enviados pelo cliente
     * @param usuarioLogado Usuário autenticado que está criando o serviço
     * @return O serviço criado
     * @throws RecursoNaoEncontradoException Se o usuário não for encontrado
     */
    @Transactional
    public Servico create(ServicoRequestDTO dto, UserDetailsImpl usuarioLogado) {
        Servico novoServico = new Servico();
        novoServico.setNome(dto.nome());
        novoServico.setValor(dto.valor());
        novoServico.setDescricao(dto.descricao());

        servicoRepo.save(novoServico);

        User prestador = userRepo.findById(usuarioLogado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        prestador.getServicosOferecidos().add(novoServico);
        userRepo.save(prestador);

        return novoServico;
    }

    /**
     * Busca um serviço pelo ID.
     *
     * @param id Identificador do serviço
     * @return O serviço encontrado
     * @throws RecursoNaoEncontradoException Se o serviço não for encontrado
     */
    public Servico findById(Long id) {
        return servicoRepo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado. ID: " + id));
    }

    /**
     * Lista todos os serviços ativos.
     *
     * @return Lista de serviços ativos
     */
    public List<Servico> findAll() {
        return servicoRepo.findAllByAtivoTrue();
    }

    /**
     * Atualiza os dados de um serviço existente.
     *
     * @param id  Identificador do serviço
     * @param dto Dados a serem atualizados
     * @return O serviço atualizado
     * @throws RecursoNaoEncontradoException Se o serviço não for encontrado
     * @throws RegraNegocioException         Se o nome informado já estiver em uso por outro serviço
     */
    @Transactional
    public Servico update(Long id, ServicoRequestDTO dto) {
        Servico servico = findById(id);

        servicoRepo.findByNomeIgnoreCase(dto.nome()).ifPresent(s -> {
            if (!s.getId().equals(id)) {
                throw new RegraNegocioException("O nome '" + dto.nome() + "' já está em uso por outro serviço.");
            }
        });

        servico.setNome(dto.nome());
        servico.setValor(dto.valor());
        servico.setDescricao(dto.descricao());

        return servicoRepo.save(servico);
    }

    /**
     * Realiza a exclusão lógica de um serviço, desativando-o.
     *
     * @param id Identificador do serviço
     * @throws RecursoNaoEncontradoException Se o serviço não for encontrado
     */
    @Transactional
    public void delete(Long id) {
        Servico servico = findById(id);

        servico.setAtivo(false);
        servicoRepo.save(servico);
    }
}
