package com.system.watchCar.repository;

import com.system.watchCar.entity.AcaoInvestigacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcaoInvestigacaoRepository extends JpaRepository<AcaoInvestigacao, Long> {

    @Query("SELECT a FROM AcaoInvestigacao a " +
            "JOIN FETCH a.user u " +
            "JOIN FETCH a.denuncia d " +
            "WHERE u.id = :idUsuario AND d.id = :idDenuncia")
    List<AcaoInvestigacao> findByUserAndDenuncia(@Param("idUsuario") Long idUsuario,
                                                 @Param("idDenuncia") Long idDenuncia);


    List<AcaoInvestigacao> findByDenuncia_Id(Long idDenuncia);

}


