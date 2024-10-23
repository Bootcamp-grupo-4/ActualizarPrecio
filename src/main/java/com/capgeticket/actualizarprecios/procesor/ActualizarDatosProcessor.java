package com.capgeticket.actualizarprecios.procesor;


import com.capgeticket.actualizarprecios.dto.EventoDto;
import com.capgeticket.actualizarprecios.model.EventoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

@Slf4j
public class ActualizarDatosProcessor implements ItemProcessor<EventoEntity, EventoDto> {

    /**
     * Procesa un EventoEntity para calcular un nuevo precio basado en el número de ventas y
     * retorna un EventoDto con los datos actualizados.
     *
     * @param eventoEntity Evento a procesar.
     * @return EventoDto con el nuevo precio calculado.
     */
    @Override
    public EventoDto process(EventoEntity eventoEntity) {
        int numeroDeVentas = eventoEntity.getVentas() != null ? eventoEntity.getVentas().size() : 0;
        BigDecimal precioActual = eventoEntity.getPrecio();

        log.info("Procesando evento con ID {}. Número de ventas: {}. Precio actual: {}",
                eventoEntity.getId(), numeroDeVentas, precioActual);

        BigDecimal nuevoPrecio = calcularNuevoPrecio(precioActual, numeroDeVentas,
                eventoEntity.getPrecioMinimo(), eventoEntity.getPrecioMaximo());

        log.info("Nuevo precio calculado para el evento con ID {}: {}", eventoEntity.getId(), nuevoPrecio);

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

    /**
     * Calcula el nuevo precio en función del número de ventas. Si el número de ventas es menor
     * a 10, el precio mínimo se aplica. Se incrementa el precio en 5 euros por cada 10 ventas,
     * pero nunca supera el precio máximo.
     *
     * @param precioActual El precio actual del evento.
     * @param numeroDeVentas Número de ventas realizadas.
     * @param precioMinimo Precio mínimo permitido.
     * @param precioMaximo Precio máximo permitido.
     * @return El nuevo precio calculado.
     */
    private BigDecimal calcularNuevoPrecio(BigDecimal precioActual, int numeroDeVentas,
                                           BigDecimal precioMinimo, BigDecimal precioMaximo) {

        log.info("Calculando nuevo precio. Precio actual: {}, Ventas: {}, Precio mínimo: {}, Precio máximo: {}",
                precioActual, numeroDeVentas, precioMinimo, precioMaximo);

        if (numeroDeVentas < 10) {
            log.info("Número de ventas inferior a 10, asignando el precio mínimo: {}", precioMinimo);
            return precioMinimo;
        }

        int incrementoPorCadaDiezVentas = numeroDeVentas / 10;
        BigDecimal incremento = BigDecimal.valueOf(incrementoPorCadaDiezVentas * 5); // 5 euros por cada 10 ventas

        BigDecimal nuevoPrecio = precioActual.add(incremento);

        if (nuevoPrecio.compareTo(precioMaximo) > 0) {
            log.info("El nuevo precio supera el precio máximo, ajustando al precio máximo: {}", precioMaximo);
            return precioMaximo;
        }

        log.info("El nuevo precio es: {}", nuevoPrecio);
        return nuevoPrecio;
    }
}
