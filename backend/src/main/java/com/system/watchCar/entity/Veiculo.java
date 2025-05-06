package com.system.watchCar.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_VEICULO")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO_VEICULO")
    private TipoVeiculo tipoVeiculo;

    private String placa;
    private Integer ano;
}

