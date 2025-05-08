package com.system.watchCar.repository;

import com.system.watchCar.entity.Ocorrencia;
import com.system.watchCar.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long>, JpaSpecificationExecutor<Ocorrencia> {

    Page<Ocorrencia> findByStatusDenunciaContainingIgnoreCaseAndCodArtigoContainingIgnoreCaseAndHoraOcorrenciaContainingIgnoreCase(
            String statusDenuncia, String codArtigo, String horaOcorrencia, Pageable pageable);

    @Query(value = """
    SELECT *
    FROM tb_denuncia d
    WHERE (:status IS NULL OR d.status_denuncia = :status)
      AND (:artigo IS NULL OR d.cod_artigo = :artigo)
      AND (:hora IS NULL OR d.hora_ocorrencia = :hora)
      AND (:dataInicio IS NULL OR d.data_hora >= CAST(:dataInicio AS TIMESTAMP))
      AND (:dataFim IS NULL OR d.data_hora <= CAST(:dataFim AS TIMESTAMP))
""",
            nativeQuery = true)
    Page<Ocorrencia> findByFilters(
            @Param("status") String status,
            @Param("artigo") String artigo,
            @Param("hora") String hora, // ou LocalTime, dependendo da sua entidade
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );





}


