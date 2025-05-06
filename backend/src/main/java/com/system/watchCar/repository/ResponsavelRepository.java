package com.system.watchCar.repository;

import com.system.watchCar.entity.Responsavel;
import com.system.watchCar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

}

