package co.grap.pack.grap.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Grap/QR Manage 영역에서 공통으로 사용하는 기본 DataSource 설정.
 */
@org.springframework.context.annotation.Configuration
public class CmsDataSourceConfig {

    @Bean(name = "grapDataSourceProperties")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties grapDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "grapDataSource")
    @Primary
    public DataSource grapDataSource(
            @Qualifier("grapDataSourceProperties") DataSourceProperties dataSourceProperties
    ) {
        HikariDataSource dataSource = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("grap-hikari-pool");
        dataSource.setMaximumPoolSize(10);
        return dataSource;
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(
            @Qualifier("grapDataSource") DataSource dataSource
    ) {
        return new DataSourceTransactionManager(dataSource);
    }
}
