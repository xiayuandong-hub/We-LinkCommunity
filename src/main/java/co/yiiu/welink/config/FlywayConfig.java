package co.yiiu.welink.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Configuration
public class FlywayConfig {

    @Resource
    private DataSource dataSource;

    @PostConstruct
    public void migrate() {
        Flyway flyway = Flyway.configure().dataSource(dataSource).locations("classpath:db/migration",
                "filesystem:db/migration").baselineOnMigrate(true).load();
        //flyway.migrate();
    }

}
