package com.system.watchCar.repository;

import com.system.watchCar.entity.TipoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoVeiculoRepository extends JpaRepository<TipoVeiculo, Long> {

    Optional<TipoVeiculo> findByTipoAndModeloAndMarcaAndCor(String tipo, String modelo, String marca, String cor);
}
