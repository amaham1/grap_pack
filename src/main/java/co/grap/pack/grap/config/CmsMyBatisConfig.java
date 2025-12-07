package co.grap.pack.grap.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * Grap CMS 서비스 MyBatis 설정
 */
@org.springframework.context.annotation.Configuration
@MapperScan(basePackages = "co.grap.pack.grap.**.mapper")
public class CmsMyBatisConfig {

    /**
     * SqlSessionFactory 빈 등록
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // Mapper XML 위치 설정 - grap 서비스 전용
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/grap/**/*.xml"));

        // Type Alias 패키지 설정 - grap 서비스 전용
        sessionFactory.setTypeAliasesPackage("co.grap.pack.grap");

        // MyBatis Configuration 설정
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setDefaultFetchSize(100);
        configuration.setDefaultStatementTimeout(30);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}
