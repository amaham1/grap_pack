package co.grap.pack.qrgen.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * QR Generator 서비스 MyBatis 설정
 */
@org.springframework.context.annotation.Configuration
@MapperScan(basePackages = "co.grap.pack.qrgen.**.mapper", sqlSessionFactoryRef = "qrGenSqlSessionFactory")
public class QrGenMyBatisConfig {

    /**
     * QR Generator 서비스용 SqlSessionFactory 빈 등록
     */
    @Bean(name = "qrGenSqlSessionFactory")
    public SqlSessionFactory qrGenSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // Mapper XML 위치 설정 - qrgen 서비스 전용
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/qrgen/**/*.xml"));

        // Type Alias 패키지 설정 - qrgen 서비스 전용
        sessionFactory.setTypeAliasesPackage("co.grap.pack.qrgen");

        // MyBatis Configuration 설정
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultFetchSize(100);
        configuration.setDefaultStatementTimeout(30);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}
