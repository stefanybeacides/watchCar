package com.system.watchCar.service;

import com.system.watchCar.dto.*;
import com.system.watchCar.entity.*;
import com.system.watchCar.enums.TipoTemplateEmail;
import com.system.watchCar.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final UserRepository userRepository;
    private final TipoVeiculoRepository tipoVeiculoRepository;
    private final VeiculoRepository veiculoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final ArtigoRepository artigoRepository;
    private final AcaoInvestigacaoRepository acaoInvestigacaoRepository;
    private final EmailService emailService;
    private final LocalRepository localRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public OcorrenciaService(OcorrenciaRepository repository, UserRepository userRepository, TipoVeiculoRepository tipoVeiculoRepository, VeiculoRepository veiculoRepository, ResponsavelRepository responsavelRepository, ArtigoRepository artigoRepository, AcaoInvestigacaoRepository acaoInvestigacaoRepository, EmailService emailService, LocalRepository localRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.tipoVeiculoRepository = tipoVeiculoRepository;
        this.veiculoRepository = veiculoRepository;
        this.responsavelRepository = responsavelRepository;
        this.artigoRepository = artigoRepository;
        this.acaoInvestigacaoRepository = acaoInvestigacaoRepository;
        this.emailService = emailService;
        this.localRepository = localRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Page<OcorrenciaDTO> obterOcorrenciasComDetalhes(
            User user, String status, String artigo, String hora,
            String usuarioNome, String usuarioEmail, String veiculoMarca, String veiculoModelo, String veiculoPlaca,
            LocalDateTime dataInicio, LocalDateTime dataFim, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Long idUsuario = user.getRole().getName() == RoleType.PUBLICO ? user.getId() : null;

        Page<Ocorrencia> ocorrencias = findByFilters(
                status, artigo, hora, usuarioNome, usuarioEmail, veiculoMarca, veiculoModelo, veiculoPlaca,
                dataInicio, dataFim, idUsuario, pageRequest
        );

        List<OcorrenciaDTO> ocorrenciasComDetalhes = ocorrencias.getContent().stream()
                .map(ocorrencia -> {
                    User usuario = userRepository.findById(ocorrencia.getIdUsuario()).orElse(null);
                    Veiculo veiculo = veiculoRepository.findById(ocorrencia.getIdVeiculo()).orElse(null);
                    Artigo artigoCriminal = artigoRepository.findById(Long.valueOf(ocorrencia.getCodArtigo())).orElse(null);
                    Local local = localRepository.findById(ocorrencia.getIdLocal().getId()).orElse(null);

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

                    if (local != null) {
                        dto.setLogradouro(local.getLogradouro());
                        dto.setBairro(local.getBairro());
                        dto.setCidade(local.getCidade());
                        dto.setEstado(local.getEstado());
                        dto.setCep(local.getCep());
                    }

                    return dto;
                }).collect(Collectors.toList());

        return new PageImpl<>(ocorrenciasComDetalhes, pageRequest, ocorrencias.getTotalElements());
    }




    public Page<Ocorrencia> findByFilters(
            String status, String artigo, String hora,
            String usuarioNome, String usuarioEmail, String veiculoMarca, String veiculoModelo, String veiculoPlaca,
            LocalDateTime dataInicio, LocalDateTime dataFim,
            Long user, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ocorrencia> query = cb.createQuery(Ocorrencia.class);
        Root<Ocorrencia> root = query.from(Ocorrencia.class);

        List<Predicate> predicates = new ArrayList<>();

        if (!status.isEmpty()) {
            predicates.add(cb.equal(root.get("statusDenuncia"), status));
        }
        if (!artigo.isEmpty()) {
            predicates.add(cb.equal(root.get("codArtigo"), artigo));
        }
        if (!hora.isEmpty()) {
            predicates.add(cb.equal(root.get("horaOcorrencia"), hora));
        }
        if (user != null) {
            predicates.add(cb.equal(root.get("idUsuario"), user));
        }
        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dataHora"), dataInicio));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dataHora"), dataFim));
        }

        // Subqueries para buscar por nome, email, marca, modelo, placa
        if (!usuarioNome.isEmpty()) {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<User> subRoot = sub.from(User.class);
            sub.select(subRoot.get("id")).where(cb.like(cb.lower(subRoot.get("username")), "%" + usuarioNome.toLowerCase() + "%"));
            predicates.add(root.get("idUsuario").in(sub));
        }

        if (!usuarioEmail.isEmpty()) {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<User> subRoot = sub.from(User.class);
            sub.select(subRoot.get("id")).where(cb.like(cb.lower(subRoot.get("email")), "%" + usuarioEmail.toLowerCase() + "%"));
            predicates.add(root.get("idUsuario").in(sub));
        }

        if (!veiculoPlaca.isEmpty() || !veiculoMarca.isEmpty() || !veiculoModelo.isEmpty()) {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Veiculo> subRoot = sub.from(Veiculo.class);
            Join<Object, Object> tipoVeiculoJoin = subRoot.join("tipoVeiculo");

            List<Predicate> subPredicates = new ArrayList<>();
            if (!veiculoPlaca.isEmpty()) {
                subPredicates.add(cb.like(cb.lower(subRoot.get("placa")), "%" + veiculoPlaca.toLowerCase() + "%"));
            }
            if (!veiculoMarca.isEmpty()) {
                subPredicates.add(cb.like(cb.lower(tipoVeiculoJoin.get("marca")), "%" + veiculoMarca.toLowerCase() + "%"));
            }
            if (!veiculoModelo.isEmpty()) {
                subPredicates.add(cb.like(cb.lower(tipoVeiculoJoin.get("modelo")), "%" + veiculoModelo.toLowerCase() + "%"));
            }

            sub.select(subRoot.get("id")).where(cb.and(subPredicates.toArray(new Predicate[0])));
            predicates.add(root.get("idVeiculo").in(sub));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Ocorrencia> typedQuery = entityManager.createQuery(query);
        long total = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Ocorrencia> resultList = typedQuery.getResultList();
        return new PageImpl<>(resultList, pageable, total);
    }



    @Transactional
    public Ocorrencia criarDenuncia(DenunciaRequest request) {
        User usuario;
        if(request.getIdUsuario() != null){
            usuario = userRepository.findById(request.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        }else{
            usuario = new User();
            String encodedPassword = passwordEncoder.encode("123");
            RoleType roleType;
            roleType = RoleType.PUBLICO;
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new RuntimeException("Papel não encontrado: " + roleType));
            usuario.setUsername(request.getUsername());
            usuario.setCpf(request.getCpf());
            usuario.setEmail(request.getEmail());
            usuario.setRole(role);
            usuario.setPassword(encodedPassword);
            usuario.setAtivo(false);
            usuario = userRepository.save(usuario);
        }


        
        if (!isValidStatus(request.getStatusDenuncia())) {
            throw new RuntimeException("Status da denúncia inválido");
        }

        
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

        
        Veiculo veiculo = new Veiculo();
        veiculo.setTipoVeiculo(tipo);
        veiculo.setAno(request.getAno());
        veiculo.setPlaca(request.getPlaca());
        veiculo = veiculoRepository.save(veiculo);

        Local local = new Local();
        local.setCep(request.getCep());
        local.setCidade(request.getCidade());
        local.setBairro(request.getBairro());
        local.setEstado(request.getEstado());
        local.setLogradouro(request.getLogradouro());
        local = localRepository.save(local);
        
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setIdUsuario(usuario.getId());
        ocorrencia.setDescricaoOcorrencia(request.getDescricao());
        ocorrencia.setStatusDenuncia(request.getStatusDenuncia());
        ocorrencia.setHoraOcorrencia(request.getHoraOcorrencia());
        ocorrencia.setDataHora(request.getDataHora() != null ? request.getDataHora() : LocalDateTime.now()); 
        ocorrencia.setIdVeiculo(veiculo.getId());
        ocorrencia.setCodArtigo(request.getArtigoLei());
        ocorrencia.setAlerta(Boolean.TRUE.equals(request.getReceberAlertas()) ? 1L : 0L);
        ocorrencia.setIdLocal(local);
        ocorrencia = repository.save(ocorrencia);

        Responsavel responsavel = new Responsavel();
        responsavel.setDenuncia(ocorrencia);
        responsavel.setUsuario(usuario);
        responsavel.setNumDistintivo(usuario.getBadge());
        responsavel.setDelegacia(usuario.getDelegate());
        responsavel.setDataCriacao(LocalDateTime.now());
        responsavelRepository.save(responsavel);

        
        if (usuario.getBadge() != null && usuario.getDelegate() != null) {
            Responsavel resp = new Responsavel();
            resp.setDenuncia(ocorrencia);
            resp.setUsuario(usuario);
            resp.setNumDistintivo(usuario.getBadge());
            resp.setDelegacia(usuario.getDelegate());
            responsavelRepository.save(resp);
        }

        if(request.getReceberAlertas()){

        // Preparar os dados para o template
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("ocorrencia", ocorrencia);
        templateVariables.put("denunciante", usuario);

        // Enviar o e-mail com o template
        emailService.enviarEmailComTemplate(
                usuario.getEmail(),
                TipoTemplateEmail.NOVA_DENUNCIA,
                templateVariables
        );
        }
        return ocorrencia;
    }

    
    private boolean isValidStatus(String status) {
        
        return "Em andamento".equals(status) || "Solucionado".equals(status) || "Arquivado".equals(status);
    }

    public OcorrenciaDetalhadaResponse buscarDetalhesPorId(Long id) {
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        User usuario = userRepository.findById(ocorrencia.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Veiculo veiculo = veiculoRepository.findById(ocorrencia.getIdVeiculo())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        Optional<Responsavel> responsavelAtual = responsavelRepository.findTopByDenunciaIdOrderByDataCriacaoDesc(id);

        List<Responsavel> historico = responsavelRepository.findByDenunciaIdOrderByDataCriacaoDesc(id);
        List<OcorrenciaDetalhadaResponse.ResponsavelHistoricoDto> historicoDtos = historico.stream().map(r ->
                new OcorrenciaDetalhadaResponse.ResponsavelHistoricoDto(r.getUsuario().getUsername(), r.getDataCriacao(), r.getNumDistintivo(), r.getDelegacia())
        ).collect(Collectors.toList());

        // Buscar ações de investigação relacionadas à denúncia
        List<AcaoInvestigacao> acoes = acaoInvestigacaoRepository.findByDenuncia_Id(id);
        List<OcorrenciaDetalhadaResponse.AcaoInvestigacaoDto> acaoDtos = acoes.stream().map(a -> {
            // Buscar os dados do responsável (nome, distintivo, delegacia)
            String nomeResponsavel = a.getUser() != null ? a.getUser().getUsername() : null;
            String distintivoResponsavel = a.getUser() != null ? a.getUser().getBadge() : null;
            String delegaciaResponsavel = a.getUser() != null ? a.getUser().getDelegate() : null;

            assert a.getUser() != null;
            return new OcorrenciaDetalhadaResponse.AcaoInvestigacaoDto(
                    a.getId(),
                    a.getTipoAcao(),
                    a.getDescricaoAcao(),
                    a.getDataAcao(),
                    a.getUser().getId(),
                    nomeResponsavel,
                    distintivoResponsavel,
                    delegaciaResponsavel
            );
        }).collect(Collectors.toList());

        OcorrenciaDetalhadaResponse response = new OcorrenciaDetalhadaResponse();
        response.setId(ocorrencia.getId());
        response.setUsuarioNome(usuario.getUsername());
        response.setVeiculoPlaca(veiculo.getPlaca());
        response.setVeiculoModelo(veiculo.getTipoVeiculo().getModelo());
        response.setStatusDenuncia(ocorrencia.getStatusDenuncia());
        response.setResponsavelId(responsavelAtual.map(r -> r.getUsuario().getId()).orElse(null));
        response.setHistoricoResponsaveis(historicoDtos);
        response.setAcoesInvestigacao(acaoDtos);

        return response;
    }


    public boolean verificarResponsavel(Long id, String usuarioId) {
        
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        
        Long usuarioIdLong = Long.valueOf(usuarioId);

        
        Responsavel responsavel = responsavelRepository.findByUsuarioIdAndStatusAndDenunciaId(usuarioIdLong, 1L, ocorrencia.getId());

        
        if (responsavel != null) {
            return true; 
        } else {
            return false; 
        }
    }

    @Transactional
    public void assumirResponsavel(Long id, String usuarioId) {
        
        if (verificarResponsavel(id, usuarioId)) {
            throw new RuntimeException("Este usuário já é responsável por esta ocorrência.");
        }

        
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));
        User user = userRepository.findById(Long.valueOf(usuarioId))
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        
        responsavelRepository.updateStatusPorOcorrenciaEUsuario(ocorrencia.getId(), user.getId(), Long.valueOf(0));

        
        Responsavel responsavel = new Responsavel();
        responsavel.setUsuario(user);
        responsavel.setStatus(Long.valueOf(1)); 
        responsavel.setDenuncia(ocorrencia); 
        responsavel.setDelegacia(user.getDelegate());
        responsavel.setNumDistintivo(user.getBadge());
        responsavel.setDataCriacao(LocalDateTime.now());
        responsavel = responsavelRepository.save(responsavel);

        ocorrencia.setIdResponsavel(responsavel.getId());
        repository.save(ocorrencia);
    }
    @Transactional
    public void desassumirResponsavel(Long id, String usuarioId) {
        
        if (!verificarResponsavel(id, usuarioId)) {
            throw new RuntimeException("Este usuário não é o responsável por esta ocorrência.");
        }

        
        Ocorrencia ocorrencia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        
        Responsavel responsavel = responsavelRepository.findByUsuarioIdAndStatusAndDenunciaId(Long.valueOf(usuarioId), 1L, ocorrencia.getId());

        if (responsavel == null) {
            throw new RuntimeException("Responsável não encontrado com status ativo.");
        }

        
        responsavel.setStatus(0L); 
        responsavel.setDataCriacao(LocalDateTime.now()); 

        
        responsavelRepository.save(responsavel);

        
        ocorrencia.setIdResponsavel(null);
        repository.save(ocorrencia); 
    }


    public List<AcaoInvestigacaoDetalhadaResponse> listarComDetalhes(Long idResponsavel, Long idDenuncia) {
        List<AcaoInvestigacao> entidades = acaoInvestigacaoRepository.findByUserAndDenuncia(idResponsavel, idDenuncia);
        User usuario = userRepository.findById(idResponsavel).orElse(null);

        return entidades.stream().map(acao -> {
            AcaoInvestigacaoDetalhadaResponse dto = new AcaoInvestigacaoDetalhadaResponse();
            dto.setId(acao.getId());
            dto.setTipoAcao(acao.getTipoAcao());
            dto.setDescricaoAcao(acao.getDescricaoAcao());
            dto.setDataAcao(acao.getDataAcao());

            dto.setResponsavel(usuario);
            dto.setDenuncia(acao.getDenuncia());

            return dto;
        }).collect(Collectors.toList());
    }

    public void salvar(AcaoInvestigacaoRequest request) {
        AcaoInvestigacao acao = new AcaoInvestigacao();

        // Verifica se a denúncia existe de forma segura
        Ocorrencia denuncia = repository.findById(request.getIdDenuncia())
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));

        // Verifica se o usuário realmente existe
        User usuario = userRepository.findById(request.getIdResponsavel())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + request.getIdResponsavel()));

        if(request.getTipoAcao().equals("Solucionado") || request.getTipoAcao().equals("Arquivado")){
            acao.setTipoAcao(request.getTipoAcao());
            denuncia.setStatusDenuncia(request.getTipoAcao());
            repository.save(denuncia);
        }else{
            acao.setTipoAcao(request.getTipoAcao());
        }

        acao.setUser(usuario);
        acao.setDenuncia(denuncia);
        acao.setTipoAcao(request.getTipoAcao());
        acao.setDescricaoAcao(request.getDescricaoAcao());
        acao.setDataAcao(request.getDataAcao());

        acaoInvestigacaoRepository.save(acao);
        if(denuncia.getAlerta() > 0){
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("ocorrencia", denuncia);
        templateVariables.put("denunciante", usuario);

        // Enviar o e-mail com o template
        emailService.enviarEmailComTemplate(
                usuario.getEmail(),
                TipoTemplateEmail.ACAO_INVESTIGACAO,
                templateVariables
        );
        }
    }

    public Map<String, Long> contarOcorrenciasPorStatus() {
        List<Object[]> resultados = repository.countGroupByStatus();

        Map<String, Long> contagem = new HashMap<>();
        long total = 0;

        for (Object[] resultado : resultados) {
            String status = (String) resultado[0];
            Long quantidade = (Long) resultado[1];

            contagem.put(status, quantidade);
            total += quantidade;
        }

        contagem.put("Todos", total);

        return contagem;
    }
}

