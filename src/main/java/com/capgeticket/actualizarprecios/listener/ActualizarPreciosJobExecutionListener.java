package com.capgeticket.actualizarprecios.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActualizarPreciosJobExecutionListener implements JobExecutionListener {

    /**
     * Método que se ejecuta antes de que el Job inicie.
     * Registra un log indicando el inicio del job con su ID.
     *
     * @param jobExecution la ejecución del job actual.
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("--- Starting job to update event prices with id {}", jobExecution.getId());
    }

    /**
     * Método que se ejecuta después de que el Job haya terminado.
     * Registra un log con el estado de finalización del job, indicando si fue exitoso o fallido.
     *
     * @param jobExecution la ejecución del job actual.
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("--- Job to update event prices with id {} has completed successfully", jobExecution.getId());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("--- Job to update event prices with id {} has failed", jobExecution.getId());
        }
    }
}
