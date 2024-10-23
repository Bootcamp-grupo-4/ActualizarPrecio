package com.capgeticket.actualizarprecios.config.batch;

import com.capgeticket.actualizarprecios.dto.EventoDto;
import com.capgeticket.actualizarprecios.listener.ActualizarPreciosJobExecutionListener;
import com.capgeticket.actualizarprecios.mapper.EventoWithVentasExtractor;
import com.capgeticket.actualizarprecios.model.EventoEntity;
import com.capgeticket.actualizarprecios.procesor.ActualizarDatosProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfigurationActualizarPrecio {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Método para leer los eventos y sus ventas desde la base de datos.
     * @param dataSource DataSource que se utiliza para ejecutar la consulta.
     * @return ItemReader que lee los eventos con las ventas asociadas.
     */
    @Bean
    public ItemReader<EventoEntity> reader(DataSource dataSource) {
        log.info("Ejecutando el método reader para obtener eventos y sus ventas.");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String query = "SELECT e.id, e.nombre, e.descripcion, e.fechaEvento, e.precioMinimo, e.precioMaximo, e.localidad, " +
                "e.nombreDelRecinto, e.genero, e.mostrar, e.precio, " +
                "ve.id AS venta_id, ve.nombreTitular, ve.numeroTarjeta, ve.mesCaducidad, ve.yearCaducidad, ve.concepto, " +
                "ve.cantidad, ve.fechaCompra " +
                "FROM evento e " +
                "LEFT JOIN VentaEntrada ve ON e.id = ve.id_evento";

        List<EventoEntity> eventos = jdbcTemplate.query(query, new EventoWithVentasExtractor());
        log.info("Se han encontrado {} eventos con sus respectivas ventas.", eventos.size());

        return new ListItemReader<>(eventos);
    }

    /**
     * Método que devuelve el procesador para actualizar los datos de los eventos.
     * @return ActualizarDatosProcessor encargado de transformar los eventos.
     */
    @Bean
    public ActualizarDatosProcessor processor() {
        log.info("Creando instancia de ActualizarDatosProcessor.");
        return new ActualizarDatosProcessor();
    }

    /**
     * Método que configura el writer encargado de actualizar los precios de los eventos en la base de datos.
     * @param dataSource DataSource utilizado para conectarse a la base de datos.
     * @return ItemWriter para escribir los precios actualizados en la tabla evento.
     */
    @Bean
    public ItemWriter<EventoDto> writer(DataSource dataSource) {
        log.info("Configurando el writer para actualizar los precios de los eventos.");
        return new JdbcBatchItemWriterBuilder<EventoDto>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE evento SET precio = :precio WHERE id = :id")
                .dataSource(dataSource)
                .build();
    }

    /**
     * Método para definir el Job de actualización de precios de eventos.
     * @param listener Listener para monitorear el estado del Job.
     * @param step1 Step del Job que contiene la lógica de lectura, procesamiento y escritura.
     * @return Job configurado para actualizar los precios de los eventos.
     */
    @Bean
    public Job actualizarPreciosJob(ActualizarPreciosJobExecutionListener listener, Step step1) {
        log.info("Configurando el Job para actualizar los precios de los eventos.");
        return jobBuilderFactory
                .get("actualizarPreciosJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    /**
     * Método que define el Step del Job. Incluye la lógica de lectura de eventos,
     * procesamiento de los datos y actualización de precios.
     * @param reader Lector de eventos y ventas.
     * @param writer Writer para actualizar los precios en la base de datos.
     * @param processor Procesador encargado de realizar la transformación de los datos.
     * @return Step configurado con la lógica de negocio para actualizar los precios.
     */
    @Bean
    public Step step1(ItemReader<EventoEntity> reader, ItemWriter<EventoDto> writer,
                      ActualizarDatosProcessor processor) {
        log.info("Configurando el Step para leer, procesar y actualizar precios de eventos.");
        return stepBuilderFactory
                .get("step1")
                .<EventoEntity, EventoDto>chunk(2)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
