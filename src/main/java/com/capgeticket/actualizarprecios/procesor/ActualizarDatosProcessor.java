package com.capgeticket.actualizarprecios.procesor;

import com.capgeticket.actualizarprecios.dto.EventoDto;
import com.capgeticket.actualizarprecios.model.EventoEntity;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

public class ActualizarDatosProcessor implements ItemProcessor<EventoEntity, EventoDto> {

    @Override
    public EventoDto process(EventoEntity eventoEntity) {
        int numeroDeVentas = eventoEntity.getVentas() != null ? eventoEntity.getVentas().size() : 0;
        BigDecimal precioActual = eventoEntity.getPrecio();

        BigDecimal nuevoPrecio = calcularNuevoPrecio(precioActual, numeroDeVentas,
                eventoEntity.getPrecioMinimo(), eventoEntity.getPrecioMaximo());

        return EventoDto.builder()
                .id(eventoEntity.getId())
                .nombre(eventoEntity.getNombre())
                .descripcion(eventoEntity.getDescripcion())
                .fechaEvento(eventoEntity.getFechaEvento())
                .precioMinimo(eventoEntity.getPrecioMinimo())
                .precioMaximo(eventoEntity.getPrecioMaximo())
                .localidad(eventoEntity.getLocalidad())
                .nombreDelRecinto(eventoEntity.getNombreDelRecinto())
                .genero(eventoEntity.getGenero())
                .mostrar(eventoEntity.getMostrar())
                .precio(nuevoPrecio)
                .build();
    }

    private BigDecimal calcularNuevoPrecio(BigDecimal precioActual, int numeroDeVentas,
                                           BigDecimal precioMinimo, BigDecimal precioMaximo) {

        if (numeroDeVentas < 10) {
            return precioMinimo;
        }
        int incrementoPorCadaDiezVentas = numeroDeVentas / 10;
        BigDecimal incremento = BigDecimal.valueOf(incrementoPorCadaDiezVentas * 5); // 5 euros por cada 10 ventas

        BigDecimal nuevoPrecio = precioActual.add(incremento);

        if (nuevoPrecio.compareTo(precioMaximo) > 0) {
            return precioMaximo;
        }

        return nuevoPrecio;
    }


}
