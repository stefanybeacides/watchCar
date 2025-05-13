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
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long>, JpaSpecificationExecutor<Ocorrencia> {

    Page<Ocorrencia> findByStatusDenunciaContainingIgnoreCaseAndCodArtigoContainingIgnoreCaseAndHoraOcorrenciaContainingIgnoreCase(
            String statusDenuncia, String codArtigo, String horaOcorrencia, Pageable pageable);

    @Query("""
    SELECT o FROM Ocorrencia o
    WHERE 
        (:status IS NULL OR o.statusDenuncia = :status)
        AND (:artigo IS NULL OR o.codArtigo = :artigo)
        AND (:hora IS NULL OR o.horaOcorrencia = :hora)
        AND (:dataInicio IS NULL OR o.dataHora >= :dataInicio)
        AND (:dataFim IS NULL OR o.dataHora <= :dataFim)
""")
    Page<Ocorrencia> findByFilters(
            @Param("status") String status,
            @Param("artigo") String artigo,
            @Param("hora") String hora,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );


    @Query("SELECT o.statusDenuncia, COUNT(o) FROM Ocorrencia o GROUP BY o.statusDenuncia")
    List<Object[]> countGroupByStatus();
}


