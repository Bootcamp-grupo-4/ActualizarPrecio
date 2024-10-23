package com.capgeticket.actualizarprecios.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "venta_entrada")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaEntradaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento", nullable = false)
    private EventoEntity evento;

    @Column(name = "nombreTitular", nullable = false, length = 255)
    private String nombreTitular;

    @Column(name = "numeroTarjeta", nullable = false, length = 255)
    private String numeroTarjeta;

    @Column(name = "mesCaducidad", nullable = false)
    private Integer mesCaducidad;

    @Column(name = "yearCaducidad", nullable = false)
    private Integer yearCaducidad;

    @Column(name = "concepto", nullable = false, length = 255)
    private String concepto;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "fechaCompra", nullable = false)
    private LocalDate fechaCompra;
}
