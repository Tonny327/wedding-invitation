package com.wedding.invitation.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Configures DataSource from DATABASE_URL when present (e.g. on Render, Heroku).
 * Otherwise application.properties (DB_URL, DB_USERNAME, DB_PASSWORD) are used.
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "DATABASE_URL", matchIfMissing = false)
    public DataSource dataSourceFromUrl(@Value("${DATABASE_URL}") String databaseUrl) {
        URI uri = URI.create(databaseUrl.replace("postgres://", "postgresql://"));
        String[] userInfo = uri.getUserInfo() != null ? uri.getUserInfo().split(":", 2) : new String[]{"", ""};
        String username = userInfo.length > 0 ? userInfo[0] : "";
        String password = userInfo.length > 1 ? userInfo[1] : "";

        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + (uri.getPort() > 0 ? uri.getPort() : 5432)
                + uri.getPath()
                + (uri.getQuery() != null ? "?" + uri.getQuery() : "");

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
}
