package com.system.watchCar.repository;

import com.system.watchCar.entity.Artigo;
import com.system.watchCar.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

}
