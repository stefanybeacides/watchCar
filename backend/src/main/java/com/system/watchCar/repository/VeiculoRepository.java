package com.system.watchCar.repository;

import com.system.watchCar.entity.TipoVeiculo;
import com.system.watchCar.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

}
