package com.capgeticket.actualizarprecios.config.datasource;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration

public class DatasourceConfiguration {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl("jdbc:postgresql://capgeticket-db.crq4se40qnfl.eu-north-1.rds.amazonaws.com:5432/postgres");
        dataSource.setUsername("postgres");
        dataSource.setPassword("0Ze6niBb1IksyJ4RwbNB");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

