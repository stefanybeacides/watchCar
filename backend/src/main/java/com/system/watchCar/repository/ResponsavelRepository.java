package com.system.watchCar.repository;

import com.system.watchCar.entity.Responsavel;
import com.system.watchCar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    Optional<Responsavel> findTopByDenunciaIdOrderByDataCriacaoDesc(Long id);
    List<Responsavel> findByDenunciaIdOrderByDataCriacaoDesc(Long id);

    Responsavel findByUsuarioId(Long usuarioId);  // Consulta pelo ID_USER da entidade Usuario
    @Modifying
    @Transactional
    @Query("UPDATE Responsavel r SET r.status = :status WHERE r.denuncia.id = :ocorrenciaId AND r.usuario.id = :usuarioId")
    void updateStatusPorOcorrenciaEUsuario(@Param("ocorrenciaId") Long ocorrenciaId, @Param("usuarioId") Long usuarioId, @Param("status") Long status);


    @Query("SELECT r FROM Responsavel r WHERE r.usuario.id = :usuarioId AND r.status = 1")
    Responsavel findByUsuarioIdAndStatus(@Param("usuarioId") Long usuarioId);


    Responsavel findByUsuarioIdAndStatusAndDenunciaId(Long usuarioIdLong, long l, Long id);
}

