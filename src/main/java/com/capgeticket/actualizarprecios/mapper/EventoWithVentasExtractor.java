package com.capgeticket.actualizarprecios.mapper;

import com.capgeticket.actualizarprecios.model.EventoEntity;
import com.capgeticket.actualizarprecios.model.VentaEntradaEntity;
import org.springframework.jdbc.core.ResultSetExtractor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventoWithVentasExtractor implements ResultSetExtractor<List<EventoEntity>> {

    @Override
    public List<EventoEntity> extractData(ResultSet rs) throws SQLException {
        Map<Long, EventoEntity> eventoMap = new HashMap<>();

        while (rs.next()) {
            Long eventoId = rs.getLong("id");

            EventoEntity evento = eventoMap.get(eventoId);

            if (evento == null) {
                evento = new EventoEntity();
                evento.setId(eventoId);
                evento.setNombre(rs.getString("nombre"));
                evento.setDescripcion(rs.getString("descripcion"));
                evento.setFechaEvento(rs.getDate("fechaEvento").toLocalDate());
                evento.setPrecioMinimo(rs.getBigDecimal("precioMinimo"));
                evento.setPrecioMaximo(rs.getBigDecimal("precioMaximo"));
                evento.setLocalidad(rs.getString("localidad"));
                evento.setNombreDelRecinto(rs.getString("nombreDelRecinto"));
                evento.setGenero(rs.getString("genero"));
                evento.setMostrar(rs.getBoolean("mostrar"));
                evento.setPrecio(rs.getBigDecimal("precio"));

                evento.setVentas(new ArrayList<>());
                eventoMap.put(eventoId, evento);
            }

            Long ventaId = rs.getLong("venta_id");
            if (ventaId != null && ventaId != 0) {
                VentaEntradaEntity venta = new VentaEntradaEntity();
                venta.setId(ventaId);
                venta.setNombreTitular(rs.getString("nombreTitular"));
                venta.setNumeroTarjeta(rs.getString("numeroTarjeta"));
                venta.setMesCaducidad(rs.getInt("mesCaducidad"));
                venta.setYearCaducidad(rs.getInt("yearCaducidad"));
                venta.setConcepto(rs.getString("concepto"));
                venta.setCantidad(rs.getBigDecimal("cantidad"));
                venta.setFechaCompra(rs.getDate("fechaCompra").toLocalDate());

                evento.getVentas().add(venta);
            }
        }

        return new ArrayList<>(eventoMap.values());
    }
}
