package com.system.watchCar.repository;

import com.system.watchCar.entity.TipoVeiculo;
import com.system.watchCar.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

}
