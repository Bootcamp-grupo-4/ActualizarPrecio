package com.capgeticket.actualizarprecios.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job actualizarPreciosJob;

    /**
     * Ejecuta el Job "actualizarPreciosJob" cada 5 minutos.
     * Se generan nuevos parámetros de trabajo con la marca de tiempo actual.
     */
    @Scheduled(fixedRate = 300000) // Ejecuta cada 5 minutos (300000 ms = 5 minutos)
    public void runActualizarPreciosJob() {
        try {
            log.info("Iniciando el Job de actualización de precios.");

            // Crear JobParameters usando la marca de tiempo actual para asegurar que el job se ejecuta cada vez
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // Ejecutar el Job y registrar el estado de la ejecución
            JobExecution execution = jobLauncher.run(actualizarPreciosJob, params);
            log.info("Estado del Job: {}", execution.getStatus());

        } catch (Exception e) {
            log.error("Error al ejecutar el Job de actualización de precios", e);
        }
    }
}
