package com.capgeticket.actualizarprecios.config.batch;

import com.capgeticket.actualizarprecios.dto.EventoDto;
import com.capgeticket.actualizarprecios.listener.ActualizarPreciosJobExecutionListener;
import com.capgeticket.actualizarprecios.mapper.EventoWithVentasExtractor;
import com.capgeticket.actualizarprecios.model.EventoEntity;
import com.capgeticket.actualizarprecios.procesor.ActualizarDatosProcessor;
import lombok.RequiredArgsConstructor;
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


@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfigurationActualizarPrecio {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReader<EventoEntity> reader(DataSource dataSource) {
        // Cargar todos los resultados en memoria
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String query = "SELECT e.id, e.nombre, e.descripcion, e.fechaEvento, e.precioMinimo, e.precioMaximo, e.localidad, " +
                "e.nombreDelRecinto, e.genero, e.mostrar, e.precio, " +
                "ve.id AS venta_id, ve.nombreTitular, ve.numeroTarjeta, ve.mesCaducidad, ve.yearCaducidad, ve.concepto, " +
                "ve.cantidad, ve.fechaCompra " +
                "FROM evento e " +
                "LEFT JOIN VentaEntrada ve ON e.id = ve.id_evento";

        List<EventoEntity> eventos = jdbcTemplate.query(query, new EventoWithVentasExtractor());

        // Usar un ListItemReader para devolver la lista de eventos con ventas
        return new ListItemReader<>(eventos);
    }





    @Bean
    public ActualizarDatosProcessor processor(){
        return new ActualizarDatosProcessor();
    }

    @Bean
    public ItemWriter<EventoDto> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<EventoDto>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE evento SET precio = :precio WHERE id = :id")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public Job actualizarPreciosJob(ActualizarPreciosJobExecutionListener listener, Step step1) {
        return jobBuilderFactory
                .get("actualizarPreciosJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader<EventoEntity> reader, ItemWriter<EventoDto> writer,
                      ActualizarDatosProcessor processor) {
        return stepBuilderFactory
                .get("step1")
                .<EventoEntity, EventoDto>chunk(2)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
