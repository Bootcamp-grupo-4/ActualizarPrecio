package com.capgeticket.actualizarprecios.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "evento")
public class EventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "fechaevento", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "preciominimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMinimo;

    @Column(name = "preciomaximo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMaximo;

    @Column(name = "localidad", nullable = false, length = 255)
    private String localidad;

    @Column(name = "nombredelrecinto", nullable = false, length = 255)
    private String nombreDelRecinto;

    @Column(name = "genero", nullable = false, length = 255)
    private String genero;

    @Column(name = "mostrar", nullable = false)
    private Boolean mostrar;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @OneToMany(mappedBy = "evento")
    private List<VentaEntradaEntity> ventas;
}
