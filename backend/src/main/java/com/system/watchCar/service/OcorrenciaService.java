package com.system.watchCar.service;

import javax.persistence.criteria.Predicate;

import com.system.watchCar.dto.DenunciaRequest;
import com.system.watchCar.dto.OcorrenciaDTO;
import com.system.watchCar.entity.*;
import com.system.watchCar.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final UserRepository userRepository;
    private final TipoVeiculoRepository tipoVeiculoRepository;
    private final VeiculoRepository veiculoRepository;
    private final ResponsavelRepository responsavelRepository;

    public OcorrenciaService(OcorrenciaRepository repository, UserRepository userRepository, TipoVeiculoRepository tipoVeiculoRepository, VeiculoRepository veiculoRepository, ResponsavelRepository responsavelRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.tipoVeiculoRepository = tipoVeiculoRepository;
        this.veiculoRepository = veiculoRepository;
        this.responsavelRepository = responsavelRepository;
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

                    // Mapear a ocorrência para o DTO com dados do usuário e veículo
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

        ocorrencia = repository.save(ocorrencia);

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


}

