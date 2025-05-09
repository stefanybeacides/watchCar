package com.system.watchCar.repository;

import com.system.watchCar.entity.Artigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtigoRepository extends JpaRepository<Artigo, Long> {

    // Busca se rubrica já está como código ou descrição
    @Query("SELECT a FROM Artigo a WHERE LOWER(a.codArtigo) LIKE LOWER(CONCAT('%', :rubrica, '%')) " +
            "OR LOWER(a.descricao) LIKE LOWER(CONCAT('%', :rubrica, '%'))")
    List<Artigo> findByRubricaInCodigoOuDescricao(@Param("rubrica") String rubrica);

}
