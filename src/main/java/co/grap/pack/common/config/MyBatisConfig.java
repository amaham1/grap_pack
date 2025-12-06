package co.grap.pack.common.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis 설정
 */
@org.springframework.context.annotation.Configuration
@MapperScan(basePackages = "co.grap.pack.**.mapper")
public class MyBatisConfig {

    /**
     * SqlSessionFactory 빈 등록
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // Mapper XML 위치 설정
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/**/*.xml"));

        // Type Alias 패키지 설정 (application.yml 대신 여기서 설정)
        sessionFactory.setTypeAliasesPackage("co.grap.pack");

        // MyBatis Configuration 설정
        // SqlSessionFactory를 수동으로 등록하면 application.yml의 설정이 적용되지 않으므로
        // 여기서 명시적으로 설정해야 함
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);  // snake_case를 camelCase로 자동 변환
        configuration.setDefaultFetchSize(100);
        configuration.setDefaultStatementTimeout(30);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}
