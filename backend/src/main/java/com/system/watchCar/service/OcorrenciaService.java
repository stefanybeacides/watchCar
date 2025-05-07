package com.system.watchCar.service;

import javax.persistence.criteria.Predicate;

import com.system.watchCar.dto.DenunciaRequest;
import com.system.watchCar.dto.OcorrenciaDTO;
import com.system.watchCar.dto.OcorrenciaDetalhadaResponse;
import com.system.watchCar.dto.ResponsavelResponse;
import com.system.watchCar.entity.*;
import com.system.watchCar.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final UserRepository userRepository;
    private final TipoVeiculoRepository tipoVeiculoRepository;
    private final VeiculoRepository veiculoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final ArtigoRepository artigoRepository;

    public OcorrenciaService(OcorrenciaRepository repository, UserRepository userRepository, TipoVeiculoRepository tipoVeiculoRepository, VeiculoRepository veiculoRepository, ResponsavelRepository responsavelRepository, ArtigoRepository artigoRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.tipoVeiculoRepository = tipoVeiculoRepository;
        this.veiculoRepository = veiculoRepository;
        this.responsavelRepository = responsavelRepository;
        this.artigoRepository = artigoRepository;
    }



    @Transactional
    public Page<OcorrenciaDTO> obterOcorrenciasComDetalhes(String status, String artigo, String hora,
                                                           LocalDateTime dataInicio, LocalDateTime dataFim,
                                                           int page, int size) {
        // Definir a páginação
        PageRequest pageRequest = PageRequest.of(page, size);

        // Buscar as ocorrências com filtros
        Page<Ocorrencia> ocorrencias = repository.findByFilters(status, artigo, hora, dataInicio, dataFim, pageRequest);

        // Mapear as ocorrências para o DTO com dados adicionais
        List<OcorrenciaDTO> ocorrenciasComDetalhes = ocorrencias.getContent().stream()
                .map(ocorrencia -> {
                    // Buscar o usuário
                    User usuario = userRepository.findById(ocorrencia.getIdUsuario()).orElse(null);

                    // Buscar o veículo
                    Veiculo veiculo = veiculoRepository.findById(ocorrencia.getIdVeiculo()).orElse(null);

                    // Buscar o artigo relacionado à ocorrência
                    Artigo artigoCriminal = artigoRepository.findById(Long.valueOf(ocorrencia.getCodArtigo())).orElse(null);

                    // Mapear a ocorrência para o DTO com dados do usuário, veículo e artigo
                    OcorrenciaDTO dto = new OcorrenciaDTO();
                    dto.setId(ocorrencia.getId());
                    dto.setDescricaoOcorrencia(ocorrencia.getDescricaoOcorrencia());
                    dto.setStatusDenuncia(ocorrencia.getStatusDenuncia());
                    dto.setHoraOcorrencia(ocorrencia.getHoraOcorrencia());
                    dto.setDataHora(ocorrencia.getDataHora());

                    if (usuario != null) {
                        dto.setUsuarioNome(usuario.getUsername());
                        dto.setUsuarioEmail(usuario.getEmail());
                    }

                    if (veiculo != null) {
                        dto.setVeiculoPlaca(veiculo.getPlaca());
                        dto.setVeiculoModelo(veiculo.getTipoVeiculo().getModelo());
                        dto.setVeiculoMarca(veiculo.getTipoVeiculo().getMarca());
                    }

                    if (artigoCriminal != null) {
                        dto.setArtigoId(artigoCriminal.getId());
                        dto.setArtigoCodigo(artigoCriminal.getCodArtigo());
                        dto.setArtigoDescricao(artigoCriminal.getDescricao());
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        // Retornar as ocorrências completas como um Page DTO
        return new PageImpl<>(ocorrenciasComDetalhes, pageRequest, ocorrencias.getTotalElements());
    }


    @Transactional
    public Ocorrencia criarDenuncia(DenunciaRequest request) {
        // 1. Validar e buscar usuário
        User usuario = userRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Validar o status da denúncia
        if (!isValidStatus(request.getStatusDenuncia())) {
            throw new RuntimeException("Status da denúncia inválido");
        }

        // 3. Criar ou buscar tipo de veículo
        TipoVeiculo tipo = tipoVeiculoRepository.findByTipoAndModeloAndMarcaAndCor(
                request.getTipo(), request.getModelo(), request.getMarca(), request.getCor()
        ).orElseGet(() -> {
            TipoVeiculo novoTipo = new TipoVeiculo();
            novoTipo.setTipo(request.getTipo());
            novoTipo.setModelo(request.getModelo());
            novoTipo.setMarca(request.getMarca());
            novoTipo.setCor(request.getCor());
            return tipoVeiculoRepository.save(novoTipo);
        });

        // 4. Criar veículo
        Veiculo veiculo = new Veiculo();
        veiculo.setTipoVeiculo(tipo);
        veiculo.setAno(request.getAno());
        veiculo.setPlaca(request.getPlaca());
        veiculo = veiculoRepository.save(veiculo);


        // 5. Criar denúncia
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setIdUsuario(usuario.getId());
        ocorrencia.setDescricaoOcorrencia(request.getDescricao());
        ocorrencia.setStatusDenuncia(request.getStatusDenuncia());
        ocorrencia.setHoraOcorrencia(request.getHoraOcorrencia());
        ocorrencia.setDataHora(request.getDataHora() != null ? request.getDataHora() : LocalDateTime.now()); // Usar a data atual caso não seja fornecida
        ocorrencia.setIdVeiculo(veiculo.getId());
        ocorrencia.setCodArtigo(request.getArtigoLei());
        ocorrencia = repository.save(ocorrencia);

        Responsavel responsavel = new Responsavel();
        responsavel.setDenuncia(ocorrencia);
        responsavel.setUsuario(usuario);
        responsavel.setNumDistintivo(usuario.getBadge());
        responsavel.setDelegacia(usuario.getDelegate());
        responsavel.setDataCriacao(LocalDateTime.now());
        responsavelRepository.save(responsavel);

        // 6. Se o usuário tiver distintivo e delegacia, cria um responsável
        if (usuario.getBadge() != null && usuario.getDelegate() != null) {
            Responsavel resp = new Responsavel();
            resp.setDenuncia(ocorrencia);
            resp.setUsuario(usuario);
            resp.setNumDistintivo(usuario.getBadge());
            resp.setDelegacia(usuario.getDelegate());
            responsavelRepository.save(resp);
        }

        return ocorrencia;
    }

    // Método para validar o status da denúncia
    private boolean isValidStatus(String status) {
        // Validar que o status está dentro dos valores possíveis
        return "Em andamento".equals(status) || "Solucionado".equals(status) || "Arquivado".equals(status);
    }

    public OcorrenciaDetalhadaResponse buscarDetalhesPorId(Long id) {
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        User usuario = userRepository.findById(ocorrencia.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Veiculo veiculo = veiculoRepository.findById(ocorrencia.getIdVeiculo())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        // Responsável atual (último a assumir)
        Optional<Responsavel> responsavelAtual = responsavelRepository.findTopByDenunciaIdOrderByDataCriacaoDesc(id);

        // Histórico de responsáveis
        List<Responsavel> historico = responsavelRepository.findByDenunciaIdOrderByDataCriacaoDesc(id);
        List<OcorrenciaDetalhadaResponse.ResponsavelHistoricoDto> historicoDtos = historico.stream().map(r ->
                new OcorrenciaDetalhadaResponse.ResponsavelHistoricoDto(r.getUsuario().getUsername(), r.getDataCriacao(), r.getNumDistintivo(), r.getDelegacia())
        ).collect(Collectors.toList());

        OcorrenciaDetalhadaResponse response = new OcorrenciaDetalhadaResponse();
        response.setId(ocorrencia.getId());
        response.setUsuarioNome(usuario.getUsername());
        response.setVeiculoPlaca(veiculo.getPlaca());
        response.setVeiculoModelo(veiculo.getTipoVeiculo().getModelo());
        response.setStatusDenuncia(ocorrencia.getStatusDenuncia());
        response.setResponsavelId(responsavelAtual.map(r -> r.getUsuario().getId()).orElse(null));
        response.setHistoricoResponsaveis(historicoDtos);

        return response;
    }

    public boolean verificarResponsavel(Long id, String usuarioId) {
        // Recupera a ocorrência pelo id
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        // Converte o usuarioId para Long
        Long usuarioIdLong = Long.valueOf(usuarioId);

        // Busca o responsável ativo pelo id do usuário e status igual a 1
        Responsavel responsavel = responsavelRepository.findByUsuarioIdAndStatusAndDenunciaId(usuarioIdLong, 1L, ocorrencia.getId());

        // Verifica se o responsável foi encontrado e está com status 1
        if (responsavel != null) {
            return true; // O usuário é o responsável
        } else {
            return false; // O usuário não é o responsável
        }
    }

    @Transactional
    public void assumirResponsavel(Long id, String usuarioId) {
        // Verifica se o usuário já é responsável pela ocorrência
        if (verificarResponsavel(id, usuarioId)) {
            throw new RuntimeException("Este usuário já é responsável por esta ocorrência.");
        }

        // Recupera a ocorrência
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));
        User user = userRepository.findById(Long.valueOf(usuarioId))
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        // Desativa qualquer outro responsável anterior no histórico
        responsavelRepository.updateStatusPorOcorrenciaEUsuario(ocorrencia.getId(), user.getId(), Long.valueOf(0));

        // Recupera o responsável pela ID do usuário
        Responsavel responsavel = new Responsavel();
        responsavel.setUsuario(user);
        responsavel.setStatus(Long.valueOf(1)); // Marca como responsável ativo
        responsavel.setDenuncia(ocorrencia); // Associa a ocorrência ao responsável
        responsavel.setDelegacia(user.getDelegate());
        responsavel.setNumDistintivo(user.getBadge());
        responsavel.setDataCriacao(LocalDateTime.now()); // Data atual

        // Salva o novo responsável no histórico (ou atualiza, se necessário)
        responsavelRepository.save(responsavel);
    }
    @Transactional
    public void desassumirResponsavel(Long id, String usuarioId) {
        // Verifica se o usuário é o responsável pela ocorrência
        if (!verificarResponsavel(id, usuarioId)) {
            throw new RuntimeException("Este usuário não é o responsável por esta ocorrência.");
        }

        // Recupera a ocorrência pelo ID
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        // Busca o responsável ativo (com status = 1) para a ocorrência
        Responsavel responsavel = responsavelRepository.findByUsuarioIdAndStatusAndDenunciaId(Long.valueOf(usuarioId), 1L, ocorrencia.getId());

        if (responsavel == null) {
            throw new RuntimeException("Responsável não encontrado com status ativo.");
        }

        // Desativa o responsável atual no histórico (Status = 0)
        responsavel.setStatus(0L); // Marca como não ativo
        responsavel.setDataCriacao(LocalDateTime.now()); // Atualiza a data de criação (pode ser necessário se quiser gravar a alteração)

        // Salva a alteração no responsável
        responsavelRepository.save(responsavel);

        // Remove o responsável da ocorrência
        ocorrencia.setIdResponsavel(null);
        repository.save(ocorrencia); // Salva a ocorrência com o responsável removido
    }





}

